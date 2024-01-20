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

package me.untouchedodin0.privatemines.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sk89q.worldedit.math.BlockVector3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.WordUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class Utils {

  public static Location toLocation(BlockVector3 vector3, org.bukkit.World world) {
    return new Location(world, vector3.getX(), vector3.getY(), vector3.getZ());
  }

  public static String color(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static List<String> color(List<String> list) {
    List<String> stringList = new ArrayList<>();
    list.forEach(string -> stringList.add(color(string)));
    return stringList;
  }

  public static String colorBukkit(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static String format(Material material) {
    return WordUtils.capitalize(material.name().toLowerCase().replaceAll("_", " "));
  }

  public static String getGit() {

    try (CloseableHttpClient client = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(
          "https://api.github.com/repos/UntouchedOdin0/PrivateMines/commits");
      CloseableHttpResponse response = client.execute(request);
      String responseBody = EntityUtils.toString(response.getEntity());

      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(responseBody);
      JsonNode latestCommit = root.get(0);
      String sha = latestCommit.get("sha").textValue();
      return sha.substring(0, Math.min(7, sha.length()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String mapToString(Map<Material, Double> map) {
    if (!map.isEmpty()) {
      StringBuilder stringBuilder = new StringBuilder("{");
      for (Entry<Material, Double> entry : map.entrySet()) {
        stringBuilder.append(entry.getKey());
        stringBuilder.append("=");
        stringBuilder.append(entry.getValue());
        stringBuilder.append(", ");
      }
      stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    return "";
  }

  public static String getNMSVersion() {
    final String version = Bukkit.getServer().getClass().getPackage().getName();
    return version.substring(version.lastIndexOf('.') + 1);
  }
}
