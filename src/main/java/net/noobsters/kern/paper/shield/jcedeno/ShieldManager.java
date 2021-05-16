package net.noobsters.kern.paper.shield.jcedeno;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.shield.jcedeno.commands.ShieldCMD;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldNotFoundException;
import net.noobsters.kern.paper.shield.jcedeno.listeners.ShieldListener;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;
import net.noobsters.kern.paper.shield.jcedeno.objects.ShieldProfile;

public class ShieldManager {
    private @Getter Kern instance;
    private @Getter ShieldListener shieldListener;
    private @Getter ShieldCMD shieldCMD;
    private @Getter MongoDatabase mongoDatabase;
    private @Getter MongoCollection<CustomShield> shieldCollection;
    private @Getter MongoCollection<ShieldProfile> shieldProfileCollection;
    /** Initialize the cache */
    private static @Getter Map<String, CustomShield> shieldLocalCache = new HashMap<>();
    private static @Getter Map<String, ShieldProfile> shieldProfileCache = new HashMap<>();

    public ShieldManager(final Kern instance) {
        this.instance = instance;

        /** Instantiate the listener and command */
        this.shieldListener = new ShieldListener(this);
        this.shieldCMD = new ShieldCMD(this);

        /** Create a connection to the database */
        var mongoHynix = MongoHynix.createFromJson(DatabasesConfig.of("databases"));
        /** Obtain mongo client, and then condor database with the necessary codec. */
        this.mongoDatabase = mongoHynix.getMongoClient().getDatabase("jcedeno")
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        /** Obtain the shield collection as a colelction of CustomShields. */
        this.shieldCollection = mongoDatabase.getCollection("shield", CustomShield.class);
        /** Copy the contents onto the local cache */
        shieldCollection.find().forEach(all -> shieldLocalCache.put(all.getName(), all));
        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            shieldCollection.watch().forEach(shields -> {
                var doc = shields.getFullDocument();
                System.out.println("Updating shield: " + doc.getName());
                shieldLocalCache.put(doc.getName(), doc);
            });
        });
        /** Obtain the punishments collection as a collection of ShieldProfiles */
        this.shieldProfileCollection = instance.getCondorManager().getMongoDatabase()
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())))
                .getCollection("punishments", ShieldProfile.class);
    }

    /**
     * Function that obtains the currently active shield of a player.
     * 
     * @param id Player UUID to be queried.
     * @return {@link ShieldProfile} if it exists, null if it doesn't.
     */
    public ShieldProfile getShieldProfile(String id) {
        var prof = shieldProfileCollection.find(Filters.eq("_id", id)).first();
        return prof;
    }

    /**
     * Utility function that updates the shield selected by a player to a different
     * one.
     * 
     * @param id        Player UUID to be updated.
     * @param shield_id Unique shield ID.
     * @return {@link ShieldProfile} if both the profile and the shield exists.
     * 
     * @throws ShieldNotFoundException When the provided shield_id does not exist.
     */
    public ShieldProfile changeShield(String id, String shield_id) throws ShieldNotFoundException {
        /** Check if the provided shield exists */
        var shield = shieldCollection.find(Filters.eq(shield_id)).first();
        /** If null, throw exception */
        if (shield == null)
            throw new ShieldNotFoundException(
                    "Shield " + shield_id + " could not be found on the collection " + shieldCollection.getNamespace());
        /** Otherwise, update the player's profile and return it */
        return shieldProfileCollection.findOneAndUpdate(Filters.eq(id), Updates.set("shield", shield.getName()),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

    }

    /**
     * Utility function that pulls a shield from the database and puts it in cache.
     * 
     * @param id Player UUID to be updated.
     * @return {@link CustomShield} object or null if not existant.
     */
    public CustomShield getShield(String id) {
        /** Query shield */
        var shield = shieldCollection.find(Filters.eq(id)).first();
        /** Put in local cache */
        shieldLocalCache.put(id, shield);
        return shield;
    }

    /**
     * Helper function that pulls the shield from local cache and requeries for new
     * data.
     * 
     * @param id Player UUID to be updated.
     * @return {@link CustomShield} object or null if not existant.
     */
    public CustomShield getShieldFromCache(String id) {
        var cached = shieldLocalCache.get(id);
        /** If null ask the database to query it */
        if (cached == null) {
            CompletableFuture.runAsync(() -> getShield(id));
        }

        return cached;
    }

}
