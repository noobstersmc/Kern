package net.noobsters.kern.paper.shield.jcedeno.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;
import net.noobsters.kern.paper.shield.jcedeno.objects.ShieldProfile;

public class PlayerUpdatedShieldEvent extends Event {
    /*
     * Methods Required by BukkitAPI
     */
    private @Getter static final HandlerList HandlerList = new HandlerList();
    private @Getter final HandlerList Handlers = HandlerList;

    private @Getter ShieldProfile profile;
    private @Getter CustomShield shield;
    private Player player = null;

    public Player getPlayer() {
        if (player == null) {
            var id = UUID.fromString(profile.getUuid());
            this.player = Bukkit.getPlayer(id);
        }
        return player;
    }

    public PlayerUpdatedShieldEvent(ShieldProfile profile, CustomShield shield, boolean async) {
        super(async);
        this.profile = profile;
        this.shield = shield;
    }

}
