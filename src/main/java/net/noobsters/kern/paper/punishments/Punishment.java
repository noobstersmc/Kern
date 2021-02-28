package net.noobsters.kern.paper.punishments;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class Punishment {
    @BsonProperty(value = "punisher")
    String punisher;
    @BsonProperty(value = "reason")
    String reason;
    @BsonProperty(value = "expiration")
    Long expiration;
    @BsonProperty(value = "creation")
    Long creation = System.currentTimeMillis();
    @BsonProperty(value = "type")
    PunishmentType type;

    public Punishment() {
    }

    public String timeLeft() {
        var millis = expiration - System.currentTimeMillis();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public void performPunishment(MongoCollection<PlayerProfile> collection, PlayerProfile profile) {
        /** Find the player if online */
        var player = Bukkit.getPlayer(UUID.fromString(profile.getUuid()));
        if (player != null && player.isOnline()) {
            /** Perform action based on punishment type */
            switch (this.type) {
                case BAN:
                    player.kickPlayer("You've been banned from the server: " + this.reason);
                    break;
                case MUTE:
                    player.sendMessage("You've been muted for " + this.reason);
                    break;

                default:
                    break;
            }
        }
        /** Write onto the database */
        collection.findOneAndUpdate(Filters.eq("_id", profile.getUuid()), Updates.push(type.getDBName(), this));
    }

}
