package net.noobsters.kern.paper.condor;

import com.google.common.collect.ImmutableList;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.shield.CustomShield;
import net.noobsters.kern.paper.stats.PlayerStats;

public class CondorManager {
    private @Getter Kern instance;
    private @Getter MongoCollection<CondorProfile> condorCollection;
    private @Getter MongoDatabase mongoDatabase;

    public CondorManager(Kern instance) {
        this.instance = instance;
        /** Make this database aware of the other types that might be used. */
        this.mongoDatabase = instance.getProfileManager().getMongoHynix().getMongoClient().getDatabase("condor")
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder()
                                .register(CustomShield.class, PlayerStats.class).automatic(true).build())));
        ;
        this.condorCollection = mongoDatabase.getCollection("auth", CondorProfile.class);

        instance.getCommandManager().getCommandCompletions().registerAsyncCompletion("condor_fields", c -> {
            return ImmutableList.of("name", "token", "credits", "limit", "super");
        });
        instance.getCommandManager().registerCommand(new CondorProfileCMD(this));
    }

}
