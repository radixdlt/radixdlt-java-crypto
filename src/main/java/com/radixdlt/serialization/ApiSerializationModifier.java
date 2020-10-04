package com.radixdlt.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.google.common.hash.HashCode;
import com.radixdlt.crypto.HashUtils;
import com.radixdlt.serialization.mapper.JacksonCborMapper;

import java.io.IOException;

public class ApiSerializationModifier extends BeanSerializerModifier {

    private final JacksonCborMapper hashDsonMapper;

    public ApiSerializationModifier(JacksonCborMapper hashDsonMapper) {
        this.hashDsonMapper = hashDsonMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<?> modifySerializer(
            final SerializationConfig serializationConfig,
            final BeanDescription beanDescription,
            final JsonSerializer<?> jsonSerializer) {
        return new HashGeneratingSerializer((JsonSerializer<Object>) jsonSerializer);
    }

    private class HashGeneratingSerializer extends JsonSerializer<Object> {

        private final JsonSerializer<Object> serializer;

        HashGeneratingSerializer(JsonSerializer<Object> jsonSerializer) {
            this.serializer = jsonSerializer;
        }

        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (o.getClass().isAnnotationPresent(SerializeWithHid.class)) {
                jsonGenerator.writeStartObject();
                serializer.unwrappingSerializer(null).serialize(o, jsonGenerator, serializerProvider);
                byte[] bytesToHash = hashDsonMapper.writeValueAsBytes(o);
                HashCode hash = HashUtils.sha256(bytesToHash);
                jsonGenerator.writeObjectField("hid", hash);
                jsonGenerator.writeEndObject();
            } else {
                serializer.serialize(o, jsonGenerator, serializerProvider);
            }
        }
    }
}
