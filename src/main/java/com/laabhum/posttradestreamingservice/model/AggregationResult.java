package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
public class AggregationResult  {
    private long timeFrame;
    private double diff;
    private String windowStartTime;
    private int instrumentToken;
    private double lastPrice;
    private Ohlc ohlc;


    public AggregationResult(long timeFrame, double diff, String windowStartTime, int instrumentToken, double lastPrice, Ohlc ohlc) {
        this.diff = diff;
        this.windowStartTime = windowStartTime;
        this.instrumentToken = instrumentToken;
        this.lastPrice = lastPrice;
        this.ohlc = ohlc;
        this.timeFrame = timeFrame;

    }



    public AggregationResult(){
         this.setDiff(0.0);


    }

}
