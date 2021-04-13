package net.noobsters.kern.paper.punishments.events;

import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

public class PlayerBannedEvent extends GenericPunishmentEvent{

    public PlayerBannedEvent(PlayerProfile profile, Punishment ban, boolean async) {
        super(profile, ban, async);
    }

    public Punishment getBan(){
        return this.getPunishment();
    }
    
    
}
