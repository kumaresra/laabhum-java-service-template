package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.InstrumentTick;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;


public class InstrumentSerde implements Serde<InstrumentTick> {

    JsonSerializer<InstrumentTick> serializer = new JsonSerializer<>();
    JsonDeserializer<InstrumentTick> deserializer = new JsonDeserializer<>(InstrumentTick.class);
    @Override
    public Serializer<InstrumentTick> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<InstrumentTick> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
