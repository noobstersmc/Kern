package net.noobsters.kern.paper.punishments;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;

public class PunishmentsManager {
    private @Getter Kern instance;
    private @Getter DatabasesConfig databasesJson = DatabasesConfig.of("databases");
    private @Getter MongoHynix mongoHynix;
    private @Getter MongoCollection<PunishmentProfile> profiles;

    public PunishmentsManager(Kern instance) {
        this.instance = instance;
        this.mongoHynix = MongoHynix.createFromJson(this.databasesJson);

        var client = mongoHynix.getMongoClient();
        var database = client.getDatabase("condor");
        // Boilerplate necesario para usar objetos
        var pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        this.profiles = database.getCollection("punishments", PunishmentProfile.class)
                .withCodecRegistry(pojoCodecRegistry);

    }

    public static void main(String[] args) {
        var manager = new PunishmentsManager(null);
    }

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class PunishmentProfile {
        @BsonId
        String uuid;
        @BsonProperty(value = "name")
        String name;
        @BsonProperty(value = "previousNames")
        List<String> previousNames;
        @BsonProperty(value = "ips")
        List<String> ips;
        @BsonProperty(value = "activePunishments")
        List<Object> activePunishments;

        private static PunishmentProfile fromCollection(MongoCollection<PunishmentProfile> collection, UUID uuid,
                String name) {
            var profile = collection.find(Filters.eq("_id", uuid.toString())).first();
            if (profile != null) {
                // Profile exists, return it.
                return profile;
            }
            // Profile doesn't exist, veriy the uuid and create a new one.
            var newProfile = PunishmentProfile.of(uuid.toString(), name, Collections.emptyList(),
                    Collections.emptyList(), Collections.emptyList());
            if (name != null && name.length() > 0) {
                // ASK FOR THE DATA MAYBE?

            }

            return null;
        }

    }

}
