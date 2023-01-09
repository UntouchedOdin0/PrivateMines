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
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;

public class MineTypeManager {

  private final LinkedHashMap<String, MineType> mineTypes = new LinkedHashMap<>();
//  private final TreeMap<String, MineType> mineTypeTreeMap = new TreeMap<>();
  private final PrivateMines privateMines;

  public MineTypeManager(PrivateMines privateMines) {
    this.privateMines = privateMines;
  }

  public void registerMineType(MineType mineType) {
    if (mineType == null) {
      privateMines.getLogger().info("MineType was null!");
    }
    if (mineType != null) {
//      privateMines.getLogger().info("name: " + mineType.getName());
//      mineTypes.put(mineType.getName(), mineType);
      mineTypes.put(mineType.getName(), mineType);
    }
  }

  public MineType getMineType(MineType name) {
    return mineTypes.get(name.getName());
//    return mineTypes.get(name.getName());
  }

  public MineType getMineType(String string) {
    return mineTypes.get(string);
//    return mineTypes.get(string);
  }

  public MineType getDefaultMineType() {
    if (mineTypes.isEmpty()) {
      privateMines.getLogger().info(
          "No default mine type was found!\nCreate a mine type in the mineTypes section of the config.ymlPlease ask in the discord server if you need help");
      throw new RuntimeException();
    }
    return mineTypes.entrySet().stream().findFirst().get().getValue();
  }

  public void clear() {
    mineTypes.clear();
  }

  public MineType getLast(LinkedHashMap<String, MineType> lhm)
  {
    MineType last;
    int count = 1;

    for (Map.Entry<String, MineType> it :
        lhm.entrySet()) {

      if (count == lhm.size()) {
        last = it.getValue();
        return last;
      }
      count++;
    }
    return null;
  }

  public boolean isLastMineType(MineType mineType) {
    return mineType.equals(getLast(mineTypes));
  }

  public MineType getNextType(MineType mineType) {
    TreeMap<String, MineType> treeMap = new TreeMap<>(mineTypes);
    return treeMap.higherEntry(mineType.getName()).getValue();
  }

//  public MineType getNextType(MineType mineType) {
//    return mineTypeTreeMap.higherEntry(mineType.getName()).getValue();
//  }
//    if (mineTypeTreeMap.lastEntry().getValue().equals(mineType)) {
//      return mineType;
//    }
//    return mineTypeTreeMap.higherEntry(mineType.getName()).getValue();
//  }

  public LinkedHashMap<String, MineType> getMineTypes() {
    return mineTypes;
  }

//  public TreeMap<String, MineType> getTypes() {
//    return mineTypeTreeMap;
//  }

//  public LinkedHashMap<String, MineType> getTypes() {
//    return mineTypes;
//  }

  public int getTotalMineTypes() {
    return mineTypes.size();
  }
}
