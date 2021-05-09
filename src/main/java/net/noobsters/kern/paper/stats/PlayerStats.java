package net.noobsters.kern.paper.stats;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import lombok.Getter;
import lombok.Setter;

public class PlayerStats {
    @BsonId
    private @Getter @Setter String uuid;
    @BsonProperty(value = "stats")
    private @Getter @Setter Map<String, Document> stats = new HashMap<>();

    /**
     * Default constructor for a PlayerStats Object.
     * 
     * @param uuid  Stringified uuid, with dashes included.
     * @param stats The stats document map of the user.
     */
    public PlayerStats(String uuid, Map<String, Document> stats) {
        this.uuid = uuid;
        this.stats = stats;
        stats.get("uhc");
    }

    /**
     * Empty constructor for Pojoc.
     */
    public PlayerStats() {
    }

    @Override
    public String toString() {
        return "PlayerStats(uuid=" + this.uuid + ", stats=" + this.stats + ")";
    }

    /**
     * Helper function to auto-cast the uhc stats object if present in the
     * statistics map of the user.
     * 
     * @return {@link UHCStats} or Null if not present.
     */
    public UHCStats obtainUHCStats() {
        var uhcStats = stats.get("uhc");
        return uhcStats != null ? UHCStats.from(uhcStats) : null;
    }

}