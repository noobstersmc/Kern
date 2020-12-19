package net.noobsters.kern.paper.databases.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class PlayerData {
        Object whitelist;

        @Override
        public String toString(){
            return whitelist.toString();            
        }

}
