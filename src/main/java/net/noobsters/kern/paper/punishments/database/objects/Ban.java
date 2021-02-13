package net.noobsters.kern.paper.punishments.database.objects;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import dev.morphia.annotations.Id;
import dev.morphia.annotations.Property;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.punishments.database.objects.punishments.AbstractPunishment;

@RequiredArgsConstructor(staticName = "create")
public class Ban extends AbstractPunishment<Player> {
    @Id
    private @Getter String id = getPunishmentID();

    @Property("reason")
    private @NonNull @Getter String reason;

    @Property("punisher")
    private @NonNull @Getter UUID punisher;

    @Property("submissionDate")
    private @NonNull @Getter Long submissionDate;

    @Property("expirationTime")
    private @NonNull @Getter Long expirationTime;

    @Override
    public CompletableFuture<Boolean> execute(final Player player) {
        /** Mute player logic goes here */
        return CompletableFuture.supplyAsync(() -> {
            // If player is null return false
            if (player == null) {
                return false;
            } else if (player.isOnline()) {
                player.kickPlayer("You've been banned for " + this.reason + " until " + this.expirationTime + " by "
                        + this.getPunisherName());
            }
            return true;
        });

    }

}
