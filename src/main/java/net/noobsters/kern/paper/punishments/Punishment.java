package net.noobsters.kern.paper.punishments;

import java.util.concurrent.TimeUnit;

import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.events.GenericPunishmentEvent;
import net.noobsters.kern.paper.punishments.events.PlayerBannedEvent;
import net.noobsters.kern.paper.punishments.events.PlayerMutedEvent;

@Data
@AllArgsConstructor(staticName = "of")
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

    public Punishment() {
    }

    public String timeLeft() {
        var millis = expiration - System.currentTimeMillis();
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }

    public void performPunishment(Player player) {
        switch (this.type) {
        case BAN:
            player.kickPlayer("You've been banned from the server: " + this.reason);
            break;
        case MUTE:
            player.sendMessage("You've been muted for " + this.reason);
            break;

        default:
            break;
        }
    }

    @SuppressWarnings("all")
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

}
