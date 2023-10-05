package org.projectweather.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ZonedDateTimeConfig {

    private final DateTimeFormatter formatterZonedDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss 'UTC'XXX");
    private final DateTimeFormatter formatterLocalDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JsonSerializer<ZonedDateTime> jsonSerializerZonedDateTime = new JsonSerializer<>() {
        @Override
        public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(formatterZonedDateTime.format(value));
        }
    };

    private final JsonSerializer<LocalDateTime> jsonSerializerLocalDateTime = new JsonSerializer<>() {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(formatterLocalDateTime.format(value));
        }
    };

    private final JsonDeserializer<ZonedDateTime> jsonDeserializerZonedDateTime = new JsonDeserializer<>(){
        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String text = p.getValueAsString();
            return ZonedDateTime.parse(text, formatterZonedDateTime);
        }
    };

    private final JsonDeserializer<LocalDateTime> jsonDeserializerLocalDateTime = new JsonDeserializer<>(){
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            return LocalDateTime.parse(text, formatterLocalDateTime);
        }
    };

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(ZonedDateTime.class, jsonSerializerZonedDateTime)
                        .addSerializer(LocalDateTime.class, jsonSerializerLocalDateTime)
                .addDeserializer(ZonedDateTime.class, jsonDeserializerZonedDateTime)
                .addDeserializer(LocalDateTime.class, jsonDeserializerLocalDateTime));
        return objectMapper;
    }

}
