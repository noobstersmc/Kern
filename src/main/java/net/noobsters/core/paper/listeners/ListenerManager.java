package net.noobsters.core.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.Getter;
import net.noobsters.core.paper.Core;

public class ListenerManager {
    private Core instance;
    private @Getter ShieldListeners shields;

    public ListenerManager(Core instance) {
        this.instance = instance;
        shields = new ShieldListeners(instance);

        Bukkit.getPluginManager().registerEvents(new ShieldListeners(instance), instance);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

}