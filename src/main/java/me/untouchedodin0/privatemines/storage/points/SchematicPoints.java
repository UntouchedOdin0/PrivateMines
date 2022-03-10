package me.untouchedodin0.privatemines.storage.points;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Bukkit;

public class SchematicPoints {

    private BlockVector3 spawn;
    private BlockVector3 npc;
    private BlockVector3 corner1;
    private BlockVector3 corner2;

    public BlockVector3 getSpawn() {
        return spawn;
    }

    public void setSpawn(BlockVector3 spawn) {
        this.spawn = spawn;
    }

    public BlockVector3 getNpc() {
        return npc;
    }

    public void setNpc(BlockVector3 npc) {
        this.npc = npc;
    }

    public BlockVector3 getCorner1() {
        return corner1;
    }

    public void setCorner1(BlockVector3 corner1) {
        Bukkit.getLogger().info("Corner1 set to " + corner1 + "!");
        this.corner1 = corner1;
    }

    public BlockVector3 getCorner2() {
        return corner2;
    }

    public void setCorner2(BlockVector3 corner2) {
        Bukkit.getLogger().info("Corner2 set to " + corner2 + "!");
        this.corner2 = corner2;
    }
}