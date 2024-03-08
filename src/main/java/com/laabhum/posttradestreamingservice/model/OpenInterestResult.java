package com.laabhum.posttradestreamingservice.model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenInterestResult {
    @JsonProperty("windowStart")
    private String windowStart;

    @JsonProperty("windowEnd")
    private String windowEnd;

    @JsonProperty("windowDuration")
    private long windowDuration;

    @JsonProperty("key")
    private String key;

    @JsonProperty("oiChange")
    private long oiChange;

    @JsonProperty("token")
    private int token;

    @JsonProperty("oldOi")
    private int oldOi;

    @JsonProperty("oldPrice")
    private Double oldPrice;

    @JsonProperty("newOi")
    private int newOi;

    @JsonProperty("newPrice")
    private Double newPrice;

    @JsonProperty("priceChange")
    private Double priceChange;

    @JsonProperty("oiInterpretation")
    private String oiInterpretation;

    @JsonProperty("oiSentiment")
    private String oiSentiment;

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

    @JsonProperty("volume")
    private int volume;
}
