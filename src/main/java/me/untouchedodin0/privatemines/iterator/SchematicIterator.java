package me.untouchedodin0.privatemines.iterator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.points.SchematicPoints;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchematicIterator {

    SchematicStorage schematicStorage;
    SchematicPoints schematicPoints = new SchematicPoints();

    public SchematicIterator(SchematicStorage storage) {
        this.schematicStorage = storage;
    }

    public void findRelativePoints(File file) {

        Task task = Task.asyncDelayed(() -> {
            Clipboard clipboard;
            ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);

            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
                    clipboard = clipboardReader.read();
                    Bukkit.getLogger().info("Clipboard: " + clipboard);
                    Bukkit.getLogger().info("Clipboard Region: " + clipboard.getRegion());
                    List<BlockVector3> corners = new ArrayList<>();

                    clipboard.getRegion().forEach(blockVector3 -> {
                        BlockType blockType = clipboard.getBlock(blockVector3).getBlockType();
                        if (blockType.getMaterial().isAir() || blockType.equals(BlockTypes.BEDROCK)) return;
                        Bukkit.getLogger().info("blockType: " + blockType);
                        if (blockType.equals(BlockTypes.SPONGE)) {
                            Bukkit.getLogger().info("Found sponge at: " + blockVector3);
                            schematicPoints.setSpawn(blockVector3);
                        } else if (blockType.equals(BlockTypes.POWERED_RAIL)) {
                            corners.add(blockVector3);
                        }
                    });

                    Bukkit.getLogger().info("spawn " + schematicPoints.getSpawn());
                    Bukkit.getLogger().info("corners: " + corners);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        schematicStorage.addSchematic(file, schematicPoints);
    }
}
