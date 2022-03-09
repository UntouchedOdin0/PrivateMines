package me.untouchedodin0.privatemines.iterator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.points.SchematicPoints;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class SchematicIterator {

    SchematicStorage schematicStorage;

    public SchematicIterator(SchematicStorage storage) {
        this.schematicStorage = storage;
    }

    public void findRelativePoints(File file) {

        SchematicPoints schematicPoints = new SchematicPoints();

        Clipboard clipboard;
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                clipboard = clipboardReader.read();
                Bukkit.getLogger().info("Clipboard: " + clipboard);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
