package net.noobsters.kern.paper.punishments.gui;

import org.bukkit.Material;

import fr.mrmicky.fastinv.ItemBuilder;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;

public class PunizioneGui {

    public @Getter RapidInv gui;

    public PunizioneGui(String name, int penalties, String description) {
        gui = new RapidInv(9, "Punizione " + name);

        //TODO: BAMOS
        var teaming = new ItemBuilder(Material.LEAD).name(ChatColor.YELLOW + "Teaming")
        .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Ban " + penalties*12 + " hours").build();
        gui.setItem(0, teaming, click->{
            
            //ban penalties x12 hours
            //add penaltie points
        });

        var spam = new ItemBuilder(Material.MAP).name(ChatColor.YELLOW + "Spam / Flood")
        .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Mute " + penalties*2 + " hours").build();
        gui.setItem(2, spam, click->{
            //mute penalties x 2 hours
            //add penaltie points
        });

        var inappropriateLanguage = new ItemBuilder(Material.POISONOUS_POTATO).name(ChatColor.YELLOW + "Inappropriate Language")
        .addLore(ChatColor.WHITE + "+1 penalty point", ChatColor.GRAY + "Mute " + penalties*2 + " hours").build();
        gui.setItem(3, inappropriateLanguage, click->{
            //mute penalties x 2 hour
        });

        var sports = new ItemBuilder(Material.BARRIER).name(ChatColor.GOLD + "Bad Sportsmanship")
        .addLore(ChatColor.WHITE + "+3 penalty points", ChatColor.GRAY + (penalties >= 12 ? "Ban " : "Mute ") + penalties + " days").build();
        gui.setItem(5, sports, click->{
            if(penalties >= 12){
                //is a ban
                //ban penalties days
                //add penaltie points
            }else{
                //is a mute
                //mute penalties days
                //add penaltie points
            }
        });

        var hate = new ItemBuilder(Material.WITHER_ROSE).name(ChatColor.GOLD + "Hate Speech")
        .addLore(ChatColor.WHITE + "+3 penalty points", ChatColor.GRAY + (penalties >= 12 ? "Ban " : "Mute ") + penalties + " days").build();
        gui.setItem(6, hate, click->{
            if(penalties >= 12){
                //is a ban
                //ban penalties days
                //add penaltie points
            }else{
                //is a mute
                //mute penalties days
                //add penaltie points
            }
        });

        var hack = new ItemBuilder(Material.NETHERITE_SCRAP).name(ChatColor.DARK_RED + "Hacked Client")
        .addLore(ChatColor.WHITE + "+12 penalty points", ChatColor.GRAY + "Undefined ban").build();
        gui.setItem(8, hack, click->{
            //ban perma
            //add penaltie points
        });

    }
}
