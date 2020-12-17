package net.noobsters.kern.paper.databases;

import java.util.Optional;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.UuidRepresentation;
import org.bson.codecs.UuidCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bukkit.Bukkit;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.databases.entities.PlayerData;
import net.noobsters.kern.paper.databases.entities.User;
import net.noobsters.kern.paper.databases.listeners.DatabaseListener;

public class DatabaseManager {
    private @Getter static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private @Getter Kern kern;
    /* All mongo stuff */
    private @Getter MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(
                    "mongodb://admin:puto@localhost:27017/?authSource=admin&readPreference=primary&appname=MongoDB%20Compass&ssl=false"))
            .codecRegistry(codecRegistries()).retryWrites(true).build());
    private @Getter MongoDatabase database = mongoClient.getDatabase("Kern");
    private @Getter MongoCollection<Document> collection = database.getCollection("users");

    public DatabaseManager(final Kern kern) {
        this.kern = kern;
        Bukkit.getServer().getPluginManager().registerEvents(DatabaseListener.of(this), kern);
    }

    /* CodecRegistry to support UUID in Mongo */
    private CodecRegistry codecRegistries() {
        return CodecRegistries.fromRegistries(
                CodecRegistries.fromProviders(new UuidCodecProvider(UuidRepresentation.STANDARD)),
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    }

    public User getUser(UUID uuid) {
        var query = new BasicDBObject("_id", uuid);
        var cursor = collection.find(query);
        var document = cursor.first();

        if (document != null) {
            System.out.println("Exists");
            System.out.println(document.toJson());

            return null;
        }

        System.out.println("Doesn't exist");
        var user = User.of(uuid, PlayerData.of("whitelist"));
        System.out.println(user.toJson());
        collection.insertOne(user.getAsDocument());

        return null;
    }

}
