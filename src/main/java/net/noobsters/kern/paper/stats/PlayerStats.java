package net.noobsters.kern.paper.stats;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class PlayerStats {
    @BsonId
    private String uuid;
    @BsonProperty(value = "stats")
    private Map<String, Document> stats = new HashMap<>();

    public PlayerStats(String uuid, Map<String, Document> stats) {
        this.uuid = uuid;
        this.stats = stats;
        stats.get("uhc");
    }

    public PlayerStats() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Map<String, Document> getStats() {
        return stats;
    }

    public void setStats(Map<String, Document> stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "PlayerStats(uuid=" + this.uuid + ", stats=" + this.stats + ")";
    }

}