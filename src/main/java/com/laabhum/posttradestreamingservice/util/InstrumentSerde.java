package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.AggregationResult;
import com.laabhum.posttradestreamingservice.model.Instrument;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

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
