package org.projectweather.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;


@Configuration
public class DateTimeConfig {

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

    private final JsonSerializer<ZoneId> jsonSerializerZoneId = new JsonSerializer<>() {
        @Override
        public void serialize(ZoneId zoneId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(zoneId.getId());
        }
    };

    private final JsonSerializer<Instant> jsonSerializerInstant = new JsonSerializer<>() {
        @Override
        public void serialize(Instant instant, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(instant.toString());
        }
    };


    private final JsonDeserializer<ZonedDateTime> jsonDeserializerZonedDateTime = new JsonDeserializer<>() {
        @Override
        public ZonedDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String text = p.getValueAsString();
            return ZonedDateTime.parse(text, formatterZonedDateTime);
        }
    };

    private final JsonDeserializer<LocalDateTime> jsonDeserializerLocalDateTime = new JsonDeserializer<>() {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String text = p.getValueAsString();
            return LocalDateTime.parse(text, formatterLocalDateTime);
        }
    };

    private final JsonDeserializer<ZoneId> jsonDeserializerZoneId = new JsonDeserializer<>() {
        @Override
        public ZoneId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            String zoneIdStr = jsonParser.getText();
            try {
                return ZoneId.of(zoneIdStr);
            } catch (DateTimeException e) {
                throw new InvalidFormatException(jsonParser, "Invalid ZoneId: " + zoneIdStr, zoneIdStr, ZoneId.class);
            }
        }
    };

    private final JsonDeserializer<Instant> jsonDeserializerInstant = new JsonDeserializer<>() {
        @Override
        public Instant deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            long instantStr = Long.parseLong(jsonParser.getText());
            return Instant.ofEpochSecond(instantStr);
        }
    };

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(ZonedDateTime.class, jsonSerializerZonedDateTime)
                .addSerializer(LocalDateTime.class, jsonSerializerLocalDateTime)
                .addSerializer(ZoneId.class, jsonSerializerZoneId)
                .addSerializer(Instant.class, jsonSerializerInstant)
                .addDeserializer(ZonedDateTime.class, jsonDeserializerZonedDateTime)
                .addDeserializer(LocalDateTime.class, jsonDeserializerLocalDateTime)
                .addDeserializer(ZoneId.class, jsonDeserializerZoneId)
                .addDeserializer(Instant.class, jsonDeserializerInstant));
        return objectMapper;
    }

}
