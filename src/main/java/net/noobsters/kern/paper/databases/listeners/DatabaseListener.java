package net.noobsters.kern.paper.databases.listeners;

import java.util.Map;
import java.util.Random;

import com.mongodb.BasicDBObject;

import org.bson.Document;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.databases.DatabaseManager;

@RequiredArgsConstructor(staticName = "of")
public class DatabaseListener implements Listener {
    private @NonNull DatabaseManager dbManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        final var player = e.getPlayer();
        var doc = dbManager.isInCollection(player.getUniqueId());

        if (doc != null) {
            System.out.println("reading from db");
            dbManager.getCollection().updateOne(new BasicDBObject().append("_id", player.getUniqueId()),
                    of(Map.of("$inc", Map.of("user_data.played_time", new Random().nextLong()))));
            System.out.println(doc.toJson());
        } else {
            System.out.println("Adding to db");
            dbManager.getCollection().insertOne(of(Map.of("_id", player.getUniqueId(), "user_data",
                    of(Map.of("name", player.getName(), "first_join", System.currentTimeMillis(), "played_time", 0)))));
        }

    }

    @SuppressWarnings("all")
    private Document of(Map map) {
        return new Document(map);
    }

}
