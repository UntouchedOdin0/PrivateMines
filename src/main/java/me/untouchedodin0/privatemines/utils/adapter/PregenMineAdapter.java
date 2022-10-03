package me.untouchedodin0.privatemines.utils.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import redempt.redlib.misc.LocationUtils;

import java.io.IOException;
import java.util.Objects;

public class PregenMineAdapter extends TypeAdapter<PregenMine> {

    @Override
    public PregenMine read(JsonReader reader) throws IOException {
        PregenMine pregenMine = new PregenMine();
        reader.beginObject();
        String fieldName = null;

        while (reader.hasNext()) {
            JsonToken token = reader.peek();

            if (token.equals(JsonToken.STRING)) {
                // get the current token
                fieldName = reader.nextName();
            }

            if ("location".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setLocation(LocationUtils.fromString(reader.nextString()));
            }

            if ("spawnLocation".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setSpawnLocation(LocationUtils.fromString(reader.nextString()));
            }

            if ("lowerRails".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setLowerRails(LocationUtils.fromString(reader.nextString()));
            }

            if ("upperRails".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setUpperRails(LocationUtils.fromString(reader.nextString()));
            }

            if ("fullMin".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setFullMin(LocationUtils.fromString(reader.nextString()));
            }

            if ("fullMax".equals(fieldName)) {
                token = reader.peek();
                pregenMine.setFullMax(LocationUtils.fromString(reader.nextString()));
            }
        }

        reader.endObject();
        return pregenMine;
    }

    @Override
    public void write(JsonWriter writer, PregenMine pregenMine) throws IOException {
        writer.beginObject();

        writer.name("location");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getLocation())));

        writer.name("spawnLocation");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getSpawnLocation())));

        writer.name("lowerRails");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getLowerRails())));

        writer.name("upperRails");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getUpperRails())));

        writer.name("fullMin");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getFullMin())));

        writer.name("fullMax");
        writer.value(LocationUtils.toString(Objects.requireNonNull(pregenMine.getFullMax())));

        writer.endObject();
    }
}
