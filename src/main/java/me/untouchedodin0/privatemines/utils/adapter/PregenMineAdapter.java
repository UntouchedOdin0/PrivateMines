/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines.utils.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Objects;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import redempt.redlib.misc.LocationUtils;

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
