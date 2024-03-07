package com.laabhum.posttradestreamingservice.util;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.OpenInterestResult;

public class OpenInterestResultSerde implements Serde<OpenInterestResult> {

    JsonSerializer<OpenInterestResult> serializer = new JsonSerializer<>();
    JsonDeserializer<OpenInterestResult> deserializer = new JsonDeserializer<>(OpenInterestResult.class);
    @Override
    public Serializer<OpenInterestResult> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<OpenInterestResult> deserializer() {
        return deserializer;
    }

}
