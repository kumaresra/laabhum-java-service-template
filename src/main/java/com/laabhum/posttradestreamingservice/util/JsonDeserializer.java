package com.laabhum.posttradestreamingservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    ObjectMapper objectMapper = getObjectMapper();
    private static ObjectMapper getObjectMapper() {
        ObjectMapper obj = new ObjectMapper();
        obj.registerModule(new JavaTimeModule());
        return obj;
    }
    private Class<T> destinationClass;
    private Type reflectionTypeToken;

    public JsonDeserializer(Class<T> destinationClass) {
        this.destinationClass = destinationClass;
    }

    public JsonDeserializer(Type reflectionTypeToken) {
        this.reflectionTypeToken = reflectionTypeToken;
    }

    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        // nothing to do
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        try {

            return objectMapper.readValue(bytes, destinationClass);

        } catch (Exception e) {
            throw new SerializationException("Error deserializing message", e);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}