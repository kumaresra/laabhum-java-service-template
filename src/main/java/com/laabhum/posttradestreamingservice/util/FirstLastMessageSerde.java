package com.laabhum.posttradestreamingservice.util;

import com.laabhum.posttradestreamingservice.model.FirstLastMessage;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import com.laabhum.posttradestreamingservice.model.FirstLastMessage;

public class FirstLastMessageSerde implements Serde<FirstLastMessage> {

    JsonSerializer<FirstLastMessage> serializer = new JsonSerializer<>();
    JsonDeserializer<FirstLastMessage> deserializer = new JsonDeserializer<>(FirstLastMessage.class);
    @Override
    public Serializer<FirstLastMessage> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<FirstLastMessage> deserializer() {
        return deserializer;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
