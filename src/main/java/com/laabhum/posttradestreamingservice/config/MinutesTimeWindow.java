package com.laabhum.posttradestreamingservice.config;



import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Windows;
import org.apache.kafka.streams.kstream.internals.TimeWindow;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
@Slf4j
//Implementation of a daily custom window starting at given hour (like daily windows starting at 6pm) with a given timezone
public class MinutesTimeWindow extends Windows<TimeWindow> {

    private final ZoneId zoneId;
    private final long grace;
    private final int startHour;

    public MinutesTimeWindow(final ZoneId zoneId, final int startHour, final Duration grace) {
        this.zoneId = zoneId;
        this.grace = grace.toMillis();
        this.startHour = startHour;
    }

    @Override
    public Map<Long, TimeWindow> windowsFor(final long timestamp) {

        final Instant instant = Instant.ofEpochMilli(timestamp);

        final ZonedDateTime zonedDateTime = instant.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);
        log.info("incoming ts {}", formatter.format(zonedDateTime));
        final ZonedDateTime startTime = zonedDateTime.getHour() >= startHour ? zonedDateTime.truncatedTo(ChronoUnit.DAYS).withHour(startHour) : zonedDateTime.truncatedTo(ChronoUnit.DAYS).minusDays(1).withHour(startHour);
        final ZonedDateTime endTime = startTime.plusMinutes(50);
        log.info("Start time {}, end time {}", formatter.format(startTime), formatter.format(endTime));
        final Map<Long, TimeWindow> windows = new LinkedHashMap<>();
        windows.put(toEpochMilli(startTime), new TimeWindow(toEpochMilli(startTime), toEpochMilli(endTime)));
        return windows;
    }

    @Override
    public long size() {
        return Duration.ofMinutes(50).toMillis();
    }

    @Override
    public long gracePeriodMs() {
        return grace;
    }

    private long toEpochMilli(final ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }
}