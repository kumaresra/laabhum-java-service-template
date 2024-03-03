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


	
	

}
