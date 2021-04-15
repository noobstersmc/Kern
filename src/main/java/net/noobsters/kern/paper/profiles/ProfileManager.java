package net.noobsters.kern.paper.profiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.punishments.PunishmentCommand;
import net.noobsters.kern.paper.punishments.events.PlayerBannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerMutedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnbannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnmutedEvent;

public class ProfileManager implements Listener {
    private static @Getter Map<String, PlayerProfile> cache = Collections.synchronizedMap(new HashMap<>());
    private static @Getter DatabasesConfig dbConfig = DatabasesConfig.of("databases");
    private @Getter MongoHynix mongoHynix;
    private @Getter MongoDatabase database;
    private @Getter MongoCollection<PlayerProfile> collection;
    private @Getter Kern instance;

    public ProfileManager(final Kern instance) {
        this.instance = instance;

        this.mongoHynix = MongoHynix.createFromJson(dbConfig);
        try {
            this.database = mongoHynix.getMongoClient().getDatabase("condor")
                    .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));

            this.collection = database.getCollection("punishments", PlayerProfile.class);
            // If everything is okay, register the listener.
            Bukkit.getServer().getPluginManager().registerEvents(this, instance);
            instance.getCommandManager().registerCommand(new PunishmentCommand(instance));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CompletableFuture<Optional<PlayerProfile>> queryPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            var profile = collection.find(Filters.eq("_id", uuid.toString())).first();

            return Optional.ofNullable(profile);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        var id = e.getUniqueId();
        var address = e.getAddress().getHostName();
        var query = collection.find(Filters.eq("_id", id.toString())).first();

        if (query != null) {
            query.commitAddress(address, collection);
            cache.put(id.toString(), query);
        } else {
            var newProfile = PlayerProfile.create(id, e.getName(), address);
            cache.put(id.toString(), newProfile);
            collection.insertOne(newProfile);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onLogin(PlayerLoginEvent e) {
        var player = e.getPlayer();
        var profile = cache.get(player.getUniqueId().toString());
        if (profile != null) {
            var ban = profile.isBanned();
            if (ban != null) {
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, ban.timeLeft());
            }
            // Log the connection
            profile.commitChangeOfState(State.connected(), collection);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent e) {
        // Clean cache
        var oldProfile = cache.remove(e.getPlayer().getUniqueId().toString());
        if (oldProfile != null) {
            // Log the disconnection
            oldProfile.commitChangeOfState(State.disconnected(), collection);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerBan(PlayerBannedEvent e) {
        var player = e.getPlayer();
        var ban = e.getBan();
        /** If async, schedule the kick in the main thread */
        if (player != null) {
            if (Bukkit.isPrimaryThread())
                player.kickPlayer(ban.getReason());
            else
                Bukkit.getScheduler().runTask(Kern.getInstance(), () -> player.kickPlayer(ban.getReason()));
        }

        /** Broadcast the message to everyone else */
        Bukkit.broadcastMessage(ChatColor.GREEN + e.getProfile().getName() + " has been banned by " + ban.getPunisher()
                + " for " + ban.timeLeft());

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMute(PlayerMutedEvent e) {
        var player = e.getProfile().getName();
        var mute = e.getMute();
        Bukkit.broadcastMessage(
                ChatColor.GREEN + player + " has been muted by " + mute.getPunisher() + " for " + mute.timeLeft());

    }

    @EventHandler
    public void onUnmute(PlayerUnmutedEvent e) {
        var profile = e.getProfile();

        Bukkit.broadcastMessage(ChatColor.GREEN + profile.getName() + " has been unmuted.");

    }

    @EventHandler
    public void onUnban(PlayerUnbannedEvent e) {
        var profile = e.getProfile();

        Bukkit.broadcastMessage(ChatColor.GREEN + profile.getName() + " has been unbanned.");

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMutedPlayerChat(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var id = player.getUniqueId().toString();
        var profile = cache.get(id);
        var mute = profile.isMuted();
        if (mute != null) {
            e.setCancelled(true);
            player.sendMessage(
                    ChatColor.RED + "You are currently muted for " + mute.getReason() + " (" + mute.timeLeft() + ")");

        }

    }

}
