package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class FirstLastMessage {
    private OptionGreek firstOi;
    private OptionGreek lastOi;

    public void add(OptionGreek data) {

        if (firstOi  == null) {
            firstOi = data;
        }
        lastOi = data;
    }
}
