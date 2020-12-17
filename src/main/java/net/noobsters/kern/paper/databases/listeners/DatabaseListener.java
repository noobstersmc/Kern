package net.noobsters.kern.paper.databases.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.databases.DatabaseManager;

@RequiredArgsConstructor(staticName = "of")
public class DatabaseListener implements Listener{
    private @NonNull DatabaseManager dbManager;
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        e.getPlayer().sendMessage("Hello world");
        dbManager.getUser(e.getPlayer().getUniqueId());

    }
    
}
