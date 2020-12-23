package net.noobsters.kern.paper.databases.listeners;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.addToSet;
import static com.mongodb.client.model.Updates.inc;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.databases.DatabaseManager;

@RequiredArgsConstructor(staticName = "of")
public class DatabaseListener implements Listener {
    private @NonNull DatabaseManager dbManager;
    private HashMap<String, Long> connectionTime = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final var player = e.getPlayer();        
        
        var doc = dbManager.isInCollection(player.getUniqueId());
        connectionTime.put(player.getUniqueId().toString(), System.currentTimeMillis());

        if (doc != null) {
            Bukkit.broadcastMessage(doc.toJson());
            var ip = player.getAddress().getHostName();
            var time = doc.get("user_data", Document.class).getLong("played_time");
            dbManager.getCollection().updateOne(eq("_id", player.getUniqueId()), addToSet("user_data.known_ips", ip));
            player.sendMessage("Welcome back. You have " + Math.round(time / 1000) + " seconds played.");

        } else {
            player.sendMessage("Welcome to Noobsters! Enjoy your stay!");
            dbManager.getCollection().insertOne(of(Map.of("_id", player.getUniqueId(), "user_data",
                    of(Map.of("name", player.getName(), "first_join", System.currentTimeMillis(), "played_time", 0)))));
        }
        

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        final var player = e.getPlayer();
        var doc = dbManager.isInCollection(player.getUniqueId());

        if (doc != null) {
            // Update time later
            var time_diff = System.currentTimeMillis() - connectionTime.remove(player.getUniqueId().toString());
            dbManager.getCollection().updateOne(eq("_id", player.getUniqueId()),
                    inc("user_data.played_time", time_diff));

        }

    }

    @SuppressWarnings("all")
    private Document of(Map map) {
        return new Document(map);
    }

}
