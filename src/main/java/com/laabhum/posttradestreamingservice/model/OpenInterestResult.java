package com.laabhum.posttradestreamingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenInterestResult {
    private String windowStart;
    private String windowEnd;
    private long windowDuration;
    private String key;
    private long oiChange;
    private int token;
    private int oldOi;
    private Double oldPrice;
    private int newOi;
    private Double newPrice;
    private Double priceChange;
    private String oiInterpretation;
    private String oiSentiment;
}