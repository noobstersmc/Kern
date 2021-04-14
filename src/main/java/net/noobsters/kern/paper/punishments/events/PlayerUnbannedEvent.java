package net.noobsters.kern.paper.punishments.events;

import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

public class PlayerUnbannedEvent  extends GenericPunishmentEvent{

    public PlayerUnbannedEvent(PlayerProfile profile, Punishment ban, boolean async) {
        super(profile, ban, async);
    }

    /**
     * Utility function, shortcut to {@link GenericPunishmentEvent#getPunishment()}
     * 
     * @return the punishment called by this event.
     */
    public Punishment getBan(){
        return this.getPunishment();
    }
    
    
}
