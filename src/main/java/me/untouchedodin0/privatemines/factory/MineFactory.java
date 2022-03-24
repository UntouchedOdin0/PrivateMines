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
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.task.Task;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

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
        UUID owner = player.getUniqueId();

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        SchematicStorage storage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);

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

                    Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(), lrailsV.getBlockY(), lrailsV.getBlockZ() + 1);
                    Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(), urailsV.getBlockY(), urailsV.getBlockZ() + 1);

                    localSession.setClipboard(clipboardHolder);

                    Operation operation = clipboardHolder
                            .createPaste(editSession)
                            .to(vector)
                            .ignoreAirBlocks(true)
                            .build();
                    try {
                        Operations.completeLegacy(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }
                    Instant pasted = Instant.now();
                    Duration durationPasted = Duration.between(start, pasted);

                    mineData.setMinimumMining(lrailsL.subtract(0, 0, 1));
                    mineData.setMaximumMining(urailsL.subtract(0, 0 , 1));
                    mineData.setSpawnLocation(spongeL);

                    mine.setMineOwner(owner);
                    mine.setMineType(mineType);
                    mine.setLocation(vector);
                    mine.setSpawnLocation(spongeL);
                    mine.setMineData(mineData);

                    //noinspection unused

                    Task teleport = Task.syncDelayed(() -> {
                        spongeL.getBlock().setType(Material.AIR);
                    });
                    privateMines.getMineStorage().addMine(owner, mine);
                    TextComponent teleportMessage = new TextComponent("Click me to teleport to your mine!" );
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
