package net.noobsters.kern.paper.databases;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import com.mongodb.MongoClientSettings;

import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import lombok.Data;
import lombok.val;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;

public class Demo {
    public static void main(String[] args) throws Exception {
        // Obtain a json config
        val dbConfig = DatabasesConfig.of("databases");
        // Use it to create a Monhgo Hynix instance
        val mHynx = MongoHynix.createFromJson(dbConfig);
        // Obtain the actual MongoClient. Mongo required in classpath.
        val mongo = mHynx.getMongoClient();
        // Obain a database inside mongo
        var pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        var db = mongo.getDatabase("condor").withCodecRegistry(pojoCodecRegistry);
        var tokens = db.getCollection("auth", CondorUser.class);
        // var query = BsonDocument.parse("{credits: {$eq: 0, $lte: 1}}");
        var tokensIterator = tokens.find().iterator();

        while (tokensIterator.hasNext()) {
            var next = tokensIterator.next();
            System.out.println(next.toString());
        }

    }

    @Data
    public static class CondorUser {
        @BsonId
        ObjectId id;
        @BsonProperty("token")
        String token;
        @BsonProperty("credits")
        int credits;
        @BsonProperty("instance_limit")
        int instance_limit;
        @BsonProperty("name")
        String name;
    }
}
