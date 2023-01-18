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

package me.untouchedodin0.privatemines.mine;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;

public class MineTypeManager {

  private final LinkedHashMap<String, MineType> mineTypes = new LinkedHashMap<>();
  private final PrivateMines privateMines;

  public MineTypeManager(PrivateMines privateMines) {
    this.privateMines = privateMines;
  }

  public void registerMineType(MineType mineType) {
    if (mineType == null) {
      privateMines.getLogger().info("MineType was null!");
    }
    if (mineType != null) {
      mineTypes.put(mineType.getName(), mineType);
    }
  }

  public MineType getMineType(MineType name) {
    return mineTypes.get(name.getName());
  }

  public MineType getMineType(String string) {
    return mineTypes.get(string);
  }

  public MineType getDefaultMineType() {
    if (mineTypes.isEmpty()) {
      privateMines.getLogger().info(
          "No default mine type was found!\nCreate a mine type in the mineTypes section of the config.yml\nPlease ask in the discord server if you need help");
      throw new RuntimeException();
    }
    return mineTypes.entrySet().stream().findFirst().get().getValue();
  }

  public MineType getNextMineType(MineType current) {
    Iterator<Map.Entry<String, MineType>> iterator = mineTypes.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry<String, MineType> entry = iterator.next();
      if (entry.getValue().equals(current)) {
        if (iterator.hasNext()) {
          return iterator.next().getValue();
        } else {
          return current;
        }
      }
    }
    return null;
  }
}
