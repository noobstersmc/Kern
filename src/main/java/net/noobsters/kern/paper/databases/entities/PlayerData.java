package net.noobsters.kern.paper.databases.entities;

import org.bson.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.databases.DatabaseManager;

@Data
@AllArgsConstructor(staticName = "of")
public class PlayerData {
        Object whitelist;

        @Override
        public String toString(){
            return whitelist.toString();            
        }

}
