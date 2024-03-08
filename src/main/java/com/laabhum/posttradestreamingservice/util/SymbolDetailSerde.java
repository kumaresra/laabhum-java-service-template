package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.SymbolDetail;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class SymbolDetailSerde implements Serde<SymbolDetail> {

    JsonSerializer<SymbolDetail> serializer = new JsonSerializer<>();
    JsonDeserializer<SymbolDetail> deserializer = new JsonDeserializer<>(SymbolDetail.class);
    @Override
    public Serializer<SymbolDetail> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<SymbolDetail> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
