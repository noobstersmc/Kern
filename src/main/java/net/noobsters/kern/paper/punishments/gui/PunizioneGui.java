package net.noobsters.kern.paper.punishments.gui;

import com.mongodb.client.MongoCollection;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.guis.RapidInv;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.punishments.BanUnits;
import net.noobsters.kern.paper.punishments.Punishment;
import net.noobsters.kern.paper.punishments.PunishmentType;

public class PunizioneGui {

    public @Getter RapidInv gui;

    public PunizioneGui(PlayerProfile profile, CommandSender sender, String description) {
        gui = new RapidInv(9, "Punizione || " + profile.getName());

        var currentPoints = profile.getPenalties() + 1;

        var teaming = new ItemBuilder(Material.LEAD).name(ChatColor.YELLOW + "Teaming")
                .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Ban " + currentPoints * 12 + " hours")
                .build();
        gui.setItem(0, teaming, click -> {
            profile.commitPenalty(1, getCollection());
            profile.commitPunishment(Punishment.of(sender.getName(), "Teaming - " + description,
                    System.currentTimeMillis() + BanUnits.parseString(currentPoints * 12 + "h"),
                    System.currentTimeMillis(), PunishmentType.BAN, false), getCollection());

            gui.closeAll();
        });

        var spam = new ItemBuilder(Material.MAP).name(ChatColor.YELLOW + "Spam / Flood")
                .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Mute " + currentPoints * 2 + " hours")
                .build();
        gui.setItem(2, spam, click -> {
            profile.commitPenalty(1, getCollection());
            profile.commitPunishment(Punishment.of(sender.getName(), "Spam / Flood - " + description,
                    System.currentTimeMillis() + BanUnits.parseString(currentPoints * 2 + "h"),
                    System.currentTimeMillis(), PunishmentType.MUTE, false), getCollection());

            gui.closeAll();

        });

        var inappropriateLanguage = new ItemBuilder(Material.POISONOUS_POTATO)
                .name(ChatColor.YELLOW + "Inappropriate Language")
                .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Mute " + currentPoints * 2 + " hours")
                .build();
        gui.setItem(3, inappropriateLanguage, click -> {
            profile.commitPenalty(1, getCollection());
            profile.commitPunishment(Punishment.of(sender.getName(), "Inappropriate Language - " + description,
                    System.currentTimeMillis() + BanUnits.parseString(currentPoints * 2 + "h"),
                    System.currentTimeMillis(), PunishmentType.MUTE, false), getCollection());

            gui.closeAll();
        });

        var sports = new ItemBuilder(Material.BARRIER).name(ChatColor.GOLD + "Bad Sportsmanship")
                .addLore(ChatColor.WHITE + "+3 penalty points",
                        ChatColor.GRAY + ((currentPoints + 2) >= 12 ? "Ban " : "Mute ") + (currentPoints + 2) + " days")
                .build();
        gui.setItem(5, sports, click -> {
            if ((currentPoints + 2) >= 12) {
                profile.commitPunishment(Punishment.of(sender.getName(), "Bad Sportsmanship - " + description,
                        System.currentTimeMillis() + BanUnits.parseString((currentPoints + 2) + "d"),
                        System.currentTimeMillis(), PunishmentType.BAN, false), getCollection());
            } else {
                profile.commitPunishment(Punishment.of(sender.getName(), "Bad Sportsmanship - " + description,
                        System.currentTimeMillis() + BanUnits.parseString((currentPoints + 2) + "d"),
                        System.currentTimeMillis(), PunishmentType.MUTE, false), getCollection());

            }
            profile.commitPenalty(3, getCollection());
            gui.closeAll();
        });

        var hate = new ItemBuilder(Material.WITHER_ROSE).name(ChatColor.GOLD + "Hate Speech")
                .addLore(ChatColor.WHITE + "+3 penalty points",
                        ChatColor.GRAY + ((currentPoints + 2) >= 12 ? "Ban " : "Mute ") + (currentPoints + 2) + " days")
                .build();
        gui.setItem(6, hate, click -> {
            if ((currentPoints + 2) >= 12) {
                profile.commitPunishment(Punishment.of(sender.getName(), "Hate Speech - " + description,
                        System.currentTimeMillis() + BanUnits.parseString((currentPoints + 2) + "d"),
                        System.currentTimeMillis(), PunishmentType.BAN, false), getCollection());
            } else {
                profile.commitPunishment(Punishment.of(sender.getName(), "Hate Speech - " + description,
                        System.currentTimeMillis() + BanUnits.parseString((currentPoints + 2) + "d"),
                        System.currentTimeMillis(), PunishmentType.MUTE, false), getCollection());

            }
            profile.commitPenalty(3, getCollection());
            gui.closeAll();
        });

        var hack = new ItemBuilder(Material.NETHERITE_SCRAP).name(ChatColor.DARK_RED + "Hacked Client")
                .addLore(ChatColor.WHITE + "+12 penalty points", ChatColor.GRAY + "Undefined ban").build();
        gui.setItem(8, hack, click -> {
            profile.commitPunishment(Punishment.of(sender.getName(), "Hacked Client - " + description,
                    System.currentTimeMillis() + BanUnits.parseString("10y"), System.currentTimeMillis(),
                    PunishmentType.BAN, false), getCollection());
            profile.commitPenalty(12, getCollection());
            gui.closeAll();
        });

    }

    private static MongoCollection<PlayerProfile> getCollection() {
        return Kern.getInstance().getProfileManager().getCollection();
    }
}
