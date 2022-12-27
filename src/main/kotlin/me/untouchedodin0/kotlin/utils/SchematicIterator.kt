package me.untouchedodin0.kotlin.utils

import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.world.block.BlockType
import me.untouchedodin0.privatemines.config.Config
import java.io.File
import java.io.FileInputStream

/**
 * Schematic iterator to find relative points within schematic files
 * @author DrZoddiak
 */
class SchematicIterator {

    fun findRelativePoints(file: File): MineBlocks {
        ClipboardFormats.findByFile(file)?.let { format ->
            format.getReader(FileInputStream(file)).use {
                return SchematicData(it.read()).mineBlocks
            }
        }
        return MineBlocks()
    }

    private class SchematicData(val clipboard: Clipboard) {

        private var spawn: BlockVector3? = null
        private var npc: BlockVector3? = null
        private var quarry: BlockVector3? = null
        private var corner1: BlockVector3? = null
        private var corner2: BlockVector3? = null

        init {
            clipboard.region.forEach { vec ->
                val type = clipboard.getBlock(vec).blockType
                val pos = BlockVector3.at(vec.x, vec.y, vec.z)

                val cornerMaterial = Config.mineCorner
                val spawnMaterial = Config.spawnPoint
                val npcMaterial = Config.sellNpc
                val quarryMaterial = Config.quarryMaterial

                val cornerType = BlockType.REGISTRY[cornerMaterial.key.key]
                val spawnType = BlockType.REGISTRY[spawnMaterial.key.key]
                val npcType = BlockType.REGISTRY[npcMaterial.key.key]
                val quarryType = BlockType.REGISTRY[quarryMaterial.key.key]

                when (type) {
                    cornerType -> {
                        if (corner1 == null) {
                            corner1 = pos
                        } else if (corner2 == null) {
                            corner2 = pos
                        }
                    }

                    spawnType -> {
                        if (spawn == null) {
                            spawn = pos
                        }
                    }

                    npcType -> {
                        if (npc == null) {
                            npc = pos
                        }
                    }

                    quarryType -> {
                        if (quarry == null) {
                            quarry = pos
                        }
                    }
                }
            }
        }

        val mineBlocks
            get() = MineBlocks(spawn?.let { posAt(it.x, it.y, it.z) }, npc?.let { posAt(it.x, it.y, it.z) }, quarry?.let { posAt(it.x, it.y, it.z) }, Corners(
                    corner1?.let { posAt(it.x, it.y, it.z) },
                    corner2?.let { posAt(it.x, it.y, it.z) },
            ))

        private fun posAt(x: Int, y: Int, z: Int): BlockVector3 {
            return BlockVector3.at(x, y, z)
        }
    }

    class MineBlocks(var spawnLocation: BlockVector3? = null, var npcLocation: BlockVector3? = null, var quarryLocation: BlockVector3? = null, var corners: Corners? = null)

    class Corners(val corner1: BlockVector3?, val corner2: BlockVector3?)
}