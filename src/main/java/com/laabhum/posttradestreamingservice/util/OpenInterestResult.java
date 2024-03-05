package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.OptionGreek;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OpenInterestResult {
    private long windowStart;
    private long windowEnd;
    private String key;
    private long oiDifference;
    private int token;

    private OptionGreek firstOi;
    private OptionGreek lastOi;
}
