package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class InstrumentOhlc {

	private Instrument openInstrument;
	private Instrument closeInstrument;
	private Instrument minInstrument;
	private Instrument maxInstrument;


	public void add(Instrument data) {
		if (openInstrument == null) {
			openInstrument = data;
			minInstrument = data;
			maxInstrument = data;    
		}
		closeInstrument = data;
		minInstrument= data.getLast_price()< minInstrument.getLast_price()? data:minInstrument;
		maxInstrument= data.getLast_price()> maxInstrument.getLast_price()? data:maxInstrument;
	}
}
