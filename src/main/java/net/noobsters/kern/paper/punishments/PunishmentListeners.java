package net.noobsters.kern.paper.punishments;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PunishmentListeners implements Listener {
    private @NonNull @Getter PunishmentManager pManager;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        // On preLogin, query the users
        var uuid = event.getUniqueId();
        var profile = pManager.getOrCreatePlayerProfile(uuid.toString());
        var ban = profile.isBanned();
        if (ban != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    "You are currently banned for " + ban.getReason() + "\nYour ban expires in: " + ban.timeLeft());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        var uuid = e.getPlayer().getUniqueId();
        var profile = pManager.getOrCreatePlayerProfile(uuid.toString());
        var muted = profile.isMuted();
        if (muted != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("You're currently muted for " + muted.getReason());
        }
    }

}
