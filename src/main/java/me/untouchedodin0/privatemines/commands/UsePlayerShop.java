/**
 * MIT License
 *
 * Copyright (c) 2021 - 2022 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("useshop|useplayershop|usepshop")
public class UsePlayerShop extends BaseCommand {

    PrivateMines privateMines;
    MineStorage mineStorage;

    public UsePlayerShop(PrivateMines privateMines, MineStorage mineStorage) {
        this.privateMines = privateMines;
        this.mineStorage = mineStorage;
    }

    @Default
    @CommandCompletion("@players")
    public void usePlayerShop(Player player, Player target) {
        if (!target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        } else if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Player does not have a mine!");
        } else {
            player.sendMessage(String.format(ChatColor.GREEN + "Trying to use %s's shop", target.getName()));
        }
    }
}

// Generated with love by @0xC0FFEE (untouchedodin0)
