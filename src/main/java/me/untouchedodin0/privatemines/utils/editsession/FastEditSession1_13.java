package me.untouchedodin0.privatemines.utils.editsession;

import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class FastEditSession1_13 {

    private final org.bukkit.World bukkitWorld;
    private final World world;
    private final HashMap<BlockPosition, IBlockData> modified = new HashMap<>();

    public FastEditSession1_13(org.bukkit.World bukkitWorld, World world) {
        this.bukkitWorld = bukkitWorld;
        this.world = world;
    }

    public void setBlock(int x, int y, int z, Material material) {
        modified.put(new BlockPosition(x, y, z), CraftMagicNumbers.getBlock(material).getBlockData());
    }

    public void update() {

        // Modify the blocks
        HashSet<Chunk> chunks = new HashSet<>();
        for (Map.Entry<BlockPosition, IBlockData> entry : modified.entrySet()) {
            Chunk chunk = world.getChunkProvider().getChunkAt(entry.getKey().getX(), entry.getKey().getZ(), false, false);
            if (chunk != null) {
                chunk.setType(entry.getKey(), entry.getValue(), false);
                chunks.add(chunk);
            }
        }

        for (Chunk chunk : chunks) {
            PacketPlayOutUnloadChunk unloadChunk = new PacketPlayOutUnloadChunk(chunk.getPos().x, chunk.getPos().z);
            PacketPlayOutMapChunk load = new PacketPlayOutMapChunk(chunk, 65535);
            for (Player player : Bukkit.getOnlinePlayers()) {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                int viewDistance = Bukkit.getViewDistance() + 1;
                int chunkX = entityPlayer.chunkX;
                int chunkZ = entityPlayer.chunkZ;
                if (chunk.getPos().x < chunkX
                        - viewDistance
                        || chunk.getPos().x
                        > chunkX
                        + viewDistance
                        || chunk.getPos().z
                        < chunkZ - viewDistance
                        || chunk.getPos().z
                        > chunkZ
                        + viewDistance) continue;
                entityPlayer.playerConnection.sendPacket(unloadChunk);
                entityPlayer.playerConnection.sendPacket(load);
            }
        }
        // Clear modified blocks
        modified.clear();
    }
}
