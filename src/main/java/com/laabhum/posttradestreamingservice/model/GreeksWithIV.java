package com.laabhum.posttradestreamingservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class GreeksWithIV {

    @JsonProperty("theta")
    private double theta;

    @JsonProperty("delta")
    private double delta;

    @JsonProperty("gamma")
    private double gamma;

    @JsonProperty("vega")
    private double vega;

    @JsonProperty("iv")
    private double iv;

}