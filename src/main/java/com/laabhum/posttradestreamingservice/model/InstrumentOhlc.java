package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class InstrumentOhlc {

	private InstrumentTick openInstrumentTick;
	private InstrumentTick closeInstrumentTick;
	private InstrumentTick minInstrumentTick;
	private InstrumentTick maxInstrumentTick;


	public void add(InstrumentTick data) {
		if (openInstrumentTick == null) {
			openInstrumentTick = data;
			minInstrumentTick = data;
			maxInstrumentTick = data;
		}
		closeInstrumentTick = data;
		minInstrumentTick = data.getLastPrice()< minInstrumentTick.getLastPrice()? data: minInstrumentTick;
		maxInstrumentTick = data.getLastPrice()> maxInstrumentTick.getLastPrice()? data: maxInstrumentTick;
	}
}
