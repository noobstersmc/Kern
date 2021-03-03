package net.noobsters.kern.paper.condor;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.pojo.PojoCodecProvider;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;

public class CondorManager {
    private @Getter Kern instance;
    private @Getter MongoCollection<CondorProfile> condorCollection;
    private @Getter MongoDatabase mongoDatabase;

    public CondorManager(Kern instance) {
        this.instance = instance;
        this.mongoDatabase = instance.getPunishmentManager().getMongoHynix().getMongoClient().getDatabase("condor");
        this.condorCollection = mongoDatabase.getCollection("condorAuth", CondorProfile.class)
                .withCodecRegistry(CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                        CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())));
        instance.getCommandManager().registerCommand(new CondorProfileCMD(this));
    }

}
