package net.noobsters.kern.paper.shield.jcedeno.objects;

import org.bson.codecs.pojo.annotations.BsonId;

import lombok.Data;

/**
 * Utility Object to manage a player's shield.
 */
public @Data class ShieldProfile {
    @BsonId
    private String uuid;
    private String shield;

    public ShieldProfile() {

    }
}
