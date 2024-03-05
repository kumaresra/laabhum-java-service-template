package com.laabhum.posttradestreamingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionGreek {
    private int token;
    private int oi;



}
