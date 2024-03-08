package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.GreekAndOiData;
import com.laabhum.posttradestreamingservice.model.InstrumentTick;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class GreekAndOiDataSerde implements Serde<GreekAndOiData> {

    JsonSerializer<GreekAndOiData> serializer = new JsonSerializer<>();
    JsonDeserializer<GreekAndOiData> deserializer = new JsonDeserializer<>(GreekAndOiData.class);
    @Override
    public Serializer<GreekAndOiData> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<GreekAndOiData> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
