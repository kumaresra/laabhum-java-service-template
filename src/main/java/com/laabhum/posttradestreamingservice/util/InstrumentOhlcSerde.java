package com.laabhum.posttradestreamingservice.util;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.InstrumentOhlc;

public class InstrumentOhlcSerde implements Serde<InstrumentOhlc> {

    JsonSerializer<InstrumentOhlc> serializer = new JsonSerializer<>();
    JsonDeserializer<InstrumentOhlc> deserializer = new JsonDeserializer<>(InstrumentOhlc.class);
    @Override
    public Serializer<InstrumentOhlc> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<InstrumentOhlc> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
