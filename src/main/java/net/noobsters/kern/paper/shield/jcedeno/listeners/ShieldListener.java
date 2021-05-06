package net.noobsters.kern.paper.shield.jcedeno.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import lombok.Getter;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;

public class ShieldListener implements Listener {
    private @Getter ShieldManager shieldManager;

    /**
     * Constructor that auto-registers the events of this class.
     * 
     * @param shieldManager ShieldManager instance to register the events.
     */
    public ShieldListener(final ShieldManager shieldManager) {
        this.shieldManager = shieldManager;
        /** Register the events with bukkit */
        Bukkit.getPluginManager().registerEvents(this, shieldManager.getInstance());
    }

}
