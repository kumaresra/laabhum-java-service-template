package com.laabhum.posttradestreamingservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
public class AggregationResult  {
    private long timeFrame;
    private double difference;
    private Instant windowStartTime;
    private int instrumentToken;
    private double lastPrice;
    private Ohlc ohlc;

    public AggregationResult(long timeFrame,double difference, Instant windowStartTime, int instrumentToken, double lastPrice, Ohlc ohlc) {
        this.difference = difference;
        this.windowStartTime = windowStartTime;
        this.instrumentToken = instrumentToken;
        this.lastPrice = lastPrice;
        this.ohlc = ohlc;
        this.timeFrame = timeFrame;
    }



    public AggregationResult(){
         this.setDifference(0.0);

    }

}
