package me.untouchedodin0.privatemines.events;

import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PrivateMineResetEvent extends Event {

    public static final HandlerList handlers = new HandlerList();

    public UUID owner;
    public Mine mine;
    public boolean cancelled;

    public PrivateMineResetEvent(UUID owner, Mine mine) {
        this.owner = owner;
        this.mine = mine;
    }

    public UUID getOwner() {
        return owner;
    }

    public Mine getMine() {
        return mine;
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
