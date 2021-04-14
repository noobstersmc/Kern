package net.noobsters.kern.paper.punishments.gui;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class PunizioneInfoGui {

    public @Getter RapidInv gui;


    public PunizioneInfoGui(String name, String uuid) {
        gui = new RapidInv(9, "Info player " + name); //TODO: size of punishements of that player

        var penalties = 0;
        
        var head = new ItemBuilder(Material.PLAYER_HEAD)
                    .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))))
                    .name(ChatColor.GREEN + name + "'s profile").addLore(ChatColor.YELLOW + "Penaltie points: " + ChatColor.WHITE + penalties,
                    ChatColor.YELLOW + "First join date: " +  ChatColor.WHITE + "00/00/0000",
                    ChatColor.YELLOW + "Last join date: " +  ChatColor.WHITE + "00/00/0000").build();
        gui.setItem(0, head);

        var contents = gui.getInventory().getContents();
        for (int i = 1; i < contents.length; i++) {
            if(i < contents.length){
                var punishement = "Ban/mute object";//TODO: xd
                var punish = new ItemBuilder(Material.PAPER).name(ChatColor.GREEN + "Ban/Mute")
                .addLore(ChatColor.YELLOW + "Info of the " + punishement + ": " + ChatColor.WHITE + "0")
                .build();
                gui.setItem(i, punish);
            }
        }
        

    }
}
