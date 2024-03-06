package com.laabhum.posttradestreamingservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class Ohlc {

	private double open;
	private double high;
	private double low;
	private double close;

    @Data
    @AllArgsConstructor
    public static class OpenInterestResult {
        private String windowStart;
        private String windowEnd;
        private long windowDuration;
        private String key;
        private long oiDifference;
        private int token;

        private OptionGreek firstOi;
        private OptionGreek lastOi;
    }
}
