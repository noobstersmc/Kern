package net.noobsters.kern.paper.punishments.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

@RequiredArgsConstructor
public class PlayerBannedEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;
    /*
     * Custom data, use @NonNull for the constructor
     */
    private @NonNull @Getter PlayerProfile profile;
    private @NonNull @Getter Punishment ban;
    private Player player = null;

    public Player getPlayer() {
        if (player == null) {
            var id = UUID.fromString(profile.getUuid());
            this.player = Bukkit.getPlayer(id);
        }
        return player;
    }

    public PlayerBannedEvent(PlayerProfile profile, Punishment ban, boolean async) {
        super(async);
        this.profile = profile;
        this.ban = ban;
    }

}