package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.AggregationResult;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class AggregationResultSerde implements Serde<AggregationResult> {

    JsonSerializer<AggregationResult> serializer = new JsonSerializer<>();
    JsonDeserializer<AggregationResult> deserializer = new JsonDeserializer<>(AggregationResult.class);
    @Override
    public Serializer<AggregationResult> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<AggregationResult> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
