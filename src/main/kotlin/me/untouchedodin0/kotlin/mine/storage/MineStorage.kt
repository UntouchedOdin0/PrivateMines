package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.mine.Mine
import java.util.*

class MineStorage {
    var mines: MutableMap<UUID, Mine> = HashMap()
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()

    fun addMine(uuid: UUID, mine: Mine) {
        if (mines.containsKey(uuid)) {
            privateMines.logger.info(String.format("Player %s already has a mine!!", uuid.toString()))
        } else {
            mines[uuid] = mine
        }
    }

    fun removeMine(uuid: UUID) {
        if (!mines.containsKey(uuid)) {
            privateMines.logger.warning(String.format("Player %s doesn't a mine!!", uuid.toString()))
        } else {
            mines.remove(uuid)
        }
    }

    fun hasMine(uuid: UUID): Boolean {
        return mines.containsKey(uuid)
    }

    operator fun get(uuid: UUID): Mine? {
        return mines[uuid]
    }
}