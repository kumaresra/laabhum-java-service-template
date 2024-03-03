package com.laabhum.posttradestreamingservice.config;

import java.time.Duration;
import java.time.Instant;
import java.util.Properties;
import java.util.Random;

import com.laabhum.posttradestreamingservice.model.AggregationResult;
import com.laabhum.posttradestreamingservice.model.Instrument;
import com.laabhum.posttradestreamingservice.model.Ohlc;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.laabhum.posttradestreamingservice.util.Utils.getValueAsString;


@Configuration
@EnableKafka
public class KafkaConfig {

	public static final int WINDOW_RANGE = 30;

	@Value("${laabhum.kafka.brokers:localhost:9092}")
	private String brokers;

	@Value("${laabhum.topic.ticks.input:topic_ticks_from_broker}")
	private String ticksSourceTopic;

	@Value("${laabhum.topic.oi.output:open_interest_difference_topic}")
	private String openInterestOutputTopic;


	@Bean
	KafkaStreams openInterestkafkaStreams() {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "open-interest-change");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		ObjectMapper objectMapper = new ObjectMapper();
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> openInterestStream = builder.stream(ticksSourceTopic, Consumed.with(Serdes.String(), Serdes.String()));

	 openInterestStream
		.groupByKey()
		.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(WINDOW_RANGE)))
		.aggregate(
                AggregationResult::new,
				(String key, String newValue, AggregationResult aggregate) ->  {
					try {
						Instrument instrument = objectMapper.readValue(newValue, Instrument.class);
						double last_price = instrument.getLast_price();
						double difference = last_price - aggregate.getDifference();
						int instrumentToken = instrument.getInstrument_token();
						Ohlc ohlc = instrument.getOhlc();

						return new AggregationResult(WINDOW_RANGE,difference, Instant.now(), instrumentToken, last_price, ohlc);
					} catch (Exception e) {
						return new AggregationResult(WINDOW_RANGE,0.0, Instant.now(), 0, 0.0, null);
					}
				}, // aggregator
				Materialized.as("open_interest_difference") // store name
				)
		.toStream()
		.map((Windowed<String> key, AggregationResult value) -> KeyValue.pair(key.key(), getValueAsString(value)))
		.to(openInterestOutputTopic, Produced.with(Serdes.String(), Serdes.String()));

		KafkaStreams streams = new KafkaStreams(builder.build(), props);
		streams.start();
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

		return streams;
	}




}