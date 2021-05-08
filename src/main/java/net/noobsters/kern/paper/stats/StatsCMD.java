package net.noobsters.kern.paper.stats;

import java.util.concurrent.CompletableFuture;

import com.mongodb.client.model.Filters;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import fr.mrmicky.fastinv.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

@CommandAlias("stats")
public class StatsCMD extends BaseCommand {
    /** Constant colors */
    private static final String color1 = ChatColor.of("#12af5c").toString();
    private static final String white = ChatColor.WHITE.toString();
    /** StatsManager instance */
    private StatsManager statsManager;

    /**
     * Constructor that auto-registers the command of this class.
     * 
     * @param instance {@link StatsManager} instance
     */
    public StatsCMD(final StatsManager statsManager) {
        this.statsManager = statsManager;
        /** Access statsManager's instance and register using the command manager */
        statsManager.getInstance().getCommandManager().registerCommand(this);
    }

    @Default
    @CommandCompletion("@players")
    public void statsOfPlayer(Player sender, @Flags("other") String target) {
        /** Execute inside a different thread to avoid stopping the main thread. */
        CompletableFuture.runAsync(() -> {
            /** Obtain the stats inventory. */
            var inv = getStatsGui(target);
            /** Ensure the given object isn't null. If valid, open inv. */
            if (inv != null) {
                inv.open(sender, statsManager.getInstance());
            } else {
                sender.sendMessage(target + "'s stats inventory returned null");
            }
            /** Handle any posible exceptions. [⬇] */
        }).handle((a, b) -> ExceptionHandlers.handleVoidWithSender(a, b, sender));
    }

    /**
     * Comenta este desastre
     * 
     * @param player
     * @return
     */
    public RapidInv getStatsGui(String player) {
        /** Obtain the stats collection */
        var statsCollection = statsManager.getStatsCollection();

        var playerMinecraft = PlayerDBUtil.getPlayerObject(player);
        if (playerMinecraft == null)
            return null;

        var name = playerMinecraft.get("username").getAsString();
        var uuid = playerMinecraft.get("id").getAsString();

        PlayerStats stats = statsCollection.find(Filters.eq(uuid)).first();

        if (stats == null || !stats.getStats().containsKey("uhc"))
            return null;

        RapidInv gui = new RapidInv(9 * 4, name + "'s UHC Stats");

        var uhcStats = stats.getStats().get("uhc");

        var rank = new ItemBuilder(Material.NETHER_STAR).name(color1 + "Rating: " + white + "Unrated").build();

        gui.setItem(1, rank);

        var winsValue = uhcStats.getInteger("wins", 0);
        var winsStreakValue = uhcStats.getInteger("win_streak", 0);

        var wins = new ItemBuilder(Material.TOTEM_OF_UNDYING).name(color1 + "Wins: " + white + winsValue)
                .addLore(color1 + "Highest Win streak: " + white + winsStreakValue).build();

        gui.setItem(2, wins);

        var killsValue = uhcStats.getInteger("kills", 0);

        var kills = new ItemBuilder(Material.IRON_SWORD).name(color1 + "Kills: " + white + killsValue)
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(3, kills);

        var KDRvalue = uhcStats.getInteger("kills", 0) / uhcStats.getInteger("deaths", 1);

        var KDR = new ItemBuilder(Material.IRON_AXE).name(color1 + "KDR: " + white + KDRvalue)
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(4, KDR);

        var killRecordValue = uhcStats.getInteger("kill_record", 0);

        var killRecord = new ItemBuilder(Material.DIAMOND_SWORD)
                .name(color1 + "Kill Record: " + white + killRecordValue).flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(5, killRecord);

        var timePlayedValue = uhcStats.getLong("time_played");
        if (timePlayedValue == null)
            timePlayedValue = 0L;

        var timePlayed = new ItemBuilder(Material.CLOCK)
                .name(color1 + "Time played: " + white + (timePlayedValue / 1000.0) / 120 + " hours")
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(6, timePlayed);

        var deathsValue = uhcStats.getInteger("deaths", 0);

        var deaths = new ItemBuilder(Material.REDSTONE).name(color1 + "Deaths: " + white + deathsValue)
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(7, deaths);

        var hostileMobsValue = uhcStats.getInteger("hostile_mobs", 0);
        var peacefulMobsValue = uhcStats.getInteger("peaceful_mobs", 0);

        var mobs = new ItemBuilder(Material.SHULKER_SPAWN_EGG)
                .name(color1 + "Hostile mobs: " + white + hostileMobsValue)
                .addLore(color1 + "Peaceful mobs: " + white + peacefulMobsValue).build();

        gui.setItem(11, mobs);

        var accuracyValue = (uhcStats.getInteger("projectile_shoot", 0) / 100)
                * uhcStats.getInteger("projectile_hit", 1);

        var accuracy = new ItemBuilder(Material.BOW)
                .name(color1 + "Projectile accuracy: " + white + accuracyValue + "%").build();

        gui.setItem(12, accuracy);

        var notchValue = uhcStats.getInteger("notch_apple", 0);
        var headValue = uhcStats.getInteger("golden_head", 0);
        var goldenValue = uhcStats.getInteger("golden_apple", 0);

        var apples = new ItemBuilder(Material.ENCHANTED_GOLDEN_APPLE)
                .name(color1 + "Notch Apples consumed: " + white + notchValue)
                .addLore(color1 + "Golden Heads consumed: " + white + headValue)
                .addLore(color1 + "Golden Apples consumed: " + white + goldenValue).build();

        gui.setItem(13, apples);

        var diamond = uhcStats.getInteger("diamond", 0);
        var gold = uhcStats.getInteger("gold", 0);
        var netherite = uhcStats.getInteger("netherite", 0);

        var mining = new ItemBuilder(Material.NETHERITE_PICKAXE).name(color1 + "Diamonds mined: " + white + diamond)
                .addLore(color1 + "Gold mined: " + white + gold).lore(color1 + "Netherite mined: " + white + netherite)
                .addLore(color1 + "DGR: " + white + diamond / uhcStats.getInteger("deaths", 1))
                .addLore(color1 + "GGR: " + white + gold / uhcStats.getInteger("deaths", 1))
                .addLore(color1 + "NGR: " + white + netherite / uhcStats.getInteger("deaths", 1))
                .flags(ItemFlag.HIDE_ATTRIBUTES).build();

        gui.setItem(14, mining);

        var stampBook = new ItemBuilder(Material.KNOWLEDGE_BOOK).name(ChatColor.of("#db0f00") + "Stamp book").build();

        gui.setItem(15, stampBook);

        var topWins = new ItemBuilder(Material.TOTEM_OF_UNDYING).name(ChatColor.of("#f3be12") + "Top wins");

        // for

        gui.setItem(30, topWins.build());

        var topKillRecord = new ItemBuilder(Material.DIAMOND_SWORD).flags(ItemFlag.HIDE_ATTRIBUTES)
                .name(ChatColor.of("#f3be12") + "Top kill record");

        // for

        gui.setItem(31, topKillRecord.build());

        var topRanking = new ItemBuilder(Material.NETHER_STAR).name(ChatColor.of("#f3be12") + "Top champions");

        // for

        gui.setItem(32, topRanking.build());

        return gui;
    }

}