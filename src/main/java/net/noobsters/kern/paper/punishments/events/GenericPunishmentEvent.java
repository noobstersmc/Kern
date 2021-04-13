package net.noobsters.kern.paper.punishments.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

public class GenericPunishmentEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;

    private @Getter PlayerProfile profile;
    private @Getter Punishment punishment;
    private Player player = null;

    public Player getPlayer() {
        if (player == null) {
            var id = UUID.fromString(profile.getUuid());
            this.player = Bukkit.getPlayer(id);
        }
        return player;
    }

    public GenericPunishmentEvent(PlayerProfile profile, Punishment punishment, boolean async) {
        super(async);
        this.profile = profile;
        this.punishment = punishment;
    }

}
