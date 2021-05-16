package net.noobsters.kern.paper.shield.jcedeno.objects;

import org.bson.codecs.pojo.annotations.BsonId;

import lombok.Getter;
import lombok.Setter;

/**
 * Utility Object to manage a player's shield.
 */
public class ShieldProfile {
    @BsonId
    private @Getter @Setter String uuid;
    private @Getter @Setter String shield;

    public ShieldProfile() {
    }
}
