package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public  class GreeksWithIV {
    private double theta;
    private double delta;
    private double gamma;
    private double vega;
    private double iv;
}