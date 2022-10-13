package me.untouchedodin0.privatemines.utils.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import redempt.redlib.misc.LocationUtils;

import java.io.IOException;

public class LocationAdapter extends TypeAdapter<Location> {

    @Override
    public Location read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return LocationUtils.fromString(reader.nextString());
    }

    @Override
    public void write(JsonWriter out, Location location) throws IOException {
        if (location == null) {
            out.nullValue();
            return;
        }
        out.value(LocationUtils.toString(location));
    }
}
