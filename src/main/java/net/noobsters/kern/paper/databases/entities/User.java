package net.noobsters.kern.paper.databases.entities;

import java.util.Map;
import java.util.UUID;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.databases.DatabaseManager;

@Data
@AllArgsConstructor(staticName = "of")
public class User {
    UUID _id;
    PlayerData user_data;
    
    public Document getAsDocument(){
        return new Document(Map.of("_id", _id, "user_data", user_data.toString()));
    }
    public String toJson(){
        return DatabaseManager.getGson().toJson(this);
    }
}
