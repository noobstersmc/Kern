package net.noobsters.kern.paper.punishments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.val;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

/**
 * PunishmentManager
 */
public class PunishmentManager {
    private @Getter DatabasesConfig dbConfig = DatabasesConfig.of("databases");
    private @Getter MongoHynix mongoHynix;
    private @Getter MongoCollection<PlayerProfile> collection;
    // Hashmap that keeps near up to date information about the profile of players.
    private static @Getter HashMap<String, Timestamp<PlayerProfile>> playerProfileMap = new HashMap<>();
    // private @Getter HashMap<String, PlayerProfile> cache;
    private @Getter Kern instance;

    

    public PunishmentManager(Kern instance) {
        this.instance = instance;
        // create codec registry for POJOs
        var pojoCodec = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        this.mongoHynix = MongoHynix.createFromJson(dbConfig);
        this.collection = mongoHynix.getMongoClient().getDatabase("condor").withCodecRegistry(pojoCodec)
                .getCollection("punishments", PlayerProfile.class);
        Bukkit.getPluginManager().registerEvents(new PunishmentListeners(this), instance);
        instance.getCommandManager().registerCommand(new PunishmentCommand(instance));

    }

    public static PlayerProfile getProfile(UUID uuid) {
        return getProfile(uuid.toString());
    }

    public static PlayerProfile getProfile(String uuid) {
        var profile = playerProfileMap.get(uuid);
        if (profile != null) {
            /** If data is too old, submit an update request */
            if (profile.age() >= 5_000) {
                Bukkit.getScheduler().runTaskAsynchronously(Kern.getInstance(), () -> {

                });
            }

            return profile.getObject();

        }
        return playerProfileMap.get(uuid).getObject();

    }

    /**
     * Updates the cached
     * 
     * @param profile
     */
    private void updateCache(final PlayerProfile profile) {
        /** Update the cache hashmap */
        if (!playerProfileMap.containsKey(profile.getUuid())) {
            playerProfileMap.put(profile.getUuid(), new Timestamp<PlayerProfile>(profile));
        } else {
            var oldTimestamp = playerProfileMap.get(profile.getUuid());
            oldTimestamp.setObject(profile);
        }

    }

    /**
     * Gets or creates a punishment profile for the UUID of a given player name.
     * 
     * @param name Name of the player in question, UUID as string also works.
     * @return PlayerProfile is the player exists.
     */
    public PlayerProfile getOrCreatePlayerProfile(String name) {
        /** IF the provided name is actually a UUID, just use that one instead. */
        var nameAsUUID = getId(name);
        /** Query the uuid is nameAsUUID is null */
        var uuid = nameAsUUID != null ? nameAsUUID : findUUIDOfName(name);
        if (uuid != null) {
            val profile = getOrCreatePlayerProfile(uuid);
            if (profile != null) {
                updateCache(profile);
            }
            return profile;
        }else{

        }
        /** If nothing found return null */
        return null;
    }

    /**
     * Gets or creates a punishment profile for a UUID.
     * 
     * @param uuid UUID of the player you wish to create a profile
     * @return PlayerProfile of the provided UUID
     */
    public PlayerProfile getOrCreatePlayerProfile(UUID uuid) {
        /** Find the profile for this uuid */
        var query = queryPlayerIfPresent(uuid.toString());
        /** If found, return it */
        if (query != null)
            return query;
        /** If not found, create one and return it */
        /** TODO: Improve this piece of code to obtain the player's name */
        var name = findNameOfUUID(uuid);
        if (name == null) {
            var playerRecord = PlayerDBUtil.getPlayerObject(uuid.toString());
            if (playerRecord != null)
                name = playerRecord.get("username").getAsString();
        }
        /** Create the player profile */
        var nProfile = new PlayerProfile(uuid.toString(), name != null ? name : "Unknown", Collections.emptyList(),
                Collections.emptyList(), new ArrayList<String>(), "");

        /** Insert it into the collection */
        collection.insertOne(nProfile);
        return nProfile;
    }

    /**
     * Attempt to locate a player's profile if they've ever joined before.
     * 
     * @param nameOrID UUID of the Player, or current displayname.
     * @return PlayerProfile if found, otherwise null.
     */
    public PlayerProfile queryPlayerIfPresent(String nameOrID) {
        /** Attempt to parse into UUID, if not parseable, it must be player name. */
        var id = getId(nameOrID);
        /** Query the UUID of the player's name */
        if (id == null) {
            var uuid = findUUIDOfName(nameOrID);
            /** If null return */
            if (uuid == null)
                return null;
            /** Otherwise, set the id to the newly found UUID */
            id = uuid;
        }
        /** Return the first match or null if none is found. */
        return collection.find(Filters.eq("_id", id.toString())).first();
    }

    /**
     * Function that returns the UUID of a Player's name if they exist.
     * 
     * @param name Player name to be querried locally and in playerdb.
     * @return UUID of player if found, otherwhise null.
     */
    public UUID findUUIDOfName(String name) {
        /** Ask bukkit if they know the UUID of that playerName */
        var offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
        /** If not found, move to querying playerdb */
        if (offlinePlayer == null) {
            /** Query from playerdb.co */
            var playerProf = PlayerDBUtil.getPlayerObject(name);
            /** If not found, the player doesn't even exist, so return. */
            if (playerProf == null)
                return null;
            /** If found, parse and return */
            return getId(playerProf.get("id").getAsString());
        }
        /** If found in local cache, return */
        return offlinePlayer.getUniqueId();
    }

    /**
     * Function to obtain the name of a player's uuid.
     * 
     * @param uuid UUID of the given player
     * @return Player name if they exist, otherwise null.
     */
    public String findNameOfUUID(UUID uuid) {
        var offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.getName() != null)
            return offlinePlayer.getName();
        /**
         * TODO: Add cached way to pull the playerdb.co name object again without
         * querying it twice or more times. Use caffeine maybe. In the mean time return
         * null.
         */
        return null;

    }

    /**
     * Helper function to return null if string is not parseable as UUID.
     * 
     * @param uuid UUID to be parsed in string form. Must be full (with the - ) and
     *             not trimmed.
     * @return UUID or null is not parseable.
     */
    private UUID getId(String uuid) {
        try {
            return UUID.fromString(uuid);
        } catch (Exception e) {
        }
        return null;
    }

}