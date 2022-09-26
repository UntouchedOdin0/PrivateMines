package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PregenFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
    List<Location> generatedLocations = new ArrayList<>();

    public void generateLocations(int amount) {
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();

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

        player.sendMessage("default type: " + defaultType);
        player.sendMessage("file: " + schematicFile);
        player.sendMessage("format: " + clipboardFormat);

        player.sendMessage(ChatColor.GREEN + "Finished generating the mines.");
    }

    public List<Location> getGeneratedLocations() {
        return generatedLocations;
    }
}
