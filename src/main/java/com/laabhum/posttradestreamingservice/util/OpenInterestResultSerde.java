package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.Ohlc;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class OpenInterestResultSerde implements Serde<Ohlc.OpenInterestResult> {

    JsonSerializer<Ohlc.OpenInterestResult> serializer = new JsonSerializer<>();
    JsonDeserializer<Ohlc.OpenInterestResult> deserializer = new JsonDeserializer<>(Ohlc.OpenInterestResult.class);
    @Override
    public Serializer<Ohlc.OpenInterestResult> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Ohlc.OpenInterestResult> deserializer() {
        return deserializer;
    }

}
