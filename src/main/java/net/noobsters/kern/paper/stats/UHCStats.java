package net.noobsters.kern.paper.stats;

import org.bson.Document;

/**
 * Helper class that uses a document and allows developers to obtain data from
 * an UHC's statistics profile faster.
 */
public class UHCStats {
    private Document doc;

    public UHCStats(final Document doc) {
        this.doc = doc;
    }

    public static UHCStats from(final Document doc) {
        return new UHCStats(doc);
    }

    public int getWins() {
        return doc.getInteger("wins", 0);
    }

    public int getWinStreak() {
        return doc.getInteger("win_streak", 0);
    }

    public int getKills() {
        return doc.getInteger("kills", 0);
    }

    public int getDeaths() {
        return doc.getInteger("deaths", 0);
    }

    public int getKillRecord() {
        return doc.getInteger("kill_record", 0);
    }

    public Long getTimePlayed() {
        final var time = doc.getLong("time_played");
        return time != null ? time : 0L;
    }

    public int getHostileMobsKilled() {
        return doc.getInteger("hostile_mobs", 0);
    }

    public int getPeacefulMobsKilled() {
        return doc.getInteger("peaceful_mobs", 0);
    }

    public int getProjectilesShot() {
        return doc.getInteger("projectile_shoot", 0);
    }

    public int getProjectileHit() {
        return doc.getInteger("projectile_hit", 0);
    }

    public int getNotchApples() {
        return doc.getInteger("notch_apple", 0);
    }

    public int getGoldenHeads() {
        return doc.getInteger("golden_head", 0);
    }

    public int getGoldenApple() {
        return doc.getInteger("golden_apple", 0);
    }

    public int getDiamonds() {
        return doc.getInteger("diamond", 0);
    }

    public int getGold() {
        return doc.getInteger("gold", 0);
    }

    public int getNetherite() {
        return doc.getInteger("netherite", 0);
    }

    public double getKillDeathRatio() {
        final var deaths = this.getDeaths();
        /** If dividing by 0, change the operation to divide by 1 */
        final var percentualValue = (this.getKills() / 100.0) / (double) (deaths != 0 ? deaths : 1);
        return (percentualValue * 100);
    }

    public double getProjectileAccuracy() {
        final var hits = this.getProjectileHit();
        /** If dividing by 0, change the operation to divide by 1 */
        final var percentualValue = (this.getProjectilesShot() / 100.0) / (double) (hits != 0 ? hits : 1);
        return (percentualValue * 100);

    }

}
