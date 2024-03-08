package com.laabhum.posttradestreamingservice.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.laabhum.posttradestreamingservice.model.GreekAndOiData;
import com.laabhum.posttradestreamingservice.model.InstrumentTick;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class GreekAndOiDataListSerde implements Serde<List<GreekAndOiData>> {

    JsonSerializer<List<GreekAndOiData>> serializer = new JsonSerializer<>();

    @Override
    public Serializer<List<GreekAndOiData>>  serializer() {
        return serializer;
    }

    @Override
    public Deserializer<List<GreekAndOiData>> deserializer() {
        return new Deserializer<List<GreekAndOiData>>() {

            @SneakyThrows
            @Override
            public List<GreekAndOiData> deserialize(String topic, byte[] data) {
                return getObjectMapper().readValue(data, new TypeReference<List<GreekAndOiData>>() {
                    @Override
                    public Type getType() {
                        return super.getType();
                    }
                });
            }

            private static ObjectMapper getObjectMapper() {
                ObjectMapper obj = new ObjectMapper();
                obj.registerModule(new JavaTimeModule());
                return obj;
            }
        };
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public void close() {}
}
