package com.laabhum.posttradestreamingservice.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
@Data
public class GreekAndOiData {

    @JsonProperty("token")
    private int token;

    @JsonProperty("last_price")
    private double lastPrice;

    @JsonProperty("ltp_change")
    private double ltpChange;

    @JsonProperty("last_trade_time")
    private String lastTradeTime;

    @JsonProperty("oi")
    private int openInterest;

    @JsonProperty("oi_change")
    private int oiChange;

    @JsonProperty("volume")
    private int volume;

    @JsonProperty("is_liquid")
    private boolean isLiquid;

    @JsonProperty("liquidity_warnings")
    private List<String> liquidityWarnings;

    @JsonProperty("greeks_with_iv")
    private GreeksWithIV greeksWithIV;



}
