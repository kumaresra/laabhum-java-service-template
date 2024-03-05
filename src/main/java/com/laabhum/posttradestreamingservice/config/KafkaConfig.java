package com.laabhum.posttradestreamingservice.config;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Properties;

import com.laabhum.posttradestreamingservice.model.*;
import com.laabhum.posttradestreamingservice.util.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;


@Configuration
@EnableKafka
public class KafkaConfig {

	public static final int WINDOW_RANGE = 60;

	@Value("${laabhum.kafka.brokers:localhost:9092}")
	private String brokers;

	@Value("${laabhum.topic.ticks.input:topic_ticks_from_broker}")
	private String ticksSourceTopic;
	@Value("${laabhum.topic.greeks.input:topic_greeks_from_broker_stream}")
	private String optionGreekSourceTopic;
	@Value("${laabhum.topic.oi.output:open_interest_difference_topic}")
	private String openInterestOutputTopic;
	@Value("${laabhum.topic.tick.output:tick_difference_topic}")
	private String tickOutputTopic;


//	@Bean
//	KafkaStreams tickKafkaStreams() {
//		Properties props = new Properties();
//		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "tick-change");
//		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
//		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//
//		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
//		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, new AggregationResultSerde().getClass().getName());
//
//		StreamsBuilder builder = new StreamsBuilder();
//		KStream<String, Instrument> openInterestStream = builder.stream(ticksSourceTopic, Consumed.with(Serdes.String(), new InstrumentSerde()));
//
//
//		ZoneId mumbaiZone = ZoneId.of("Asia/Kolkata");
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
//
//	 openInterestStream
//		.groupByKey()
//		.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(WINDOW_RANGE)))
//		.aggregate(
//                AggregationResult::new,
//				(String key, Instrument instrument, AggregationResult aggregate) ->  {
//
//
//						double last_price = instrument.getLast_price();
//						double diff = last_price - aggregate.getDiff();
//						int instrumentToken = instrument.getInstrument_token();
//						Ohlc ohlc = instrument.getOhlc();
//
//						return new AggregationResult(WINDOW_RANGE,diff, ZonedDateTime.of(LocalDateTime.now(), mumbaiZone).format(formatter), instrumentToken, last_price, ohlc);
//
//				}, // aggregator
//				Materialized.as("open_interest_mat") // store name
//				)
//		.toStream()
//		.map((Windowed<String> key, AggregationResult value) -> KeyValue.pair(key.key(), value))
//		.to(tickOutputTopic, Produced.with(Serdes.String(), new AggregationResultSerde()));
//
//		KafkaStreams streams = new KafkaStreams(builder.build(), props);
//		streams.start();
//		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
//
//		return streams;
//	}



	@Bean
	KafkaStreams openInterestkafkaStreams() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "open-interest-change1");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, new OpenInterestResultSerde().getClass().getName());
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5* 60* 1000); // Set commit interval to 10 seconds

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, OptionGreek> openInterestStream = builder.stream(optionGreekSourceTopic, Consumed.with(Serdes.String(), new OptionGreekSerde()));


		ZoneId mumbaiZone = ZoneId.of("Asia/Kolkata");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
		TimeWindows slidingWindow = TimeWindows.of(Duration.ofMinutes(5)) ;// 5 minutes

		openInterestStream

				.filter((a,b)-> b.getToken() == 10071810)
				.peek((a,b)-> System.out.println(b.getOi()))
				.groupByKey()
				.windowedBy(slidingWindow)
				.aggregate(
                        FirstLastMessage::new, // initializer
						(key, value, aggregate) -> { // aggregator
							aggregate.add(value);
							return aggregate;
						},
						Materialized.with(Serdes.String(), new FirstLastMessageSerde())
				)
				.toStream()
				.map((key, value) -> {
					OpenInterestResult openInterestResult = new OpenInterestResult(
							key.window().start(),
							key.window().end(),
							key.key(),
							value.getLastOi().getOi() - value.getFirstOi().getOi(),
							value.getFirstOi().getToken(),
							value.getFirstOi(),
							value.getLastOi()
					);
					return KeyValue.pair(key.key(), openInterestResult);
				})
				.to(openInterestOutputTopic, Produced.<String, OpenInterestResult>with(Serdes.String(), new OpenInterestResultSerde())); // Send output to another topic

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

		return streams;
	}


}