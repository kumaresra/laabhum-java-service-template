package com.laabhum.posttradestreamingservice.config;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import com.laabhum.posttradestreamingservice.constants.Minutes;
import com.laabhum.posttradestreamingservice.constants.OiInterpretation;
import com.laabhum.posttradestreamingservice.helper.CustomMinutesWindow;
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
import org.springframework.kafka.annotation.EnableKafka;

import static com.laabhum.posttradestreamingservice.util.Utils.getFormattedDate;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;

@Slf4j
@Configuration
@EnableKafka
public class KafkaStreamsOpenInterestAggregatorConfig {

	@Value("${laabhum.topic.symbol.input:topic_symbol_from_broker_stream}")
	private String symbolDetailTopic;
	@Value("${laabhum.topic.price.input:topic_ticks_from_broker}")
	private String instrumentPriceInputTopic;

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String brokers;

	@Value("${laabhum.topic.oi.input:topic_greeks_from_broker}")
	private String optionGreekSourceTopic;

	@Value("${laabhum.topic.oi.output.prefix:topic_oi_change_diff}")
	private String openInterestOutputTopic;

	@Value("${laabhum.data.zone:Asia/Singapore}")	 
	private String zoneIdStr;




	@Bean
	@ConditionalOnProperty(name = "oi.1min", havingValue = "true",matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams1Minute() {
		return buildOiStream(Minutes.ONE);


	}
	@Bean
	@ConditionalOnProperty(name = "oi.3min", havingValue = "true",matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams3Minute() {
		return buildOiStream(Minutes.THREE);


	}
	@Bean
	@ConditionalOnProperty(name = "oi.5min", havingValue = "true",matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams5Minute() {
		return buildOiStream(Minutes.FIVE);
	}
	@Bean
	@ConditionalOnProperty(name = "oi.10min", havingValue = "true",matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams10Minute() {
		return buildOiStream(Minutes.TEN);
	}
	@Bean
	@ConditionalOnProperty(name = "oi.15min", havingValue = "true",matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams15Minute() {
		return buildOiStream(Minutes.FIFTEEN);


	}
	@Bean
	@ConditionalOnProperty(name = "oi.30min", havingValue = "true", matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams30Minute() {
		return buildOiStream(Minutes.THIRTY);


	}
	@Bean
	@ConditionalOnProperty(name = "oi.60min", havingValue = "true", matchIfMissing = true)
	KafkaStreams openInterestkafkaStreams60Minute() {
		return buildOiStream(Minutes.SIXTY);
	}

	private KafkaStreams buildOiStream(Minutes minutes) {

		Properties props = getProperties(minutes);

		ZoneId zoneId = ZoneId.of(zoneIdStr);
		CustomMinutesWindow slidingWindow =  new CustomMinutesWindow(zoneId, minutes);// 1 minute

		StreamsBuilder builder = new StreamsBuilder();

		KTable<String, SymbolDetail> symbolTable = builder.stream(symbolDetailTopic, Consumed.with(Serdes.String(), new SymbolListSerde()))
				.flatMapValues(a -> a)
				.selectKey((key, data) -> data.getInstrumentToken()).peek((key, data) -> log.debug("Symbol Detail key {}, data {}", key, data)).toTable(Named.as("symboldetail"),Materialized.with(Serdes.String(),new SymbolDetailSerde()));


		KStream<String, GreekAndOiData> openInterestStream = builder.stream(optionGreekSourceTopic, Consumed.with(Serdes.String(), new GreekAndOiDataListSerde()))
				.flatMapValues(Map::values)
				.selectKey((key, greekAndOiData) -> String.valueOf(greekAndOiData.getToken()));


		KStream<String, InstrumentTick> flattenedPriceStream = builder.stream(instrumentPriceInputTopic,Consumed.with(Serdes.String(), new InstrumentListSerde()))
				.flatMapValues(Map::values)
				.selectKey((key, instrument) -> String.valueOf(instrument.getInstrumentToken()));

		KTable<String, InstrumentTick> priceTable = flattenedPriceStream.groupByKey(Grouped.with(Serdes.String(), new InstrumentTickSerde()))
				.reduce((oldValue, newValue) -> newValue,Materialized.with(Serdes.String(), new InstrumentTickSerde()));

		Supplier<ValueJoiner<GreekAndOiData, InstrumentTick, GreekAndOiData>> oiAndTickJoinerSupplier = () -> (greek, price) -> {
			if (price != null) {
				greek.setLastPrice(price.getLastPrice());
				greek.setVolume(price.getVolumeTraded());
			}
			return greek;
		};
		KStream<String, GreekAndOiData> leftJoiOiAndPriceStream = openInterestStream.leftJoin(priceTable, oiAndTickJoinerSupplier.get(), Joined.with(Serdes.String(), new GreekAndOiDataSerde(), new InstrumentTickSerde()));

		KStream<String, OpenInterestResult> windowingStream = leftJoiOiAndPriceStream
				.groupByKey()
				.windowedBy(slidingWindow)
				.aggregate(
						FirstLastMessage::new, // initializer
						(key, value, aggregate) -> { // aggregator
							aggregate.add(value);
							return aggregate;
						},
						Materialized.with(Serdes.String(), new FirstLastMessageSerde())
				).suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
				.toStream()
				.map((key, value) -> {
					double priceChange = value.getLastOi().getLastPrice() - value.getFirstOi().getLastPrice();
					int oiChange = value.getLastOi().getOpenInterest() - value.getFirstOi().getOpenInterest();
					OpenInterestResult openInterestResult = new OpenInterestResult(
							getFormattedDate(key.window().start(), zoneId),
							getFormattedDate(key.window().end(), zoneId),
							String.valueOf(Duration.between(Instant.ofEpochSecond(key.window().end()), Instant.ofEpochSecond(key.window().start())).toMinutes()),
							"minutes",
							oiChange,
							value.getFirstOi().getToken(),
							value.getFirstOi().getOpenInterest(),
							value.getFirstOi().getLastPrice(),
							value.getLastOi().getOpenInterest(),
							value.getLastOi().getLastPrice(),
							priceChange,
							findOiInterpretation(priceChange, oiChange).name(),
							findOiSentiment(priceChange, oiChange),
							"", "", "", "", 0, value.getLastOi().getVolume(),
							""
					);
					return KeyValue.pair(key.key(), openInterestResult);
				});

		KStream<String, OpenInterestResult> oiAndSymbolJoinedStream = windowingStream.leftJoin(symbolTable, (openInterestResult, symbol) -> {
			if(symbol == null ){
				log.debug("Symbol detail doesnt exist {}", openInterestResult.getToken());
				return openInterestResult;
			}
			openInterestResult.setExchange(symbol.getExchange());
			openInterestResult.setName(symbol.getName());
			openInterestResult.setExpiry(symbol.getExpiry());
			openInterestResult.setStrike(symbol.getStrike());
			openInterestResult.setInstrumentType(symbol.getInstrumentType());
			openInterestResult.setSymbol(Utils.generateSymbol(symbol));
			return openInterestResult;
		}, Joined.with(Serdes.String(), new OpenInterestResultSerde(), new SymbolDetailSerde()));


		writeOutputToTopic(minutes, oiAndSymbolJoinedStream); // Send output to another topic

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
		return streams;
	}



	private void writeOutputToTopic(Minutes minutes, KStream<String, OpenInterestResult> oiAndSymbolJoinedStream) {
		oiAndSymbolJoinedStream.to(getOutputTopic(minutes), Produced.<String, OpenInterestResult>with(Serdes.String(), new OpenInterestResultSerde()));
	}

 

	private String findOiSentiment(double priceChange, int oiChange) {
		if (priceChange > 0 && oiChange > 0) {
			return "Bullish sentiment is strengthening as both price and open interest have risen.";
		} else if (priceChange > 0 && oiChange < 0) {
			return "Although the price is increasing, the decrease in open interest suggests a potential weakening of the trend as existing positions are closed.";
		} else if (priceChange < 0 && oiChange > 0) {
			return "Bearish sentiment is growing as the price falls alongside an increase in open interest, indicating new short positions.";
		} else if (priceChange < 0 && oiChange < 0) {
			return "Bearish sentiment persists as both price and open interest have declined.";
		} else if (priceChange == 0 && oiChange > 0) {
			return "Despite minimal price movement, the increase in open interest hints at a potential reversal as new positions are established.";
		} else if (priceChange == 0 && oiChange < 0) {
			return "Consolidation continues with minimal price movement and a decrease in open interest as traders unwind their positions.";
		} else {
			return "No clear interpretation can be derived from the given changes.";
		}
	}
	private OiInterpretation findOiInterpretation(double priceChange, int oiChange) {
		if (oiChange > 0) {
			if (priceChange > 0) {
				return OiInterpretation.LONG_BUILDUP;
			} else {
				return OiInterpretation.SHORT_COVERING;
			}
		} else {
			if (priceChange > 0) {
				return OiInterpretation.SHORT_BUILDUP;
			} else {
				return OiInterpretation.LONG_UNWINDING;
			}
		}
	}


	@SuppressWarnings("resource")
	private Properties getProperties(Minutes minutes) {

		Properties props = new Properties();

		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "oi-change".concat("-").concat(String.valueOf(minutes.getValue())));

		//props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 5);
		return props;
	}

	private String getOutputTopic(Minutes minutes) {

		//return openInterestOutputTopic.concat("_").concat(String.valueOf(minutes.getValue()));
		return openInterestOutputTopic;

	}


}