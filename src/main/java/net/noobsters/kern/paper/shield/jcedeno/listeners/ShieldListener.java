package net.noobsters.kern.paper.shield.jcedeno.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import lombok.Getter;
import net.noobsters.kern.paper.profiles.ProfileManager;
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

    @EventHandler
    public void onCraft(final PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        if (recipe == null)
            return;
        var result = recipe.getResult();

        if (result != null && result.getType() == Material.SHIELD) {
            var uuid = e.getView().getPlayer().getUniqueId().toString();
            var shieldName = ProfileManager.getCache().get(uuid).getActiveShield();
            var shield = shieldManager.getShieldFromCache(shieldName);

            if (shield != null) {
                e.getInventory().setResult(shield.applyCustomBannerData(result));
                e.getInventory().getViewers().forEach(all -> ((Player) all).updateInventory());
            } else {
                System.out.println("Shield not present");
                Bukkit.getScheduler().runTaskLaterAsynchronously(shieldManager.getInstance(), ()->{
                    var shieldAgain = shieldManager.getShieldFromCache(shieldName);
                    e.getInventory().setResult(shieldAgain.applyCustomBannerData(result));
                    e.getInventory().getViewers().forEach(all -> ((Player) all).updateInventory());
                }, 20L);
            }

        }
    }
}
