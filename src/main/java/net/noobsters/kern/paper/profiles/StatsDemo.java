package net.noobsters.kern.paper.profiles;

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

import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;

public class StatsDemo {

    public static void main(String[] args) {
        /** Obtain mongo hynix object of database.json file */
        MongoHynix mongoHynix = MongoHynix.createFromJson(DatabasesConfig.of("databases"));
        /** Obtain mongo client, and then condor database with the necessary codec. */
        MongoDatabase condorDatabase = mongoHynix.getMongoClient().getDatabase("condor")
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        /** Obtain the punishments collection as a collection of PlayerStats */
        MongoCollection<PlayerStats> statsCollection = condorDatabase.getCollection("punishments", PlayerStats.class);

        statsCollection.find(Filters.exists("stats.uhc")).limit(10).forEach(System.out::println);
        CompletableFuture.runAsync(() -> {
            
        });

        PlayerStats stats = statsCollection.findOneAndUpdate(Filters.eq("name", "AleIV"),
                Updates.inc("stats.uhc.kills", -5), new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));

        stats.getStats().get("uhc").getInteger("deaths", 10);

        System.out.println(stats == null ? "AleIV no encontrado" : stats);
    }

}
