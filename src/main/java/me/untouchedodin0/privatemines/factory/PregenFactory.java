package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PregenFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    EditSession editSession;
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
    PregenStorage pregenStorage = privateMines.getPregenStorage();

    List<Location> generatedLocations = new ArrayList<>();

    public void generateLocations(int amount) {
        for (int i = 0; i < amount; i++) {
            generatedLocations.add(mineWorldManager.getNextFreeLocation());
        }
    }

    public void generate(Player player, int amount) {
        player.sendMessage("Generating mines...");
        generateLocations(amount);
        MineType defaultType = mineTypeManager.getDefaultMineType();
        File schematicFile = new File("plugins/PrivateMines/schematics/" + defaultType.getFile());

        if (!schematicFile.exists()) {
            privateMines.getLogger().warning("Schematic file does not exist: " + schematicFile.getName());
            return;
        }

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        SchematicStorage schematicStorage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = schematicStorage.getMineBlocksMap().get(schematicFile);

        for (Location location : generatedLocations) {
            BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

            Task.asyncDelayed(() -> {
                if (clipboardFormat != null) {
                    try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                        World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                        if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                            editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).fastMode(true).build();
                        } else {
                            editSession = WorldEdit.getInstance().newEditSession(world);
                        }
                        LocalSession localSession = new LocalSession();

                        Clipboard clipboard = clipboardReader.read();
                        ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

//                    mb0|0,50,-150
//                    mb1|13,48,-144
//                    mb2|-12,20,-116
//                    cbo|0,50,-151
//                    min|-30000000,-64,-30000000
//                    loc|763.6692645437984,140.45037494032877,728.8705431310638
//
//                    763,140,729 Sponge
//                    751,110,763 Lower Rails Sponge - mb0 + mb2 -> 763 - 0 +-12 = 751
//                    776,138,735 Upper Rails Sponge - mb0 + mb1 -> 763 - 0 + 13 = 776

                        BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner2().add(0, 0, 1));
                        BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner1().add(0, 0, 1));

                        Location spongeL = new Location(location.getWorld(), vector.getBlockX(), vector.getBlockY(), vector.getBlockZ() + 1);

                        Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(), lrailsV.getBlockY(), lrailsV.getBlockZ());
                        Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(), urailsV.getBlockY(), urailsV.getBlockZ());

                        PregenMine pregenMine = new PregenMine();

                        pregenMine.setSpawnLocation(spongeL);
                        pregenMine.setLowerRails(lrailsL);
                        pregenMine.setUpperRails(urailsL);

                        Bukkit.broadcastMessage("pregen mines: " + pregenStorage.getMines());
                        pregenStorage.addMine(pregenMine);

                        Bukkit.broadcastMessage("" + spongeL);
                        Bukkit.broadcastMessage("" + lrailsL);
                        Bukkit.broadcastMessage("" + urailsL);
                        Bukkit.broadcastMessage("vector: " + vector);

                        Bukkit.broadcastMessage("---- kotlin ----");
                        Bukkit.broadcastMessage("" + pregenMine.getSpawnLocation());
                        Bukkit.broadcastMessage("" + pregenMine.getLowerRails());
                        Bukkit.broadcastMessage("" + pregenMine.getUpperRails());
                        Bukkit.broadcastMessage("pregen mines: " + pregenStorage.getMines());

                        localSession.setClipboard(clipboardHolder);

                        Operation operation = clipboardHolder.createPaste(editSession).to(vector).ignoreAirBlocks(true).build();

                        try {
                            Operations.complete(operation);
                            editSession.close();
                        } catch (WorldEditException worldEditException) {
                            worldEditException.printStackTrace();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


//           if (clipboardFormat != null) {
//               try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
//                   World world = BukkitAdapter.adapt(mineWorldManager.getMinesWorld());
//
//                   if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
//                       editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).fastMode(true).build();
//                   } else {
//                       editSession = WorldEdit.getInstance().newEditSession(world);
//                   }
//
//                   Clipboard clipboard = clipboardReader.read();
//                   ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
//
////                   player.sendMessage("clipboard reader: " + clipboardReader);
////                   player.sendMessage("world: " + world);
////                   player.sendMessage("edit session: " + editSession);
////                   player.sendMessage("clipboard: " + clipboard);
////                   player.sendMessage("clipboard holder " + clipboardHolder);
////                   player.sendMessage("mine blocks: " + mineBlocks);
//
//                   for (Location location : generatedLocations) {
//
//                       BlockVector3 spawn = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//
//                       Bukkit.broadcastMessage("clipboard " + clipboard);
//                       Bukkit.broadcastMessage("clipboard holder " + clipboardHolder);
//                       Bukkit.broadcastMessage("format " + clipboardFormat);
//
//                       Bukkit.broadcastMessage(" " + spawn);
//                       BlockVector3 upperRails = spawn.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner1()).add(0, 0, 1);
//                       BlockVector3 lowerRails = spawn.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner2().add(0, 0, 1));
//
//                       Location spawnLoc = new Location(location.getWorld(), spawn.getBlockX(), spawn.getBlockY(), spawn.getZ() + 1);
////                       player.sendMessage("bv3 " + spawn);
////                       player.sendMessage("upper rails: " + upperRails);
////                       player.sendMessage("lower rails: " + lowerRails);
//                       player.sendMessage("spawn loc: " + LocationUtils.toString(spawnLoc));
//
//                       Operation operation = clipboardHolder
//                               .createPaste(editSession)
//                               .to(spawn)
//                               .ignoreAirBlocks(true)
//                               .build();
//                       Operations.completeLegacy(operation);
//
//                       player.sendMessage("operation: " + operation);
//                   }
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           }

//        player.sendMessage("default type: " + defaultType);
//        player.sendMessage("file: " + schematicFile);
//        player.sendMessage("format: " + clipboardFormat);
//
//        player.sendMessage(ChatColor.GREEN + "Finished generating the mines.");
        }
    }
}
;

