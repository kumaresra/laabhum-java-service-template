package com.laabhum.posttradestreamingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class PostTradeStreamingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PostTradeStreamingServiceApplication.class, args);
	}

	@KafkaListener(topics = "open_interest_difference_topic")
	public void processMessage(String message) {

		log.info("message receoved");
		 //System.out.println("Received message from input-topic:" + message);
		 
		// Your processing logic here
	}
	
	

}
