package net.noobsters.kern.paper.stats;

import static com.mongodb.client.model.Filters.eq;
import static org.bukkit.Material.BOW;
import static org.bukkit.Material.CLOCK;
import static org.bukkit.Material.DIAMOND_SWORD;
import static org.bukkit.Material.ENCHANTED_GOLDEN_APPLE;
import static org.bukkit.Material.IRON_AXE;
import static org.bukkit.Material.IRON_SWORD;
import static org.bukkit.Material.KNOWLEDGE_BOOK;
import static org.bukkit.Material.NETHERITE_PICKAXE;
import static org.bukkit.Material.NETHER_STAR;
import static org.bukkit.Material.REDSTONE;
import static org.bukkit.Material.SHULKER_SPAWN_EGG;
import static org.bukkit.Material.TOTEM_OF_UNDYING;

import java.text.DecimalFormat;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Optional;
import fr.mrmicky.fastinv.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.guis.RapidInv;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.utils.PlayerDBUtil.GenericPlayer;

@CommandAlias("stats")
public class StatsCMD extends BaseCommand {
	/** Constant colors */
	DecimalFormat numberFormat = new DecimalFormat("#.00");
	private static final String color1 = ChatColor.of("#12af5c").toString();
	private static final String color2 = ChatColor.of("#db0f00").toString();
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
	public void statsOfPlayer(Player sender, @Optional @Flags("other") String target) {

		var player = target == null ? sender.getName() : target;

		/** Execute inside a different thread to avoid stopping the main thread. */
		CompletableFuture.runAsync(() -> {
			/** Obtain the stats inventory. */
			var inv = obtainGui(player);
			/** Ensure the given object isn't null. If valid, open inv. */
			if (inv != null) {
				inv.open(sender, statsManager.getInstance());
			} else {
				sender.sendMessage(ChatColor.RED + target + " couldn't find statistics record.");
			}
			/** Handle any posible exceptions. [⬇] */
		}).handle((a, b) -> ExceptionHandlers.handleVoidWithSender(a, b, sender));
	}

	/**
	 * Helper function that queries and creates a visualization of a player's stats.
	 * 
	 * @param player Player name to be queried
	 * @return {@link RapidInv} object or null if not completable.
	 */
	public RapidInv obtainGui(String player) {
		var genericPlayer = GenericPlayer.getGenericPlayer(player);
		/** Null-safety check. */
		if (genericPlayer == null)
			return null;
		/** Query the mongo db for a player's statistics. */
		var stats = statsManager.getStatsCollection().find(eq(genericPlayer.getUuid().toString())).first();
		/** If stats are not present, retur null. */
		// TODO: Don't return but use all data as 0 values.
		if (stats == null)
			return null;
		/** Use the helpful obtainUHCStats function. */
		var uhcStats = stats.obtainUHCStats();
		/** Ternary operator to return and check for nulls. */
		return uhcStats != null ? getStatsGui(stats.obtainUHCStats(), genericPlayer) : null;
	}

	/**
	 * Private function that builds a GUI to display a player's statistical data.
	 * 
	 * @param uhcStats      {@link UHCStats} object to be used as a data source
	 * @param genericPlayer {@link GenericPlayer} object to be used as data source
	 * @return {@link RapidInv} GUI displaying the aforementioned data.
	 */
	private RapidInv getStatsGui(final UHCStats uhcStats, final GenericPlayer genericPlayer) {
		/** Create a GUI with the player's name */
		var gui = new RapidInv(9 * 4, genericPlayer.getPlayerName() + "'s UHC Stats");

		/** Obtain all required variables */
		var winsV = uhcStats.getWins();
		var winstreakV = uhcStats.getWinStreak();
		var killsV = uhcStats.getKills();
		var deathsV = uhcStats.getDeaths();
		var kdrV = uhcStats.getKillDeathRatio();
		var killRecordV = uhcStats.getKillRecord();
		var timePlayedV = uhcStats.getTimePlayed();
		var hostileMobsV = uhcStats.getHostileMobsKilled();
		var peacefulMobsV = uhcStats.getPeacefulMobsKilled();
		var projectileAccuracyV = uhcStats.getProjectileAccuracy();
		var notchV = uhcStats.getNotchApples();
		var gheadV = uhcStats.getGoldenHeads();
		var gappV = uhcStats.getGoldenApple();
		var diamondV = uhcStats.getDiamonds();
		var goldV = uhcStats.getGold();
		var netheriteV = uhcStats.getNetherite();
		var divisibleDeaths = deathsV != 0 ? deathsV : 1;

		/** Setup all items for the gui. */
		var rank = item(NETHER_STAR).name(color1 + "Rating: " + white + "Unrated").build();

		var wins = item(TOTEM_OF_UNDYING).name(color1 + "Wins: " + white + winsV)
				.lore(color1 + "Highest Win streak: " + white + winstreakV).build();

		var kills = item(IRON_SWORD).name(color1 + "Kills: " + white + killsV).flags(ItemFlag.HIDE_ATTRIBUTES)
				.build();

		var KDR = item(IRON_AXE).name(color1 + "KDR: " + white + numberFormat.format(kdrV)).flags(ItemFlag.HIDE_ATTRIBUTES).build();

		var killRecord = item(DIAMOND_SWORD).name(color1 + "Kill Record: " + white + killRecordV)
				.flags(ItemFlag.HIDE_ATTRIBUTES).build();

		var timePlayed = item(CLOCK)
				.name(color1 + "Time played: " + white + numberFormat.format(((timePlayedV / 1000.0) / 60) / 60) + " hours")
				.flags(ItemFlag.HIDE_ATTRIBUTES).build();

		var deaths = item(REDSTONE).name(color1 + "Deaths: " + white + deathsV).flags(ItemFlag.HIDE_ATTRIBUTES)
				.build();

		var mobs = item(SHULKER_SPAWN_EGG).name(color1 + "Hostile mobs: " + white + hostileMobsV)
				.lore(color1 + "Peaceful mobs: " + white + peacefulMobsV).build();

		var accuracy = item(BOW).name(color1 + "Projectile accuracy: " + white + numberFormat.format(projectileAccuracyV) + "%")
				.build();

		var apples = item(ENCHANTED_GOLDEN_APPLE).name(color1 + "Notch Apples consumed: " + white + notchV)
				.lore(color1 + "Golden Heads consumed: " + white + gheadV,
						color1 + "Golden Apples consumed: " + white + gappV)
				.build();

		var mining = item(NETHERITE_PICKAXE).name(color1 + "Diamonds mined: " + white + diamondV)
				.lore(color1 + "Gold mined: " + white + goldV, color1 + "Netherite mined: " + white + netheriteV,
						color1 + "DGR: " + white + numberFormat.format(diamondV / divisibleDeaths),
						color1 + "GGR: " + white + numberFormat.format(goldV / divisibleDeaths),
						color1 + "NGR: " + white + numberFormat.format(netheriteV / divisibleDeaths))
				.flags(ItemFlag.HIDE_ATTRIBUTES).build();
		var stampBook = item(KNOWLEDGE_BOOK).name(color2 + "Stamp book").build();

		//var topWins = item(TOTEM_OF_UNDYING).name(color3 + "Top wins").build();

		//var topKillRecord = item(DIAMOND_SWORD).flags(ItemFlag.HIDE_ATTRIBUTES).name(color3 + "Top kill record").build();
				
		//var topRanking = item(NETHER_STAR).name(color3 + "Top champions").build();

		/** Set all items to the GUI */
		gui.setItem(1, rank);
		gui.setItem(2, wins);
		gui.setItem(3, kills);
		gui.setItem(4, KDR);
		gui.setItem(5, killRecord);
		gui.setItem(6, timePlayed);
		gui.setItem(7, deaths);
		gui.setItem(11, mobs);
		gui.setItem(12, accuracy);
		gui.setItem(13, apples);
		gui.setItem(14, mining);
		gui.setItem(15, stampBook);
		//gui.setItem(30, topWins);
		//gui.setItem(31, topKillRecord);
		//gui.setItem(32, topRanking);

		/** Return the gui object to be used now. */
		return gui;
	}

	/**
	 * Shortcut function to the {@link ItemBuilder} constructor
	 * 
	 * @param material {@link Material}
	 * @return new instance of {@link ItemBuilder}.
	 */
	private static ItemBuilder item(Material material) {
		return new ItemBuilder(material);
	}

}