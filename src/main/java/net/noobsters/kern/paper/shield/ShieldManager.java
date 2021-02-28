package net.noobsters.kern.paper.shield;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;

public class ShieldManager implements Listener {
    private @NotNull Kern instance;
    private @Getter HashMap<String, CustomShield> playerCurrentShield = new HashMap<>();
    private @Getter HashMap<String, CustomShield> globalShieldList = new HashMap<>();

    public ShieldManager(final Kern instance) {
        this.instance = instance;

    }

    public ItemStack setCustomBanner(ItemStack shield, CustomShield customShield) {

        var meta = shield.getItemMeta();
        var bmeta = (BlockStateMeta) meta;
        var banner = (Banner) bmeta.getBlockState();

        banner.setPatterns(customShield.getPatterns());
        banner.setBaseColor(customShield.getColor());

        banner.update();
        bmeta.setBlockState(banner);
        shield.setItemMeta(bmeta);

        return shield;
    }

    public ItemStack removeCustomBanner(ItemStack shield) {

        var meta = shield.getItemMeta();
        var bmeta = (BlockStateMeta) meta;

        var cleanShield = new ItemStack(Material.SHIELD);

        var cMeta = cleanShield.getItemMeta();

        var cbMeta = (BlockStateMeta) cMeta;
        var cBanner = (Banner) cbMeta.getBlockState();

        bmeta.setBlockState(cBanner);

        var banner = (Banner) bmeta.getBlockState();
        banner.update();

        shield.setItemMeta(bmeta);

        return shield;
    }

    @EventHandler
    public void onItemOnInventory(InventoryCloseEvent e) {
        var player = (Player) e.getView().getPlayer();

        var uuid = player.getUniqueId().toString();
        var customShield = playerCurrentShield.get(uuid);

        var inventory = player.getInventory().getContents();
        Arrays.stream(inventory).filter(item -> item != null && item.getType() == Material.SHIELD).forEach(shield -> {
            if (playerCurrentShield.containsKey(uuid)) {
                // change shield to custom
                setCustomBanner(shield, customShield);
            } else {
                // default shield
                removeCustomBanner(shield);
            }
        });
    }

    @EventHandler
    public void onPickUp(EntityPickupItemEvent e) {
        var shield = e.getItem().getItemStack();
        var entity = e.getEntity();
        if (entity instanceof Player && shield.getType() == Material.SHIELD) {
            var player = (Player) entity;
            var uuid = player.getUniqueId().toString();
            var customShield = playerCurrentShield.get(uuid);

            if (playerCurrentShield.containsKey(uuid)) {
                // change shield to custom
                setCustomBanner(shield, customShield);
            } else {
                // default shield
                removeCustomBanner(shield);
            }
        }
    }

    @EventHandler
    public void onCraft(PrepareItemCraftEvent e) {
        var recipe = e.getRecipe();
        var uuid = e.getView().getPlayer().getUniqueId().toString();

        if (playerCurrentShield.containsKey(uuid) && recipe != null
                && recipe.getResult().getType() == Material.SHIELD) {

            var customShield = playerCurrentShield.get(uuid);
            var shield = e.getRecipe().getResult();

            e.getInventory().setResult(setCustomBanner(shield, customShield));
        }
    }

}