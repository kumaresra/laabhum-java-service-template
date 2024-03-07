package com.laabhum.posttradestreamingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tick {

	private String windowStart;
    private String windowEnd;
    private long windowDuration;
    private String key;
    private double priceChange;
    private int token;
    private double open;
    private double close;
    private double high;
    private double low;

}

 