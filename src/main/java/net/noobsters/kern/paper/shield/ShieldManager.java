package net.noobsters.kern.paper.shield;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.entity.Player;

import java.util.List;


public class ShieldManager {
    public static ShieldManager getShieldPattern(Player player) {
        return null;
    }

    public static boolean addShield(Player player, String name) {
        if (!player.hasPermission("noobsters.shield."+ name)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user "+ player.getName()+ " permission set noobsters.shield."+ name);
            System.out.println("ds");
            return true;
        }
        return false;
    }

    public static boolean removeShield(Player player, String name) {
        if (player.hasPermission("noobsters.shield."+ name)) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user "+ player.getName()+ " permission unset noobsters.shield."+ name);
            return true;
        }
        return false;
    }

    private static DyeColor getColor(String shield) {
        for (ShieldPattern pattern : Shields.getPatterns()) {
            if (pattern.getName().equalsIgnoreCase(shield)) {
                return pattern.getBackground();
            }
        }
        return null;
    }

    private static List<Pattern> getPatterns(String shield) {
        for (ShieldPattern pattern : Shields.getPatterns()) {
            if (pattern.getName().equalsIgnoreCase(shield)) {
                return pattern.getPatterns();
            }
        }
        return null;
    }
}
