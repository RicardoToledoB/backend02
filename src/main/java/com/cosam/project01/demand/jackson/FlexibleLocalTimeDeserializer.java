package com.cosam.project01.demand.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Permite que los endpoints administrativos reciban horas tanto en formato ISO string
 * ("08:00", "08:00:00") como en el formato objeto que algunos frontends construyen
 * al serializar LocalTime: {"hour":8,"minute":0,"second":0,"nano":0}.
 */
public class FlexibleLocalTimeDeserializer extends JsonDeserializer<LocalTime> {

    @Override
    public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();

        if (token == JsonToken.VALUE_NULL) {
            return null;
        }

        if (token == JsonToken.VALUE_STRING) {
            String value = parser.getText();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return parseString(value.trim(), parser, context);
        }

        if (token == JsonToken.START_OBJECT) {
            return parseObject(parser, context);
        }

        if (token == JsonToken.START_ARRAY) {
            return parseArray(parser, context);
        }

        return (LocalTime) context.handleUnexpectedToken(LocalTime.class, token, parser,
                "La hora debe venir como string HH:mm[:ss] o como objeto {hour, minute, second, nano}.");
    }

    private LocalTime parseString(String value, JsonParser parser, DeserializationContext context) throws IOException {
        try {
            return LocalTime.parse(value);
        } catch (DateTimeParseException ignored) {
            // Acepta también HH:mm:ss.SSS si Java no lo interpreta por configuración externa.
        }
        try {
            String normalized = value.length() == 5 ? value + ":00" : value;
            return LocalTime.parse(normalized);
        } catch (DateTimeParseException ex) {
            return (LocalTime) context.handleWeirdStringValue(LocalTime.class, value,
                    "Formato de hora inválido. Use HH:mm, HH:mm:ss o {hour, minute, second, nano}.");
        }
    }

    private LocalTime parseObject(JsonParser parser, DeserializationContext context) throws IOException {
        Integer hour = null;
        int minute = 0;
        int second = 0;
        int nano = 0;

        while (parser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = parser.currentName();
            JsonToken valueToken = parser.nextToken();
            if (fieldName == null) {
                parser.skipChildren();
                continue;
            }
            if (valueToken == JsonToken.VALUE_NULL) {
                continue;
            }
            switch (fieldName) {
                case "hour" -> hour = parser.getIntValue();
                case "minute" -> minute = parser.getIntValue();
                case "second" -> second = parser.getIntValue();
                case "nano" -> nano = parser.getIntValue();
                default -> parser.skipChildren();
            }
        }

        if (hour == null) {
            return (LocalTime) context.handleUnexpectedToken(LocalTime.class, JsonToken.START_OBJECT, parser,
                    "El objeto de hora debe incluir hour.");
        }

        try {
            return LocalTime.of(hour, minute, second, nano);
        } catch (DateTimeException ex) {
            return (LocalTime) context.handleWeirdStringValue(LocalTime.class,
                    "hour=" + hour + ", minute=" + minute + ", second=" + second + ", nano=" + nano,
                    "Valores de hora fuera de rango.");
        }
    }

    private LocalTime parseArray(JsonParser parser, DeserializationContext context) throws IOException {
        List<Integer> values = new ArrayList<>();
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            if (parser.currentToken().isNumeric()) {
                values.add(parser.getIntValue());
            } else if (parser.currentToken() != JsonToken.VALUE_NULL) {
                parser.skipChildren();
            }
        }
        if (values.isEmpty()) {
            return null;
        }
        int hour = values.get(0);
        int minute = values.size() > 1 ? values.get(1) : 0;
        int second = values.size() > 2 ? values.get(2) : 0;
        int nano = values.size() > 3 ? values.get(3) : 0;
        try {
            return LocalTime.of(hour, minute, second, nano);
        } catch (DateTimeException ex) {
            return (LocalTime) context.handleWeirdStringValue(LocalTime.class, values.toString(),
                    "Valores de hora fuera de rango.");
        }
    }
}
