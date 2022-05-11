package me.untouchedodin0.privatemines.events;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PrivateMineUpgradeEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    public UUID owner;
    public Mine mine;
    public MineType oldType;
    public MineType newType;
    public boolean cancelled;

    public PrivateMineUpgradeEvent(UUID owner, Mine mine, MineType oldType, MineType newType) {
        this.owner = owner;
        this.mine = mine;
        this.oldType = oldType;
        this.newType = newType;
    }

    public UUID getOwner() {
        return owner;
    }

    public Mine getMine() {
        return mine;
    }

    public MineType getOldType() {
        return oldType;
    }

    public MineType getNewType() {
        return newType;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
