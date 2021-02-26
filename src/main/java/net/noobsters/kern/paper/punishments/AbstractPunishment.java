package net.noobsters.kern.paper.punishments;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;

public abstract class AbstractPunishment<T> {
    public static UUID CONSOLE_ID = UUID.fromString("4fc0e8f7-00b0-4791-8bc9-0f2f56023ea6");

    public abstract String getReason();

    public abstract UUID getPunisher();

    public abstract Long getSubmissionDate();

    public abstract Long getExpirationTime();

    /**
     * Attempts to execute the punishment.
     * 
     * @param object Any type of object, handle specific in the implementation.
     * @return
     */
    public abstract CompletableFuture<Boolean> execute(T object);

    public String getPunishmentID() {
        final var className = this.getClass().getSimpleName();
        final var randomId = UUID.randomUUID().toString().split("-");
        return className + '-' + randomId[0] + '-' + randomId[1];
    }

    /**
     * Asks Bukkit the name of the punisher, if it exists.
     * 
     * @return Punisher's name
     */
    public String getPunisherName() {
        var id = getPunisher().toString();
        // Return console as the name
        if (id == CONSOLE_ID.toString())
            return "Console";
        // Find the name of the player
        return Bukkit.getOfflinePlayer(getPunisher()).getName();
    }

    /**
     * Helper method to calculate how many milliseconds are left in the punishment.
     * Might be negative
     * 
     * @return Long, either positive or negative number.
     */
    public Long getPunishmentLeft() {
        return getExpirationTime() - System.currentTimeMillis();
    }
}
