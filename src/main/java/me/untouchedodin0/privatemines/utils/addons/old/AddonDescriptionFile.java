package me.untouchedodin0.privatemines.utils.addons.old;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AddonDescriptionFile {

    public AddonDescriptionFile(final InputStream inputStream) {

        StringBuilder stringBuilder = new StringBuilder();

        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }

            System.out.println("stringBuilder: " + stringBuilder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
