package net.noobsters.kern.paper.punishments.events;

import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

public class PlayerMutedEvent extends GenericPunishmentEvent {

    public PlayerMutedEvent(PlayerProfile profile, Punishment ban, boolean async) {
        super(profile, ban, async);
    }

    public Punishment getMute() {
        return this.getPunishment();
    }

}
