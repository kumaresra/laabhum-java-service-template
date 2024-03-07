package com.laabhum.posttradestreamingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laabhum.posttradestreamingservice.model.AggregationResult;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Utils {

	static ObjectMapper objectMapper = new ObjectMapper();
	public static String getValueAsString(AggregationResult value)   {
		try{
			return objectMapper.writeValueAsString(value);
		}catch (Exception e){
			return null;
		}
	}

	public static String getFormattedDate(long timeinmillis, ZoneId zoneId) {
		return Instant.ofEpochMilli(timeinmillis).atZone(zoneId)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
