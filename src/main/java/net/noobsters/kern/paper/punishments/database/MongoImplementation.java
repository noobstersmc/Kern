package net.noobsters.kern.paper.punishments.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.Getter;
import lombok.val;

public class MongoImplementation {
    private @Getter MongoClient mongoClient;

    public MongoImplementation() {
        // Check if there is an environment variable for mongo.
        val ENV_URI = System.getenv("HYNX_URI");
        // Connect
        if (ENV_URI != null) {
            this.mongoClient = MongoClients.create(ENV_URI);
        } else {
            this.mongoClient = MongoClients.create();
        }

    }

    public static void main(String[] args) {
        org.apache.log4j.BasicConfigurator.configure();
        var mi = new MongoImplementation();

    }

}
