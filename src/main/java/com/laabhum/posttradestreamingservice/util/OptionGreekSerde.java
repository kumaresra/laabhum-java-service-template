package com.laabhum.posttradestreamingservice.util;

import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.OptionGreek;

public class OptionGreekSerde implements Serde<OptionGreek> {

    JsonSerializer<OptionGreek> serializer = new JsonSerializer<>();
    JsonDeserializer<OptionGreek> deserializer = new JsonDeserializer<>(OptionGreek.class);
    @Override
    public Serializer<OptionGreek> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<OptionGreek> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
