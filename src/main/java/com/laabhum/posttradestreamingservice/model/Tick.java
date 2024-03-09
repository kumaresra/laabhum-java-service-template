package com.laabhum.posttradestreamingservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tick {
    @JsonProperty("window_start")
    private String windowStart;

    @JsonProperty("window_end")
    private String windowEnd;

    @JsonProperty("windowInterval")
    private String windowInterval;

    @JsonProperty("windowType")
    private String windowType;



    @JsonProperty("price_change")
    private double priceChange;

    @JsonProperty("token")
    private int token;

    @JsonProperty("open")
    private double open;

    @JsonProperty("close")
    private double close;

    @JsonProperty("high")
    private double high;

    @JsonProperty("low")
    private double low;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("expiry")
    private String expiry;

    @JsonProperty("instrumentType")
    private String instrumentType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("strike")
    private int strike;
    @JsonProperty("symbol")
    private String symbol;
}
