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
    public int width;
    public int height;
    public int length;
    public boolean cancelled;

    public PrivateMineExpandEvent(UUID owner, Mine mine, int width, int height, int length) {
        this.owner = owner;
        this.mine = mine;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public UUID getOwner() {
        return owner;
    }

    public Mine getMine() {
        return mine;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
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
