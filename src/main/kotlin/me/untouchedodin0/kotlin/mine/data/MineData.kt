package me.untouchedodin0.kotlin.mine.data

import me.untouchedodin0.kotlin.mine.type.MineType
import me.untouchedodin0.privatemines.playershops.Shop
import org.bukkit.Location
import org.bukkit.Material
import java.util.EnumMap
import java.util.UUID

data class MineData(
    val mineOwner: UUID,
    var maximumMining: Location,
    var minimumMining: Location,
    var minimumFullRegion: Location,
    var maximumFullRegion: Location,
    val mineLocation: Location,
    var spawnLocation: Location,
    val mineType: MineType,
    var isOpen: Boolean = false,
    var tax: Double = 5.0,
) {
    var shop: Shop? = null

    var bannedPlayers: MutableList<UUID> = mutableListOf()
    var materials: Map<Material, Double> = EnumMap(Material::class.java)

    constructor(
        mineOwner: UUID,
        maximumMining: Location,
        minimumMining: Location,
        minimumFullRegion: Location,
        maximumFullRegion: Location,
        spawnLocation: Location,
        mineLocation: Location,
        mineType: MineType,
        shop: Shop
    ) : this(
        mineOwner,
        maximumMining,
        minimumMining,
        minimumFullRegion,
        maximumFullRegion,
        mineLocation,
        spawnLocation,
        mineType
    ) {
        this.shop = shop
    }
}