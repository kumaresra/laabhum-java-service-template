package com.laabhum.posttradestreamingservice.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SymbolDetail {


    @JsonProperty("instrument_token")
    private long instrumentToken;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("exchange_token")
    private int exchangeToken;

    @JsonProperty("expiry")
    private String expiry;


    @JsonProperty("instrument_type")
    private String instrumentType;


    @JsonProperty("name")
    private String name;

    @JsonProperty("segment")
    private String segment;

    @JsonProperty("strike")
    private int strike;


}
