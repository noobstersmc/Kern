package net.noobsters.kern.paper.shield.jcedeno;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.shield.jcedeno.commands.ShieldCMD;
import net.noobsters.kern.paper.shield.jcedeno.listeners.ShieldListener;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;

public class ShieldManager {
    private @Getter Kern instance;
    private @Getter ShieldListener shieldListener;
    private @Getter ShieldCMD shieldCMD;
    private @Getter MongoDatabase mongoDatabase;
    private @Getter MongoCollection<CustomShield> shieldCollection;
    /** Initialize the cache */
    private static @Getter Map<String, CustomShield> shieldLocalCache = new HashMap<>();

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
        /** Obtain the punishments collection as a collection of PlayerStats */
        this.shieldCollection = mongoDatabase.getCollection("shield", CustomShield.class);
    }

}
