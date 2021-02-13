package net.noobsters.kern.paper.punishments.database;

import java.util.UUID;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.bukkit.Bukkit;

import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.Getter;
import net.noobsters.kern.paper.punishments.database.objects.Mute;

public class MongoImplementation {
    private MongoClient mongoClient;
    private @Getter Datastore datastore;

    public MongoImplementation(int i) {
        /* Create the client */
        this.mongoClient = MongoClients.create(
                "mongodb+srv://admin:Henixceo1%21@cluster0.tpjaz.mongodb.net/admin&w=majority?authSource=admin&replicaSet=atlas-ublxsn-shard-0&readPreference=primary&appname=MongoDB%20Compass&ssl=true");
        /* Setup Morphia datastore */
        this.datastore = Morphia.createDatastore(this.mongoClient, "kern_morphia");
        /* Tell morphia where the classes are to process them */
        datastore.getMapper().mapPackage("net.noobsters.kern.paper.punishments.database.objects");
        datastore.ensureIndexes();

        /* AleIV's UUID */
        var AleIV = UUID.randomUUID();
        /* Random player */
        var RandomPlayer = Bukkit.getOnlinePlayers().stream().findAny().get();

        
        /* Create the request */
        Mute.create(
                /* What's the reason for this punishment? */
                "Toxicity",
                /* Who is creating this punishment? [AleIV] */
                AleIV,
                /* When does this request start? [Now] */
                System.currentTimeMillis(),
                /* When should the request expire? [Now + 1h] */
                System.currentTimeMillis() + 3_600_000)
                /* Who should be punished by this request? [RandomPlayer] */
                .execute(RandomPlayer); // A random player will be muted LOL

    }

}
