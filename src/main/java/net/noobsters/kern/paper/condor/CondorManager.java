package net.noobsters.kern.paper.condor;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;

public class CondorManager {
    private @Getter Kern instance;
    private @Getter MongoCollection<CondorProfile> condorCollection;
    private @Getter MongoDatabase mongoDatabase;

    public CondorManager(Kern instance) {
        this.instance = instance;
        this.mongoDatabase = instance.getPunishmentManager().getMongoHynix().getMongoClient().getDatabase("condor");
        this.condorCollection = mongoDatabase.getCollection("condorAuth", CondorProfile.class);
        instance.getCommandManager().registerCommand(new CondorProfileCMD(this));
    }

}
