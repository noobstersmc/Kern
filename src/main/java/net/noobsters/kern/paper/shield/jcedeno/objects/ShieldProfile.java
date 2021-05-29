package net.noobsters.kern.paper.shield.jcedeno.objects;

import java.util.concurrent.CompletableFuture;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.Setter;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;
import net.noobsters.kern.paper.shield.jcedeno.events.PlayerUpdatedShieldEvent;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldNotFoundException;

/**
 * Utility Object to manage a player's shield.
 */
public class ShieldProfile {
    @BsonId
    private @Getter @Setter String uuid;
    private @Getter @Setter String shield;

    public ShieldProfile() {
    }

    /**
     * Helper utility that updates the shield name locally and on the database.
     * 
     * @param uuid       Player's UUID
     * @param shield     Name of the shield
     * @param collection Collection to be updated
     * @param shieldCollection Shield collection to get the shield data from.
     */
    public void updateOnCollection(String uuid, String shield, MongoCollection<ShieldProfile> collection,
            MongoCollection<CustomShield> shieldCollection) {
        CompletableFuture.supplyAsync(() -> {
            var actualShield = shieldCollection.find(Filters.eq(shield)).first();
            if (actualShield == null)
                try {
                    throw new ShieldNotFoundException("Shield " + shield + " could not be found on the collection "
                            + shieldCollection.getNamespace());
                } catch (ShieldNotFoundException e1) {
                    e1.printStackTrace();
                    return e1;
                }
            // Update the local cache
            ShieldManager.getShieldLocalCache().put(actualShield.getName(), actualShield);
            // Call mongo to update the shield on the player's profile
            var shieldUpdate = collection.findOneAndUpdate(Filters.eq(uuid),
                    Updates.set("shield", actualShield.getName()));
            if (shieldUpdate != null) {
                // Update locally
                setShield(shield);
                // Call the event for further updates.
                Bukkit.getPluginManager().callEvent(new PlayerUpdatedShieldEvent(shieldUpdate, actualShield, true));
            }
            return shieldUpdate;
        });
    }
}
