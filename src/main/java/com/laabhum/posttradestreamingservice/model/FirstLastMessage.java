package com.laabhum.posttradestreamingservice.model;

import lombok.Data;

@Data
public class FirstLastMessage {

    private GreekAndOiData firstOi;
    private GreekAndOiData lastOi;

    public void add(GreekAndOiData data) {
        if (firstOi  == null) {
            firstOi = data;
        }
        lastOi = data;
    }
}
