package com.laabhum.posttradestreamingservice.util;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.Instrument;

public class InstrumentSerde implements Serde<Instrument> {

    JsonSerializer<Instrument> serializer = new JsonSerializer<>();
    JsonDeserializer<Instrument> deserializer = new JsonDeserializer<>(Instrument.class);
    @Override
    public Serializer<Instrument> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Instrument> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
