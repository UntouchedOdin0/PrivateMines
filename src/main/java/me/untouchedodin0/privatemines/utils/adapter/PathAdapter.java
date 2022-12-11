package me.untouchedodin0.privatemines.utils.adapter;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathAdapter implements JsonDeserializer<Path>, JsonSerializer<Path> {
    @Override
    public Path deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return Paths.get(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(Path path, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(path.toString());
    }
}