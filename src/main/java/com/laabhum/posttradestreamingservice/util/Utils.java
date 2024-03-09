package com.laabhum.posttradestreamingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laabhum.posttradestreamingservice.model.AggregationResult;
import com.laabhum.posttradestreamingservice.model.SymbolDetail;
import io.netty.util.internal.StringUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

	public static String generateSymbol(SymbolDetail symbol) {
		return symbol.getExchange() + symbol.getName() + convertExpiryString(symbol.getExpiry() )+ symbol.getStrike() + symbol.getInstrumentType();
	}

	
	public static String convertExpiryString(String input) {


		try{
			// Parse input string as Instant
			Instant instant = Instant.parse(input);

			// Convert Instant to LocalDateTime
			LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);

			// Format LocalDateTime to desired string format
			return dateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

			// Return the formatted date
		}catch (Exception e){
			return "";
		}
	}

}
