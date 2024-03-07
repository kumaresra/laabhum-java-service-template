package com.laabhum.posttradestreamingservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionGreek {
    private int token;
    private int oi;
}
