package com.laabhum.posttradestreamingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstrumentTick {
	@JsonProperty("tradable")
	private boolean tradable;

	@JsonProperty("mode")
	private String mode;

	@JsonProperty("instrument_token")
	private int instrumentToken;

	@JsonProperty("last_price")
	private double lastPrice;

	@JsonProperty("last_traded_quantity")
	private int lastTradedQuantity;

	@JsonProperty("average_traded_price")
	private double averageTradedPrice;

	@JsonProperty("volume_traded")
	private int volumeTraded;

	@JsonProperty("total_buy_quantity")
	private int totalBuyQuantity;

	@JsonProperty("total_sell_quantity")
	private int totalSellQuantity;

	@JsonProperty("ohlc")
	private Ohlc ohlc;

	@JsonProperty("change")
	private double change;


}
