package net.noobsters.kern.paper.punishments;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.concurrent.TimeUnit;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;
import org.bukkit.Material;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.events.GenericPunishmentEvent;
import net.noobsters.kern.paper.punishments.events.PlayerBannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerMutedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnbannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerUnmutedEvent;

@Data
@AllArgsConstructor(staticName = "of")
@SuppressWarnings("all")
public class Punishment {
    @BsonProperty(value = "punisher")
    String punisher;
    @BsonProperty(value = "reason")
    String reason;
    @BsonProperty(value = "expiration")
    Long expiration;
    @BsonProperty(value = "creation")
    Long creation = System.currentTimeMillis();
    @BsonProperty(value = "type")
    PunishmentType type;
    @BsonProperty(value = "canceled")
    Boolean canceled = false;

    /**
     * A helper function that returns an stringified time until expiration of
     * punishment.
     * 
     * @return String with time formatted.
     */
    public String timeLeft() {
        var millis = (expiration - System.currentTimeMillis()) + 1000;
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    /**
     * Quick function to determine wether this punishment is still active or not.
     * 
     * @return true or false accordingly.
     */
    public boolean obtainActive() {
        return this.getExpiration() > System.currentTimeMillis() && !this.getCanceled();
    }

    /**
     * Helper function to obtain the MongoDB matching query to find a punishment
     * inside a player's profile.
     * 
     * @return {@link Bson} document with the query filter.
     */
    public Bson obtainMatchingFilter() {
        return and(eq("punisher", punisher), eq("reason", reason), eq("expiration", expiration),
                eq("canceled", canceled));
    }

    /**
     * Helper function that obtains the PunishmentEvent of a specific punishment.
     * 
     * @param <E>     Any object that extends a GenericPunishment
     * @param profile Profile of the player who will be punished.
     * @param async   Async event or sync event.
     * @return An extended {@link GenericPunishmentEvent} based on the
     *         {@link Punishment#getType()} type.
     */
    public <E extends GenericPunishmentEvent> E getEvent(PlayerProfile profile, boolean async) {
        switch (this.type) {
        case BAN:
            return (E) new PlayerBannedEvent(profile, this, async);
        case MUTE:
            return (E) new PlayerMutedEvent(profile, this, async);
        default:
            return (E) new GenericPunishmentEvent(profile, this, async);
        }
    }

    /**
     * Helper function that obtains the PardonEvent of a specific punishment.
     * 
     * @param <E>     Any object that extends a GenericPunishment
     * @param profile Profile of the player who will be pardoned.
     * @param async   Async event or sync event.
     * @return An extended {@link GenericPunishmentEvent} based on the
     *         {@link Punishment#getType()} type.
     */
    public <E extends GenericPunishmentEvent> E getPardonEvent(PlayerProfile profile, boolean async) {
        switch (this.type) {
        case BAN:
            return (E) new PlayerUnbannedEvent(profile, this, async);
        case MUTE:
            return (E) new PlayerUnmutedEvent(profile, this, async);
        default:
            return (E) new GenericPunishmentEvent(profile, this, async);
        }
    }

    public Material obtainType(){
        return type == PunishmentType.BAN ? Material.MAP : Material.PAPER; 
    }

    /** Empty constructor for Mongo */
    public Punishment() {
    }

}
