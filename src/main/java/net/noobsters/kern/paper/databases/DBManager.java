package net.noobsters.kern.paper.databases;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.noobsters.kern.paper.configs.DatabasesConfig;
import net.noobsters.kern.paper.databases.impl.MongoHynix;
import net.noobsters.kern.paper.databases.types.DatabaseInterface;

public class DBManager {
    private @Getter DatabasesConfig databasesConfig;
    private @Getter List<DatabaseInterface> databases;

    public DBManager() {
        this.databases = new ArrayList<>();
        try {
            this.databasesConfig = new DatabasesConfig();

            var URI = databasesConfig.getMongoURI();

            databases.add(new MongoHynix(URI));

        } catch (Exception e) {
            System.err.println("[Kern] Can't load database config. Please check your file.");
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        var DBManager = new DBManager();
        DBManager.getDatabases().get(0).connect();
        while(true){
            
        }
    }

}
