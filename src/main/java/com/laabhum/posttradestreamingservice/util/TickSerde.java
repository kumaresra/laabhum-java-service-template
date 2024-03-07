package com.laabhum.posttradestreamingservice.util;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.Tick;

public class TickSerde implements Serde<Tick> {

    JsonSerializer<Tick> serializer = new JsonSerializer<>();
    JsonDeserializer<Tick> deserializer = new JsonDeserializer<>(Tick.class);
    @Override
    public Serializer<Tick> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Tick> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
