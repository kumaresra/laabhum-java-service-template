package com.laabhum.posttradestreamingservice.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class FirstLastMessage {

    private GreekAndOiData firstOi;
    private GreekAndOiData lastOi;

    public void add(GreekAndOiData data) {
        if (firstOi  == null) {
            log.info("firstoi added");
            firstOi = data;
        }
        lastOi = data;
    }
}
