package net.noobsters.kern.paper.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.chat.ChatListener;

public class ListenerManager {
    private Kern instance;

    public ListenerManager(Kern instance) {
        this.instance = instance;
        Bukkit.getPluginManager().registerEvents(new ChatListener(instance), instance);
        Bukkit.getPluginManager().registerEvents(instance.getShieldManager(), instance);


    }


    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

}