package net.noobsters.kern.paper.punishments.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.Punishment;

public class PunizioneInfoGui {

    private static DateFormat format = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss");
    static {
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
    }
    public @Getter RapidInv gui;

    public PunizioneInfoGui(PlayerProfile profile) {
        var name = profile.getName();
        var uuid = profile.getUuid();

        var totalRows = (int) Math.ceil((profile.getBans().size() + profile.getMutes().size()) / 9) + 1;
        gui = new RapidInv(9 * totalRows, "Info player " + name);

        var penalties = profile.getPenalties();

        var head = new ItemBuilder(Material.PLAYER_HEAD)
                .meta(SkullMeta.class, meta -> meta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid))))
                .name(ChatColor.GREEN + name + "'s profile")
                .addLore(ChatColor.YELLOW + "Penalty points: " + ChatColor.WHITE + penalties,
                        ChatColor.YELLOW + "First join date: " + ChatColor.WHITE
                                + getDate(profile.getFirstJoin() != null ? profile.getFirstJoin()
                                        : System.currentTimeMillis()),
                        ChatColor.YELLOW + "Last join date: " + ChatColor.WHITE
                                + getDate(profile.obtainLastTimeOnline()))
                .build();
        gui.setItem(0, head);

        profile.getBans().forEach(bans -> gui.addItem(getItemifiedPunishment(bans)));
        profile.getMutes().forEach(mutes -> gui.addItem(getItemifiedPunishment(mutes)));
    }

    private ItemStack getItemifiedPunishment(final Punishment punishment) {
        return new ItemBuilder(punishment.obtainType()).name(ChatColor.GREEN + punishment.getType().toString())
                .addLore(ChatColor.YELLOW + "Reason: " + ChatColor.WHITE + punishment.getReason(),
                        ChatColor.YELLOW + "Punisher: " + ChatColor.WHITE + punishment.getPunisher(),
                        ChatColor.YELLOW + "Active: " + colorizedBoolean(punishment.obtainActive()),
                        ChatColor.YELLOW + "Creation: " + ChatColor.WHITE + getDate(punishment.getCreation()),
                        ChatColor.YELLOW + "Expiration: " + ChatColor.WHITE + getDate(punishment.getExpiration()))
                .build();
    }

    private static String getDate(long date) {
        return format.format(new Date(date));
    }

    private String colorizedBoolean(boolean bool) {
        return (bool ? ChatColor.GREEN : ChatColor.RED) + String.valueOf(bool);
    }
}
