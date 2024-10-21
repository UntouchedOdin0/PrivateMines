/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.kotlin.mine.data

import me.untouchedodin0.kotlin.mine.type.MineType
import me.untouchedodin0.privatemines.playershops.Shop
import org.bukkit.Location
import org.bukkit.Material
import java.util.*

data class MineData(
    val mineOwner: UUID,
    var maximumMining: Location,
    var minimumMining: Location,
    var minimumFullRegion: Location,
    var maximumFullRegion: Location,
    val mineLocation: Location,
    var spawnLocation: Location,
    var mineType: MineType,
    var isOpen: Boolean = false,
    var tax: Double = 5.0,
) {
    var shop: Shop? = null

    var bannedPlayers: MutableList<UUID> = mutableListOf()
    var friends: MutableList<UUID> = mutableListOf()
    var materials: Map<Material, Double> = EnumMap(Material::class.java)
    var maxPlayers: Int = 0
    var maxMineSize: Int = 0

    constructor(
        mineOwner: UUID,
        maximumMining: Location,
        minimumMining: Location,
        minimumFullRegion: Location,
        maximumFullRegion: Location,
        spawnLocation: Location,
        mineLocation: Location,
        mineType: MineType,
        shop: Shop,
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