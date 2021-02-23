package net.noobsters.kern.paper.databases.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.Getter;
import net.noobsters.kern.paper.databases.types.DatabaseInterface;

public class MongoHynix implements DatabaseInterface {
    private @Getter String URI;
    private @Getter MongoClient mongoClient;

    public MongoHynix(String URI) {
        this.URI = URI;
        this.mongoClient = MongoClients.create(URI);
    }

    @Override
    public boolean connect() throws Exception {
        try {
            System.out.println("Mongo should connect to " + getURI());
            // Obtain db condor just to ping
            var db = mongoClient.getDatabase("condor");
            db.getCollection("punishments");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean disconnect() throws Exception {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isConnected() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String performQuerry(Object... objects) {
        // TODO Auto-generated method stub
        return null;
    }

}
