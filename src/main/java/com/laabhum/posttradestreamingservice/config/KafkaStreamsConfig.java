package com.laabhum.posttradestreamingservice.config;

import java.time.*;
import java.util.Properties;

import com.laabhum.posttradestreamingservice.constants.Minutes;
import com.laabhum.posttradestreamingservice.helper.CustomMinutesWindow;
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

import static com.laabhum.posttradestreamingservice.util.Utils.getFormattedDate;
import static org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;


@Configuration
@EnableKafka
public class KafkaStreamsConfig {

	@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
	private String brokers;

	@Value("${laabhum.topic.oi.input:topic_oi_from_broker_stream}")
	private String optionGreekSourceTopic;
	
	@Value("${laabhum.topic.oi.output.prefix:topic_oi_change_diff}")
	private String openInterestOutputTopic;
	
	@Value("${laabhum.data.zone:Asia/Singapore}")	 
	private String zoneIdStr;




	@Bean
	KafkaStreams openInterestkafkaStreams1Minute() {
		return buildOiStream(Minutes.ONE);


	}
	@Bean
	KafkaStreams openInterestkafkaStreams5Minute() {
		return buildOiStream(Minutes.FIVE);
	}
	
	@Bean
	KafkaStreams openInterestkafkaStreams15Minute() {
		return buildOiStream(Minutes.FIFTEEN);


	}
	@Bean
	KafkaStreams openInterestkafkaStreams30Minute() {
		return buildOiStream(Minutes.THIRTY);


	}
	@Bean
	KafkaStreams openInterestkafkaStreams60Minute() {
		return buildOiStream(Minutes.SIXTY);
	}

	private KafkaStreams buildOiStream(Minutes minutes) {
		
		Properties props = getProperties(minutes);

		ZoneId zoneId = ZoneId.of(zoneIdStr);
		CustomMinutesWindow slidingWindow =  new CustomMinutesWindow(zoneId, minutes);// 1 minute

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, OptionGreek> openInterestStream = builder.stream(optionGreekSourceTopic, Consumed.with(Serdes.String(), new OptionGreekSerde()));
		openInterestStream
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
			Ohlc.OpenInterestResult openInterestResult = new Ohlc.OpenInterestResult(
					getFormattedDate(key.window().start(), zoneId),

					getFormattedDate(key.window().end(), zoneId),
					Duration.between(Instant.ofEpochSecond(key.window().start()),Instant.ofEpochSecond(key.window().end())).toMinutes(),
					key.key(),
					value.getLastOi().getOi() - value.getFirstOi().getOi(),
					value.getFirstOi().getToken(),
					value.getFirstOi(),
					value.getLastOi()
					);
			return KeyValue.pair(key.key(), openInterestResult);
		})
		.to(getOutputTopic(minutes), Produced.<String, Ohlc.OpenInterestResult>with(Serdes.String(), new OpenInterestResultSerde())); // Send output to another topic

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
		props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, new OpenInterestResultSerde().getClass().getName());
		
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "oi-change-diff".concat("-").concat(String.valueOf(minutes.getValue())));
		
		//props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 5);
		return props;
	}

	private String getOutputTopic(Minutes minutes) {
		
		return openInterestOutputTopic.concat("_").concat(String.valueOf(minutes.getValue()));
		
	}


}