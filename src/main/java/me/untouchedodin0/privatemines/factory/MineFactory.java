package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.*;
import com.sk89q.worldedit.math.*;
import com.sk89q.worldedit.regions.*;
import com.sk89q.worldedit.regions.selector.*;
import com.sk89q.worldedit.session.*;
import com.sk89q.worldedit.world.*;
import com.sk89q.worldguard.*;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.*;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

import java.io.*;
import java.time.*;
import java.util.*;

public class MineFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    /**
     * Creates a mine for the {@link Player} at {@link Location} with {@link MineType}
     *
     * @param player   the player the mine should be created for
     * @param location the location of the mine
     * @param mineType the type of mine to paste
     */

    public void create(Player player, Location location, MineType mineType) {
        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        Mine mine = new Mine(privateMines);
        MineData mineData = new MineData();
        String regionName = String.format("mine-%s", player.getUniqueId());

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        SchematicStorage storage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);
        Map<String, Boolean> flags = mineType.getFlags();

        Task.asyncDelayed(() -> {
            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {

                    Instant start = Instant.now();

                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
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

                    CuboidRegion mineWGRegion = new CuboidRegion(world, lrailsV, urailsV);
                    localSession.setClipboard(clipboardHolder);

                    Operation operation = clipboardHolder.createPaste(editSession).to(vector).ignoreAirBlocks(true).build();
                    try {
                        Operations.completeLegacy(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }
                    Instant pasted = Instant.now();
                    Duration durationPasted = Duration.between(start, pasted);

                    Region region = clipboard.getRegion();
                    Region newRegion;

                    BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                    Vector3 realTo = vector.toVector3().add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
                    Vector3 max = realTo.add(clipboardHolder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
                    RegionSelector regionSelector = new CuboidRegionSelector(world, realTo.toBlockPoint(), max.toBlockPoint());
                    localSession.setRegionSelector(world, regionSelector);
                    regionSelector.learnChanges();

                    try {
                        newRegion = regionSelector.getRegion();

                        Location fullMin = BukkitAdapter.adapt(BukkitAdapter.adapt(world), newRegion.getMinimumPoint());
                        Location fullMax = BukkitAdapter.adapt(BukkitAdapter.adapt(world), newRegion.getMaximumPoint());
                        CuboidRegion fullRegion = new CuboidRegion(newRegion.getMinimumPoint(), newRegion.getMaximumPoint());
                        ProtectedCuboidRegion fullWorldGuardRegion = new ProtectedCuboidRegion(regionName, newRegion.getMinimumPoint(), newRegion.getMaximumPoint());
                        ProtectedCuboidRegion miningWorldGuardRegion = new ProtectedCuboidRegion(regionName, lrailsV, urailsV);
                        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                        RegionManager regionManager = container.get(world);
                        if (regionManager != null) {
                            regionManager.addRegion(miningWorldGuardRegion);
                        }
                        Task test = Task.syncDelayed(() -> {
                            if (flags != null) {
                                flags.forEach((s, aBoolean) -> {
                                    Flag<?> flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), s);
                                    if (aBoolean) {
                                        try {
                                            Utils.setFlag(miningWorldGuardRegion, flag, "allow");
                                        } catch (InvalidFlagFormat e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            Utils.setFlag(miningWorldGuardRegion, flag, "deny");
                                        } catch (InvalidFlagFormat e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        });

                        mineData.setMineOwner(player.getUniqueId());
                        mineData.setMinimumMining(lrailsL);
                        mineData.setMaximumMining(urailsL);
                        mineData.setMinimumFullRegion(fullMin);
                        mineData.setMaximumFullRegion(fullMax);
                        mineData.setSpawnLocation(spongeL);
                        mineData.setMineLocation(location);
                        mineData.setMineType(mineType.getName());
                        mineData.setMaterials(mineType.getMaterials());
                    } catch (IncompleteRegionException e) {
                        e.printStackTrace();
                    }

                    mine.setMineOwner(player.getUniqueId());
                    mine.setLocation(vector);
                    mine.setMineData(mineData);
                    mine.saveMineData(player, mineData);

                    //noinspection unused

                    Task teleport = Task.syncDelayed(() -> spongeL.getBlock().setType(Material.AIR));
                    privateMines.getMineStorage().addMine(player.getUniqueId(), mine);

                    TextComponent teleportMessage = new TextComponent(ChatColor.GREEN + "Click me to teleport to your mine!");
                    teleportMessage.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/privatemines teleport"));
                    player.spigot().sendMessage(teleportMessage);

                    privateMines.getLogger().info("Mine creation time: " + durationPasted.toMillis() + "ms");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
