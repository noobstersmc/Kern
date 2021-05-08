package net.noobsters.kern.paper.stats;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;

@RequiredArgsConstructor
public @Data class StatsManager {
    private @NonNull Kern instance;
    
    MongoHynix mongoHynix = MongoHynix.createFromJson(DatabasesConfig.of("databases"));
    MongoDatabase condorDatabase = mongoHynix.getMongoClient().getDatabase("condor")
            .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                    CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
    MongoCollection<PlayerStats> statsCollection = condorDatabase.getCollection("punishments", PlayerStats.class);

    public void updatePlayerUHCStat(String uuid, String field, int amount){
        statsCollection.findOneAndUpdate(Filters.eq(uuid), Updates.inc("stats.uhc." + field, 1));
    }
}
