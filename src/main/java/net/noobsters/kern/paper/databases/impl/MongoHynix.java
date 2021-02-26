package net.noobsters.kern.paper.databases.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.Getter;
import net.noobsters.kern.paper.utils.JsonConfig;

public class MongoHynix {
    private @Getter String URI;
    private @Getter MongoClient mongoClient;

    public MongoHynix(String URI) {
        this.URI = URI;
        this.mongoClient = MongoClients.create(URI);
    }

    /**
     * Creates a MongoHynix Client from a simple jsonConfig object with primitive
     * mongo-uri.
     * 
     * @param jsonConfig with mongo-uri as an element.
     * @return MongoHynix instance or null
     */
    public static MongoHynix createFromJson(JsonConfig jsonConfig) {
        var jsonURI = jsonConfig.getJsonObject().getAsJsonPrimitive("mongodb-connection-uri");
        if (jsonURI != null && jsonURI.isString()) {
            return new MongoHynix(jsonURI.getAsString());
        }
        System.err.println("[MongoHynix] No mongodb-connection-uri was found on " + jsonConfig.getFile().getName());
        return null;
    }
}
