package net.noobsters.kern.paper.profiles;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.punishments.Punishment;

@Data
@AllArgsConstructor
public class PlayerProfile {
    @BsonId
    String uuid;
    @BsonProperty(value = "name")
    String name;
    @BsonProperty(value = "bans")
    ArrayList<Punishment> bans;
    @BsonProperty(value = "mutes")
    ArrayList<Punishment> mutes;
    @BsonProperty(value = "addresses")
    ArrayList<String> addresses;
    @BsonProperty(value = "state")
    ArrayList<State> states;
    @BsonProperty(value = "firstJoin")
    Long firstJoin;
    @BsonProperty(value = "shield")
    String activeShield;
    @BsonProperty(value = "penalties")
    Integer penalties = 0;
    @BsonProperty(value = "points")
    Integer points = 0;

    /** Empty constructor for mongo */
    public PlayerProfile() {

    }

    public Punishment isBanned() {
        for (var ban : bans) {
            if (ban.getExpiration() > System.currentTimeMillis() && !ban.getCanceled()) {
                return ban;
            }
        }
        return null;
    }

    public Punishment isMuted() {
        for (var mute : mutes) {
            if (mute.getExpiration() > System.currentTimeMillis() && !mute.getCanceled()) {
                return mute;
            }
        }
        return null;
    }

    public void addAdress(String address) {
        if (!addresses.contains(address)) {
            addresses.add(address);
        }
    }

    public void commitNewAddress(String address, MongoCollection<PlayerProfile> collection) {
        Bukkit.getScheduler().runTaskAsynchronously(Kern.getInstance(),
                () -> collection.findOneAndUpdate(eq("_id", uuid), Updates.addToSet("addresses", address))

        );
    }

    public void commitChangeOfState(MongoCollection<PlayerProfile> collection, State state) {
        Bukkit.getScheduler().runTaskAsynchronously(Kern.getInstance(),
                () -> collection.findOneAndUpdate(eq("_id", uuid), Updates.addToSet("state", state)));
    }

    public void commitPunishment(MongoCollection<PlayerProfile> collection, Punishment punishment) {
        // Add it to the local copy
        switch (punishment.getType()) {
        case BAN: {
            bans.add(punishment);
            break;
        }
        case MUTE: {
            mutes.add(punishment);
            break;
        }
        default: {
            break;
        }
        }
        // Commit it to the database
        Bukkit.getScheduler().runTaskAsynchronously(Kern.getInstance(), () -> collection
                .findOneAndUpdate(eq("_id", uuid), Updates.push(punishment.getType().getDBName(), punishment)));
    }

    public PlayerProfile(String uuid, String name, String address) {
        this.uuid = uuid;
        this.name = name;
        this.addresses = new ArrayList<String>(List.of(address));
        this.bans = new ArrayList<Punishment>();
        this.mutes = new ArrayList<Punishment>();
        this.states = new ArrayList<State>();
        this.activeShield = "";
        this.firstJoin = System.currentTimeMillis();
    }

    public static PlayerProfile create(String uuid, String name, String address) {
        return new PlayerProfile(uuid, name, address);
    }

    public static PlayerProfile create(UUID uuid, String name, String address) {
        return create(uuid.toString(), name, address);
    }

}
