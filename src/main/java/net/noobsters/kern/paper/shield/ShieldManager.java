package net.noobsters.kern.paper.shield;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Arrays;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.utils.HTimer;

public class ShieldManager implements Listener {
    private @NotNull Kern instance;
    private @Getter HashMap<String, CustomShield> playerCurrentShield = new HashMap<>();
    private @Getter HashMap<String, CustomShield> globalShieldList = new HashMap<>();

    private @Getter MongoDatabase db;
    private @Getter MongoCollection<CustomShield> shieldCollection;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ShieldManager(final Kern instance) {
        this.instance = instance;
        this.db = instance.getPunishmentManager().getMongoHynix().getMongoClient().getDatabase("condor")
                .withCodecRegistry(fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        this.shieldCollection = db.getCollection("shields", CustomShield.class);
        instance.getServer().getPluginManager().registerEvents(this, instance); 

    }
/*
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        var player = e.getPlayer();
        var timer = HTimer.start();
        var profile = instance.getPunishmentManager().getCollection()
                .find(Filters.eq("_id", player.getUniqueId().toString())).first();

        Bukkit.broadcastMessage(timer.stop() + "ms\n" + gson.toJson(profile));

    }

    */

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

        var cleanShield = new ItemStack(Material.SHIELD);
        var cMeta = cleanShield.getItemMeta();

        // durability
        var damageable = (Damageable) meta;
        var durability = damageable.getDamage();

        var damageMeta = (Damageable) cMeta;
        damageMeta.setDamage(durability);

        // name
        if (meta.hasDisplayName())
            cMeta.setDisplayName(meta.getDisplayName());

        // enchants
        if (meta.hasEnchants()) {
            var enchants = meta.getEnchants();
            for (var enchant : enchants.entrySet()) {
                cMeta.addEnchant(enchant.getKey(), enchant.getValue(), false);
            }
        }

        shield.setItemMeta(cMeta);

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

            // shield.{name}
            // shield.*
        }
    }

}
