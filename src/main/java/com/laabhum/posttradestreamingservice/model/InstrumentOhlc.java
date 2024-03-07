package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class InstrumentOhlc {

	private Instrument firstInstrument;
	private Instrument lastInstrument;
	private Instrument minInstrument;
	private Instrument maxInstrument;


	public void add(Instrument data) {
		if (firstInstrument  == null) {
			firstInstrument = data;
			minInstrument = data;
			maxInstrument = data;    
		}
		lastInstrument = data;
		minInstrument= data.getLast_price()< minInstrument.getLast_price()? data:minInstrument;
		maxInstrument= data.getLast_price()> maxInstrument.getLast_price()? data:maxInstrument;
	}
}
