package net.noobsters.kern.paper.punishments;

import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerProfile {
    @BsonId
    String uuid;
    @BsonProperty(value = "name")
    String name;
    @BsonProperty(value = "bans")
    List<Punishment> bans;
    @BsonProperty(value = "mutes")
    List<Punishment> mutes;

    public PlayerProfile() {

    }

    public Punishment isBanned() {
        for (var ban : bans) {
            if (ban.expiration > System.currentTimeMillis() && !ban.getCanceled()) {
                return ban;
            }
        }
        return null;
    }

    public Punishment isMuted() {
        for (var mute : mutes) {
            if (mute.expiration > System.currentTimeMillis() && !mute.getCanceled()) {
                return mute;
            }
        }
        return null;
    }

    public PlayerProfile addPunishment(MongoCollection<PlayerProfile> collection, Punishment punishment) {
        return collection.findOneAndUpdate(eq("_id", uuid), Updates.push(punishment.getType().getDBName(), punishment));
    }

}
