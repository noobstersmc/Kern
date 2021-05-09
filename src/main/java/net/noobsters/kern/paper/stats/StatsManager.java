package net.noobsters.kern.paper.stats;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import lombok.Getter;
import net.noobsters.kern.paper.Kern;

public class StatsManager {
    private @Getter Kern instance;
    private @Getter StatsCMD statsCmd;
    /** Mongo Objects */
    private @Getter MongoCollection<PlayerStats> statsCollection;

    public StatsManager(final Kern instance) {
        this.instance = instance;
        /** Instantiate, and register, the command. */
        this.statsCmd = new StatsCMD(this);
        /** Messy code but it works for now. Just reuse the condorManager database */
        this.statsCollection = instance.getCondorManager().getMongoDatabase().getCollection("punishments",
                PlayerStats.class);
    }

    /**
     * Helper function that increases a given field of a player's statisitics by the
     * given amount.
     * 
     * @param uuid   Player's UUID to be increased.
     * @param field  Field to be increased. As of right now, only the following
     *               exist: kills, deaths, and wins.
     * @param amount Any positive or negative integer to incremente, or reduce, the
     *               aforementioned field.
     * @return {@link PlayerStats} object of the player, if existant, otherwise
     *         null.
     */
    public PlayerStats increasePlayerUHCStatistic(String uuid, String field, int amount) {
        return statsCollection.findOneAndUpdate(Filters.eq(uuid), Updates.inc("stats.uhc." + field, amount));
    }

    public PlayerStats setPlayerUHCStatistic(String uuid, String field, int amount) {
        return statsCollection.findOneAndUpdate(Filters.eq(uuid), Updates.set("stats.uhc." + field, amount));
    }
}
