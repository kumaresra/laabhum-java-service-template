package com.laabhum.posttradestreamingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laabhum.posttradestreamingservice.model.AggregationResult;

public class Utils {

    static ObjectMapper objectMapper = new ObjectMapper();
    public static String getValueAsString(AggregationResult value)   {
        try{
            return objectMapper.writeValueAsString(value);
        }catch (Exception e){
            return null;
        }
    }
}
