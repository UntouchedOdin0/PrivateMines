package me.untouchedodin0.privatemines.storage.points;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.List;

public class SchematicPoints {

    private BlockVector3 spawn;
    private BlockVector3 npc;
    private List<BlockVector3> corners;

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

    public List<BlockVector3> getCorners() {
        return corners;
    }

    public void setCorners(List<BlockVector3> corners) {
        this.corners = corners;
    }
}
