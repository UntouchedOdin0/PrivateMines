package me.untouchedodin0.privatemines.storage.points;

import com.sk89q.worldedit.math.BlockVector3;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchematicPoints {

    private BlockVector3 spawn;
    private BlockVector3 npc;
    private BlockVector3 corner1;
    private BlockVector3 corner2;
}
