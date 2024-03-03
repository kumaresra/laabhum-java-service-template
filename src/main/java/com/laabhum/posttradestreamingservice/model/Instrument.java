package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class Instrument {
	private boolean tradable;
	private String mode;
	private int instrument_token;
	private double last_price;
	private int last_traded_quantity;
	private double average_traded_price;
	private int volume_traded;
	private int total_buy_quantity;
	private int total_sell_quantity;
	private Ohlc ohlc;
	private double change;
    
}