package com.laabhum.posttradestreamingservice.config;

import static com.laabhum.posttradestreamingservice.util.Utils.getFormattedDate;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;

import com.laabhum.posttradestreamingservice.model.*;
import com.laabhum.posttradestreamingservice.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.laabhum.posttradestreamingservice.constants.Minutes;
import com.laabhum.posttradestreamingservice.helper.CustomMinutesWindow;

@Configuration
@Slf4j
public class KafkaStreamsPriceAggregatorConfig {
	@Value("${laabhum.topic.symbol.input:topic_symbol_from_broker_stream}")
	private String symbolDetailTopic;

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String brokers;

	@Value("${laabhum.topic.price.input:topic_price_from_broker_stream}")
	private String instrumentPriceInputTopic;

	@Value("${laabhum.topic.price.output.prefix:topic_price_change_diff}")
	private String openInterestOutputTopic;

	@Value("${laabhum.data.zone:Asia/Singapore}")	 
	private String zoneIdStr;




	@Bean
		@ConditionalOnProperty(name = "tick.1min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams1Minute() {
		return buildPriceStream(Minutes.ONE);


	}

	@Bean
		@ConditionalOnProperty(name = "tick.3min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams3Minute() {
		return buildPriceStream(Minutes.THREE);


	}
	@Bean
		@ConditionalOnProperty(name = "tick.5min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams5Minute() {
		return buildPriceStream(Minutes.FIVE);
	}

	@Bean
		@ConditionalOnProperty(name = "tick.15min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams15Minute() {
		return buildPriceStream(Minutes.FIFTEEN);


	}
	@Bean
		@ConditionalOnProperty(name = "tick.30min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams30Minute() {
		return buildPriceStream(Minutes.THIRTY);


	}
	@Bean
		@ConditionalOnProperty(name = "tick.60min", havingValue = "true",matchIfMissing = true)
	KafkaStreams tickAggKafkaStreams60Minute() {
		return buildPriceStream(Minutes.SIXTY);
	}

	private KafkaStreams buildPriceStream(Minutes minutes) {

		Properties props = getProperties(minutes);

		ZoneId zoneId = ZoneId.of(zoneIdStr);
		StreamsBuilder builder = new StreamsBuilder();
		CustomMinutesWindow slidingWindow =  new CustomMinutesWindow(zoneId, minutes);// 1 minute
		KTable<String, SymbolDetail> symbolTable = builder.stream(symbolDetailTopic, Consumed.with(Serdes.String(), new SymbolListSerde()))
				.flatMapValues(a -> a)
				.selectKey((key, data) -> data.getInstrumentToken()).peek((key, data) -> log.info("Symbol Detail key {}, data {}", key, data)).toTable(Named.as("symboldetail"),Materialized.with(Serdes.String(),new SymbolDetailSerde()));


		KStream<String, InstrumentTick> priceAggregateStream = builder.stream(instrumentPriceInputTopic,Consumed.with(Serdes.String(), new InstrumentListSerde()))
				.flatMapValues(Map::values)
				.selectKey((key, instrument) -> String.valueOf(instrument.getInstrumentToken()), Named.as("instrument"));
		KStream<String, Tick> windowingStream = priceAggregateStream
				.groupByKey(Grouped.with(Serdes.String(), new InstrumentTickSerde()))
				.windowedBy(slidingWindow)
				.aggregate(
						InstrumentOhlc::new, // initializer
						(key, value, aggregate) -> { // aggregator
							aggregate.add(value);
							return aggregate;
						},
						Materialized.with(Serdes.String(), new InstrumentOhlcSerde())
				).suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
				.toStream()
				.map((key, value) -> {
					Tick openInterestResult = new Tick(
							getFormattedDate(key.window().start(), zoneId),
							getFormattedDate(key.window().end(), zoneId),
							Duration.between(Instant.ofEpochSecond(key.window().start()), Instant.ofEpochSecond(key.window().end())).toMinutes(),
							key.key(),
							value.getCloseInstrumentTick().getLastPrice() - value.getOpenInstrumentTick().getLastPrice(),
							value.getOpenInstrumentTick().getInstrumentToken(),
							value.getOpenInstrumentTick().getLastPrice(),
							value.getCloseInstrumentTick().getLastPrice(),
							value.getMinInstrumentTick().getLastPrice(),
							value.getMaxInstrumentTick().getLastPrice(),
							"", "", "", "", 0,""
					);
					return KeyValue.pair(key.key(), openInterestResult);
				});

		KStream<String, Tick> tickAndSymbolJoinedStream = windowingStream.leftJoin(symbolTable, (tick, symbol) -> {
			if (symbol == null) {
				log.info("Symbol detail doesnt exist {}", tick.getKey());
				return tick;
			}
			tick.setExchange(symbol.getExchange());
			tick.setName(symbol.getName());
			tick.setExpiry(symbol.getExpiry());
			tick.setStrike(symbol.getStrike());
			tick.setInstrumentType(symbol.getInstrumentType());
			tick.setSymbol(Utils.generateSymbol(symbol));
			return tick;
		}, Joined.with(Serdes.String(), new TickSerde(), new SymbolDetailSerde()));

		tickAndSymbolJoinedStream.to(getOutputTopic(minutes), Produced.<String, Tick>with(Serdes.String(), new TickSerde())); // Send output to another topic

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		return streams;
	}

	@SuppressWarnings("resource")
	private Properties getProperties(Minutes minutes) {

		Properties props = new Properties();

		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "tickprice-change".concat("-").concat(String.valueOf(minutes.getValue())));

		//props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 5);
		return props;
	}

	private String getOutputTopic(Minutes minutes) {

		//return openInterestOutputTopic.concat("_").concat(String.valueOf(minutes.getValue()));
		return openInterestOutputTopic;

	}


}