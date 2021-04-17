package net.noobsters.kern.paper.profiles;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.punishments.Punishment;
import net.noobsters.kern.paper.punishments.PunishmentType;
import net.noobsters.kern.paper.punishments.events.PlayerBannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnmutedEvent;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;

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

    public long obtainLastTimeOnline(){
        return states.get(states.size()-1).getTime();
    }

    /**
     * Static constructor to quickly create a player profile
     * 
     * @param uuid    Stringified UUID of the player
     * @param name    Name of the player
     * @param address Current address of the player
     * @return
     */
    public static PlayerProfile create(String uuid, String name, String address) {
        return new PlayerProfile(uuid, name, address);
    }

    /**
     * Static constructor to quickly create a player profile
     * 
     * @param uuid    UUID of the player
     * @param name    Name of the player
     * @param address Current address of the player
     * @return
     */
    public static PlayerProfile create(UUID uuid, String name, String address) {
        return create(uuid.toString(), name, address);
    }

    /** Empty constructor for mongo */
    public PlayerProfile() {
    }

    /**
     * Utility function to obtain the first ban that is still active.
     * 
     * @return Punishment or null is no active punishment was found.
     */
    public Punishment isBanned() {
        for (var ban : bans)
            if (ban.obtainActive())
                return ban;

        return null;
    }

    /**
     * Utility function to obtain the first mute that is still active.
     * 
     * @return Punishment or null is no active punishment was found.
     */
    public Punishment isMuted() {
        for (var mute : mutes)
            if (mute.obtainActive())
                return mute;

        return null;
    }

    /**
     * Method to asyncronously update the points of a player both locally and in
     * database.
     * 
     * @param newPoints  The change in points. Positive will increase, negative will
     *                   decrease.
     * @param collection The database to which you wish to commit this change
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitIncreaseOfPoints(int newPoints, MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            findOneAndUpdate(collection, Updates.inc("points", newPoints));
            points += newPoints;
            return true;
        }).handle(ExceptionHandlers::handleException);

    }

    /**
     * Method to asyncronously update the penalty points of a user.
     * 
     * @param newPoints  The change in points. Positive will increase, negative will
     *                   decrease.
     * @param collection The database collection to which you wish to commit this
     *                   change
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitPenalty(int penaltyPoints, MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            findOneAndUpdate(collection, Updates.inc("penalties", penaltyPoints));
            penalties += penaltyPoints;
            return true;
        }).handle(ExceptionHandlers::handleException);
    }

    /**
     * Method to asyncronously update the name of the currently active shield.
     * 
     * @param newShieldName Name of the new shield to be added.
     * @param collection    The database collection to which you wish to commit this
     *                      change
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitChangeOfShield(String newShieldName,
            MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            findOneAndUpdate(collection, Updates.set("shield", newShieldName));
            this.activeShield = newShieldName;
            return true;
        }).handle(ExceptionHandlers::handleException);
    }

    /**
     * Method to asyncronously add a new address to the list of known addresses of a
     * player if not already present.
     * 
     * @param address    Address to be added.
     * @param collection The database collection to which you wish to commit the
     *                   change.
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitAddress(String address, MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            findOneAndUpdate(collection, Updates.addToSet("addresses", address));
            if (!addresses.contains(address)) {
                addresses.add(address);
            }
            return true;
        }).handle(ExceptionHandlers::handleException);

    }

    /**
     * Method to commit a connection or disconnection of a player to a server
     * 
     * @param state      {@link State} of either connected or disconnected
     * @param collection The database collection to which you wish to commit the
     *                   change.
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitChangeOfState(State state, MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            findOneAndUpdate(collection, Updates.addToSet("state", state));
            return true;
        }).handle(ExceptionHandlers::handleException);
    }

    /**
     * Method to commit a punishment to a player's profile. It also calls
     * PlayerMutedEvent or PlayerMutedEvent respectively.
     * 
     * @param punishment Either a ban or mute to be commited.
     * @param collection The database collection to which you wish to commit the
     *                   change.
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> commitPunishment(Punishment punishment,
            MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            /** Update the database */
            var type = punishment.getType();
            findOneAndUpdate(collection, Updates.push(type.getDBName(), punishment));
            /** Update the local copy if everything went correctly */
            if (type == PunishmentType.BAN) {
                bans.add(punishment);
            } else if (type == PunishmentType.MUTE) {
                mutes.add(punishment);
            }
            /** Call the event with the updated data */
            Bukkit.getPluginManager().callEvent(punishment.getEvent(this, true));
            return true;
        }).handle(ExceptionHandlers::handleException);
    }

    /**
     * Method to commit a cancel an active punishment from a player's profile. It
     * also calls {@link PlayerBannedEvent} or {@link PlayerUnmutedEvent}
     * respectively.
     * 
     * @param punishment Either a ban or mute to be commited.
     * @param collection The database collection to which you wish to commit the
     *                   change.
     * @return CompletableFuture<Boolean> with true signifying the change took place
     *         and false signifying and exception.
     */
    public CompletableFuture<Boolean> pardonPunishment(final Punishment punishment,
            MongoCollection<PlayerProfile> collection) {
        return CompletableFuture.supplyAsync(() -> {
            var punishmentDbName = punishment.getType().getDBName();
            var filter = Filters.and(eq("_id", this.getUuid()),
                    Filters.elemMatch(punishmentDbName, punishment.obtainMatchingFilter()));
            var updateResult = collection.updateOne(filter, Updates.set(punishmentDbName + ".$.canceled", true));
            if (updateResult.getMatchedCount() > 0) {
                punishment.setCanceled(true);
                Bukkit.getPluginManager().callEvent(punishment.getPardonEvent(this, true));
                return true;
            }

            return false;
        }).handle(ExceptionHandlers::handleException);
    }

    /**
     * Utility function to update the profile of the current player
     * 
     * @param collection Collection to perform the query on.
     * @param update     Generic update to be performed.
     * @return PlayerProfile, if one is found, before the update is applied.
     */
    private PlayerProfile findOneAndUpdate(MongoCollection<PlayerProfile> collection, Bson update) {
        return collection.findOneAndUpdate(eq("_id", uuid), update);
    }

}
