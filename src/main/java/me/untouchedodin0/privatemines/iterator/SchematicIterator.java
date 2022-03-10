package me.untouchedodin0.privatemines.iterator;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.points.SchematicPoints;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SchematicIterator {

    SchematicStorage schematicStorage;
    BlockVector3 spawn;
    BlockVector3 corner1;
    BlockVector3 corner2;
    List<BlockVector3> corners = new ArrayList<>();

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
                Bukkit.getLogger().info("Clipboard Region: " + clipboard.getRegion());

                clipboard.getRegion().forEach(blockVector3 -> {
                    BlockType blockType = clipboard.getBlock(blockVector3).getBlockType();
                    if (blockType.equals(BlockTypes.POWERED_RAIL)) {
                        Bukkit.getLogger().info("powered rail " + blockVector3);
                        if (schematicPoints.getCorner1() == null) {
                            schematicPoints.setCorner1(blockVector3);
                        } else if (schematicPoints.getCorner2() == null) {
                            schematicPoints.setCorner2(blockVector3);
                        }
                    }
                });
                Bukkit.getLogger().info("test 1: " + schematicPoints.getCorner1());
                Bukkit.getLogger().info("test 2: " + schematicPoints.getCorner2());

                schematicStorage.addSchematic(file, schematicPoints);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class MineBlocks {
        BlockVector3 spawnLocation;
        BlockVector3[] corners;
    }
}
