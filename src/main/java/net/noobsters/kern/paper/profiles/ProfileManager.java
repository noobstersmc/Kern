package net.noobsters.kern.paper.profiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.punishments.PunishmentCommand;
import net.noobsters.kern.paper.punishments.events.PlayerBannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerMutedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnbannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnmutedEvent;

public class ProfileManager implements Listener {
    private static @Getter Map<String, PlayerProfile> cache = Collections.synchronizedMap(new HashMap<>());
    private @Getter MongoCollection<PlayerProfile> collection;
    private @Getter Kern instance;

    public ProfileManager(final Kern instance) {
        this.instance = instance;
        try {
            this.collection = instance.getCondorManager().getMongoDatabase().getCollection("punishments", PlayerProfile.class);
            // If everything is okay, register the listener.
            Bukkit.getServer().getPluginManager().registerEvents(this, instance);
            instance.getCommandManager().registerCommand(new PunishmentCommand(instance));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {
        var id = e.getUniqueId();
        var address = e.getAddress().getHostAddress();

        var query = collection.findOneAndUpdate(Filters.eq("_id", id.toString()),
                Updates.combine(Updates.set("name", e.getName()), Updates.addToSet("addresses", address)),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        if (query != null) {
            query.commitAddress(address, collection);
            cache.put(id.toString(), query);
        } else {
            var newProfile = PlayerProfile.create(id, e.getName(), address);
            cache.put(id.toString(), newProfile);
            collection.insertOne(newProfile);
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void checkForBlacklist(AsyncPlayerPreLoginEvent e) {
        var address = e.getAddress().getHostAddress();
        var id = e.getUniqueId().toString();
        var name = e.getName();

        var someoneBannedWithSameIP = Filters.and(Filters.in("addresses", address), Filters.eq("bans.canceled", false),
                Filters.lt("bans.expiration", System.currentTimeMillis()));

        var playerIfNotBypassable = Filters.and(Filters.eq("_id", id), Filters.ne("bypass", true));

        var query = collection.find(Filters.or(playerIfNotBypassable, someoneBannedWithSameIP));

        var iter = query.iterator();
        var list = new ArrayList<PlayerProfile>();

        PlayerProfile ownProfile = null;

        while (iter.hasNext()) {
            var next = iter.next();

            if (next.getUuid() == id) {
                ownProfile = next;
                continue;
            }

            list.add(next);
        }

        if (ownProfile != null && list.size() > 0) {
            System.out.println(name + " should be blacklisted");
            e.disallow(Result.KICK_BANNED,
                    ChatColor.RED + "You are bypassing a ban on " + list.get(0).getName() + "'s account.");
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
        /** Define the variables to be used */
        final var name = e.getProfile().getName();
        final var punisher = ban.getPunisher();
        final var reason = ban.getReason().split("-")[0];
        final var timeleft = ban.timeLeft();

        /** Broadcast the message to bukkit and discord */
        Bukkit.broadcastMessage(ChatColor.of("#97559b") + name + " has been banned for " + reason + " " + timeleft);
        sendPunizioneMessage(
                String.format("**%s** has been banned for %s (%s) by **%s**", name, reason, timeleft, punisher));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMute(PlayerMutedEvent e) {
        /** Define the variables to be used */
        final var mute = e.getMute();
        final var name = e.getProfile().getName();
        final var punisher = mute.getPunisher();
        final var reason = mute.getReason().split("-")[0];
        final var timeleft = mute.timeLeft();

        /** Broadcast the message to bukkit and discord */
        Bukkit.broadcastMessage(
                ChatColor.of("#97559b") + String.format("%s has been muted for %s %s", name, reason, timeleft));
        sendPunizioneMessage(
                String.format("**%s** has been muted for %s (%s) by **%s**", name, reason, timeleft, punisher));

    }

    @EventHandler
    public void onUnmute(PlayerUnmutedEvent e) {
        var profile = e.getProfile();

        Bukkit.broadcastMessage(ChatColor.of("#97559b") + profile.getName() + " has been unmuted.");

    }

    @EventHandler
    public void onUnban(PlayerUnbannedEvent e) {
        var profile = e.getProfile();

        Bukkit.broadcastMessage(ChatColor.of("#97559b") + profile.getName() + " has been unbanned.");

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMutedPlayerChat(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var id = player.getUniqueId().toString();
        var profile = cache.get(id);
        if (profile != null) {
            var mute = profile.isMuted();
            if (mute != null) {
                e.setCancelled(true);
                var reason = mute.getReason().split("-")[0];
                player.sendMessage(
                        ChatColor.RED + "You are currently muted for " + reason + " (" + mute.timeLeft() + ")");
            }
        } else {
            instance.getProfileManager().queryPlayer(player.getUniqueId());

        }

    }

    /**
     * Static function to quickly update the cached profile of a player.
     * 
     * @param uuid    Stringified uuid for the key.
     * @param profile PlayerProfile to be cached.
     * @return The previous profile associated with the key or null.
     */
    public static PlayerProfile putInCache(String uuid, PlayerProfile profile) {
        return cache.put(uuid, profile);

    }

    /**
     * Static function to quickly update the cached profile of a player.
     * 
     * @param uuid    UUID for the key.
     * @param profile PlayerProfile to be cached.
     * @return The previous profile associated with the key or null.
     */
    public static PlayerProfile putInCache(UUID uuid, PlayerProfile profile) {
        return putInCache(uuid.toString(), profile);
    }

    /**
     * Asynchronously supplied function that queries and updates a player cache.
     * 
     * @param uuid UUID of the player to be requeried and recached.
     * @return Optional, nullable, PlayerProfile object.
     */
    public CompletableFuture<Optional<PlayerProfile>> queryPlayer(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            return queryAndCachePlayer(uuid);
        });
    }

    /**
     * Fnction that queries and updates a player cache.
     * 
     * @param uuid UUID of the player to be requeried and recached.
     * @return Optional, nullable, PlayerProfile object.
     */
    public Optional<PlayerProfile> queryAndCachePlayer(UUID uuid) {
        var profile = collection.find(Filters.eq("_id", uuid.toString())).first();
        if (profile != null) {
            putInCache(uuid, profile);
        }
        return Optional.ofNullable(profile);

    }

    /**
     * Utility function that inserts and returns a new Document into the database
     * 
     * @param uuid UUID of player
     * @param name Displayname of player
     * @return PlayerProfile
     */
    public PlayerProfile createProfile(UUID uuid, String name) {
        var nProfile = PlayerProfile.create(uuid, name, "");
        putInCache(uuid, nProfile);
        collection.insertOne(nProfile);

        return nProfile;
    }

    /**
     * Private utility function to safely ignore the JsonProcessingException
     * 
     * @param message {@link String} to be sent using
     *                {@link DiscordHook#sendPunizioneMessage(String)}
     */
    private void sendPunizioneMessage(String message) {
        try {
            DiscordHook.sendPunizioneMessage(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
