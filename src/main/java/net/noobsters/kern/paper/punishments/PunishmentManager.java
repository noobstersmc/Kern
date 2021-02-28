package net.noobsters.kern.paper.punishments;

import static com.mongodb.client.model.Filters.eq;

import java.util.Collections;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

/**
 * PunishmentManager
 */
public class PunishmentManager {
    // private Kern instance;
    private @Getter DatabasesConfig dbConfig = DatabasesConfig.of("databases");
    private @Getter MongoHynix mongoHynix;
    private @Getter MongoCollection<PlayerProfile> collection;
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

    public PlayerProfile getOrCreatePlayerProfile(String uuid) {
        var query = collection.find(eq("_id", uuid));
        var first = query.first();
        if (first == null) {
            var name = PlayerDBUtil.getPlayerObject(uuid).get("username");
            var nProfile = new PlayerProfile(uuid, name != null ? name.getAsString() : "Unknown",
                    Collections.emptyList(), Collections.emptyList());
            collection.insertOne(nProfile);
            return nProfile;
        }
        return first;
    }

}