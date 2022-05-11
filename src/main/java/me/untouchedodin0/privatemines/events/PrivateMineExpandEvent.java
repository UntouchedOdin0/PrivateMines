package me.untouchedodin0.privatemines.events;

import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PrivateMineExpandEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    public UUID owner;
    public Mine mine;
    public int oldSize;
    public int newSize;
    public boolean cancelled;

    public PrivateMineExpandEvent(UUID owner, Mine mine, int oldSize, int newSize) {
        this.owner = owner;
        this.mine = mine;
        this.oldSize = oldSize;
        this.newSize = newSize;
    }

    public UUID getOwner() {
        return owner;
    }

    public Mine getMine() {
        return mine;
    }

    public int getOldSize() {
        return oldSize;
    }

    public int getNewSize() {
        return newSize;
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
