package com.laabhum.posttradestreamingservice.helper;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.Windows;
import org.apache.kafka.streams.kstream.internals.TimeWindow;

import com.laabhum.posttradestreamingservice.constants.Minutes;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class CustomMinutesWindow extends Windows<TimeWindow> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final ZoneId zoneId;
    private final int minutes;

    public CustomMinutesWindow(ZoneId zoneId, Minutes minutes) {
        this.zoneId = zoneId;
        this.minutes = minutes.getValue();

    }



    @Override
    public Map<Long, TimeWindow> windowsFor(long timestamp) {
        // Convert timestamp to ZonedDateTime
        ZonedDateTime incomingDateTime = Instant.ofEpochMilli(timestamp).atZone(zoneId);
        log.debug("incomingDateTime {}", incomingDateTime.format(FORMATTER));

        // Set the window start time to align with the nearest x-minute interval
        int multiplesOf = this.minutes;
        ZonedDateTime windowStartTime = incomingDateTime.truncatedTo(ChronoUnit.HOURS) // Start of the hour
                .plusMinutes((long) (incomingDateTime.getMinute() / multiplesOf) * multiplesOf);
        log.debug("windowStartTime {}", windowStartTime.format(FORMATTER));

        ZonedDateTime windowEndTime = windowStartTime.plusMinutes(this.minutes);
        log.debug("windowEndTime {}", windowEndTime.format(FORMATTER));
        // Create the window
        Map<Long, TimeWindow> windows = new HashMap<>();
        windows.put(toEpochMilli(windowStartTime), new TimeWindow(toEpochMilli(windowStartTime), toEpochMilli(windowEndTime)));
        return windows;
    }

    @Override
    public long size() {
        return Duration.ofMinutes(this.minutes).toMillis();
    }

    @Override
    public long gracePeriodMs() {
        return 5000;
    }

    private long toEpochMilli(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }
}
