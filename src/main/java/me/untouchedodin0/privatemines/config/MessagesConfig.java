/**
 * MIT License
 *
 * Copyright (c) 2021 - 2023 Kyle Hicks
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

package me.untouchedodin0.privatemines.config;

public class MessagesConfig {

    public static String mineReset = "<green>Your mine has been reset!";
    public static String resetTargetMine = "<green>You've reset {name}'s mine!";
    public static String dontOwnMine = "<red>You don't own a mine!";
    public static String playerDoesntOwnMine = "<red>That player doesn't own a mine!";
    public static String playerAlreadyOwnsAMine = "<red>That player already owns a mine!";
    public static String deletedPlayersMine = "<green>You have deleted {name}'s mine!";
    public static String gavePlayerMine = "<green>You have given {name} a mine!";
    public static String mineUpgraded = "<green>Your mine has been upgraded!";
    public static String playerMineExpanded = "<green>{names}s mine has been expanded by {amount}!";
    public static String ownMineExpanded = "<green>Your mine has been expanded by {amount}!";
    public static String teleportedToOwnMine = "<green>You have been teleported to your mine!";
    public static String visitingMine = "<green>You are now visiting {name}'s mine!";
    public static String setTax = "<green>Successfully set your tax to {tax}%!";
    public static String targetAlreadyBanned = "<green>The target player is already banned!";
    public static String successfullyBannedPlayer = "<green>{name} Has been banned from your mine!";
    public static String targetIsNotBanned = "<red>{name} isn't banned!";
    public static String unbannedPlayer = "<green>{name} Has been unbanned from your mine!";
    public static String bannedFromMine = "<red>You've been banned from {name}'s mine!";
    public static String unbannedFromMine = "<green>You've been unbanned from {name}'s mine!";
    public static String targetMineClosed = "<red>The mine you were trying to teleport to is closed.";
    public static String mineOpened = "<grey>Your mine has been <green>opened<grey>!";
    public static String mineClosed = "<grey>Your mine has been <red>closed<grey>!";
}
