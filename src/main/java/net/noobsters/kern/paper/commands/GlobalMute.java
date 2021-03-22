package net.noobsters.kern.paper.commands;

import java.awt.Color;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.disguise.ReflexUtils;

@RequiredArgsConstructor
@CommandPermission("globalmute.cmd")
@CommandAlias("globalmute")
public class GlobalMute extends BaseCommand {
    private @NonNull Kern instance;

    @Default
    public void onCommand(CommandSender sender, @Optional Boolean bool) {
        var chat = instance.getChatManager();
        if (bool == null) {
            chat.setGlobalmute(!chat.isGlobalmute());
        } else {
            chat.setGlobalmute(bool);
        }

        Bukkit.broadcastMessage(
                ChatColor.of("#2be49c") + (chat.isGlobalmute() ? "Globalmute Enabled." : "Globalmute Disabled."));

    }

    /**
     * Crazy stuff ignore
     */
    HashMap<UUID, String> mapName = new HashMap<>();

    HashMap<UUID, Team> tMap = new HashMap<>();

    @CommandAlias("change-profile")
    public void changeGameProfileName(Player sender, @Name("#hexcolor") String hexColor) {
        var actual_name = sender.getName();
        var customName = obtainRandomChar() + "" + ChatColor.RESET + "" + obtainRandomChar();
        mapName.put(sender.getUniqueId(), customName);
        /** Change the name */
        ReflexUtils.setGameprofileName(sender, customName);
        /** Update everyone */
        Bukkit.getOnlinePlayers().stream().filter(players -> !players.equals(sender))
                .filter(players -> players.canSee(sender)).forEach(players -> {
                    players.hidePlayer(instance, sender);
                    players.showPlayer(instance, sender);
                });
        /** Update self */
        var color = ChatColor.of(hexColor);
        var teamID = UUID.randomUUID().toString().split("-")[0];
        var sb = Bukkit.getScoreboardManager().getMainScoreboard();
        if (sb.getTeam(teamID) == null) {
            var team = sb.registerNewTeam(teamID);
            tMap.put(sender.getUniqueId(), team);
            team.setPrefix("" + color);
            team.setSuffix("" + color + actual_name);
            team.addEntry(sender.getUniqueId().toString());
            team.addEntry(customName);
        }

    }
    void sendPackets(Player player){
        
    }

    HashMap<UUID, BukkitTask> tasks = new HashMap<>();

    @CommandAlias("change-team")
    public void changeTeamDisplay(Player sender, @Name("BaseColor") String hexColor, @Name("ChangeRate") Float changer,
            @Name("Interval") Long interval) {
        var senderTask = tasks.get(sender.getUniqueId());
        if (senderTask != null) {
            senderTask.cancel();
            tasks.remove(sender.getUniqueId());
        }
        var team = tMap.get(sender.getUniqueId());
        if (team != null) {
            final var c = NameAndLastColor.of("", ChatColor.of(hexColor).getColor());
            var task = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
                var col = getColorizedNamed(sender.getDisplayName(), ChatColor.of(c.getColor()), changer);
                team.setSuffix(col.getName());

                c.setColor(col.getColor());

            }, 0L, interval);
            tasks.put(sender.getUniqueId(), task);
        } else {
            sender.sendMessage("Team is null");
        }

    }

    public NameAndLastColor getColorizedNamed(String name, ChatColor chatColor, float changingNumber) {
        var strName = new StringBuilder();
        var chars = name.toCharArray();
        var color = chatColor.getColor();

        for (char c : chars) {
            strName.append(ChatColor.of(color) + "" + c);
            var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

            float hue = hsb[0] + (0.05f);

            float saturation = hsb[1];

            float brightness = hsb[2];
            var col = Color.getHSBColor(hue, saturation, brightness);
            color = col;
        }
        return NameAndLastColor.of(strName.toString(), color);
    }

    @AllArgsConstructor(staticName = "of")
    @Data
    static class NameAndLastColor {
        String name;
        java.awt.Color Color;
    }

    ChatColor obtainRandomChar() {
        return ChatColor.values()[new Random().nextInt(ChatColor.values().length)];
    }

    @CommandAlias("joinTeam")
    public void changeTeamName(Player sender, @Name("#hexcolor") String hexColor) {
        var color = ChatColor.of(hexColor);
        var teamID = UUID.randomUUID().toString().split("-")[0];
        var sb = Bukkit.getScoreboardManager().getMainScoreboard();
        if (sb.getTeam(teamID) == null) {
            var team = sb.registerNewTeam(teamID);
            team.setPrefix("" + color);
            team.setSuffix("" + color + sender.getName());
            team.addEntry(sender.getUniqueId().toString());
            team.addEntry(sender.getName());
            team.addEntry(sender.getDisplayName());
            team.addPlayer(sender);
            // team.addEntry(mapName.get(sender.getUniqueId()));
        }
    }

    @CommandAlias("rename")
    public void changeName(Player sender, @Name("#hexcolor") String hexColor) {
        if (!mapName.containsKey(sender.getUniqueId())) {
            /** Change the name and put it on map */
            mapName.put(sender.getUniqueId(), sender.getName());
            var color = ChatColor.of(hexColor).getColor();
            var strName = new StringBuilder();
            var chars = sender.getName().toCharArray();
            for (char c : chars) {
                strName.append(ChatColor.of(color) + "" + c);
                var hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

                float hue = hsb[0] + (0.05f);

                float saturation = hsb[1];

                float brightness = hsb[2];
                var col = Color.getHSBColor(hue, saturation, brightness);
                color = col;
                System.out.println(color.toString());
            }
            sender.setPlayerListName(strName.toString());
            sender.setDisplayName("puto");
            sender.setCustomName("puto");
            sender.setCustomNameVisible(true);
            /** Handle the custom team and rgb name */
            var teamID = UUID.randomUUID().toString().split("-")[0];
            var sb = Bukkit.getScoreboardManager().getMainScoreboard();
            if (sb.getTeam(teamID) == null) {
                var team = sb.registerNewTeam(teamID);
                team.setPrefix(ChatColor.of(hexColor) + sender.getName());
                team.addEntry(sender.getName());
                team.setColor(org.bukkit.ChatColor.valueOf(hexColor));
                team.addEntry("puto");
                team.addEntry(sender.getUniqueId().toString());
                team.addEntry(sender.getDisplayName());
            }
        } else {
            /** Remove the name from map and reset it onto the player */
            sender.setDisplayName(mapName.remove(sender.getUniqueId()));
        }
    }

}
