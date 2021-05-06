package net.noobsters.kern.paper.shield.jcedeno.commands;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mongodb.MongoWriteException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldInvalidTypeException;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldNotFoundException;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;

@CommandPermission("kern.shield")
@CommandAlias("shield|sh")
public class ShieldCMD extends BaseCommand {
    /** Static variables for messages */
    private static final String decorator = ChatColor.BOLD + "*========================*";
    private static final String header = decorator + "\n|+++  " + ChatColor.GOLD + ChatColor.BOLD + "Shield List Query"
            + ChatColor.RESET + ChatColor.BOLD + "   +++|\n" + decorator + "\n";
    private static final String color = ChatColor.GREEN.toString();
    private static final String color2 = ChatColor.GRAY.toString();
    /** Shield manager */
    private @Getter ShieldManager shieldManager;

    /**
     * Constructor that auto-registers the command of this class.
     * 
     * @param instance Kern plugin instance
     */
    public ShieldCMD(final ShieldManager shieldManager) {
        this.shieldManager = shieldManager;
        /** Access shieldManager's instance and register using the command manager */
        shieldManager.getInstance().getCommandManager().registerCommand(this);
    }

    @CommandCompletion("<name>")
    @CommandAlias("create")
    public void createShield(Player sender, String shieldName) {
        CompletableFuture.runAsync(() -> {
            var banner = sender.getInventory().getItemInMainHand().clone();
            try {
                var customShield = CustomShield.fromStack(shieldName, banner);
                var insertResult = shieldManager.getShieldCollection().insertOne(customShield);

                if (insertResult.getInsertedId() != null) {
                    sender.sendMessage(
                            ChatColor.GREEN + "You've created shield " + ChatColor.WHITE + customShield.getName()
                                    + ChatColor.GREEN + " with the data\n" + ChatColor.WHITE + customShield.toString());
                } else {
                    sender.sendMessage(ChatColor.RED + "The shield failed to be inserted.");
                }

            } catch (ShieldInvalidTypeException | MongoWriteException e) {
                ExceptionHandlers.handleVoidWithSender(null, e, sender);
            }
        });
    }

    @CommandCompletion("[limit] [skip]")
    @CommandAlias("list")
    public void getShields(CommandSender sender, @Default("10") Integer limit, @Default("0") Integer skip) {
        CompletableFuture.runAsync(() -> {
            var query = shieldManager.getShieldCollection().find().limit(limit).skip(skip);
            var iter = query.iterator();
            int current = 0;
            var message = header;

            while (iter.hasNext()) {
                var next = iter.next();
                message += (color + ((++current) + skip) + ". " + color2 + next + "\n");
            }
            if (current == 0) {
                message += String.format("%3$sNo entries could be found with limit %4$s%d %3$sand skip %4$s%d\n", limit,
                        skip, ChatColor.RED, ChatColor.WHITE);
            }

            sender.sendMessage(message + ChatColor.RESET + decorator);

        });
    }

    @SuppressWarnings("all")
    @CommandCompletion("@players")
    @Subcommand("get")
    public void getShieldProfile(CommandSender sender, String playerId) {
        CompletableFuture.runAsync(() -> {
            UUID uid = null;
            /** Attempt to make sense of the given argument */
            var player = Bukkit.getPlayer(playerId);
            if (player != null) {
                /** If the given string is a Player, then do no further. */
                uid = player.getUniqueId();
            } else {
                try {
                    /** Attempt to parse the string as an UUID object */
                    var id = UUID.fromString(playerId);
                    if (id != null)
                        uid = id;

                } catch (IllegalArgumentException e) {
                    /**
                     * If IllegalArgumentException is thrown, the playerId is not parseable as uuid.
                     * Attempt to get an OfflinePlayer from the given string.
                     */
                    var offlinePlayer = Bukkit.getOfflinePlayer(playerId);
                    /** If found, set the uid to the objects UUID. */
                    if (offlinePlayer != null)
                        uid = offlinePlayer.getUniqueId();
                }
            }
            /** If at this point the uid is null, return error. */
            if (uid == null) {
                sender.sendMessage(ChatColor.RED + "A mojang profile couldn't be found for " + playerId);
                return;
            }
            /** Attempt to query the latest data avaialable for this user */
            var shieldProfile = shieldManager.getShieldProfile(uid.toString());

            /** If not found, exit with error. */
            if (shieldProfile == null) {
                sender.sendMessage(ChatColor.RED + "A shield profile couldn't be found for " + playerId);
                return;
            }
            /** At this point, everything should be valid */
            sender.sendMessage(ChatColor.GREEN + "Shield for " + ChatColor.WHITE + playerId + " ("
                    + shieldProfile.getUuid() + ")" + ChatColor.GREEN + " is " + shieldProfile.getShield());

        }).handle((a, b) -> ExceptionHandlers.handleVoidWithSender(a, b, sender));

    }

    @CommandCompletion("<shieldName>")
    @CommandAlias("change-shield")
    public void changeShield(Player sender, @Name("shield_id") String shieldName) {
        CompletableFuture.runAsync(() -> {
            try {
                var shieldProfile = shieldManager.changeShield(sender.getUniqueId().toString(), shieldName);
                if (shieldProfile != null) {
                    sender.sendMessage(ChatColor.GREEN + "You've changed your shield to " + shieldName);
                    ShieldManager.getShieldProfileCache().put(sender.getUniqueId().toString(), shieldProfile);
                }
            } catch (ShieldNotFoundException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }

        }).handle((a, b) -> ExceptionHandlers.handleVoidWithSender(a, b, sender));
    }

    @CommandCompletion("<shieldName>")
    @CommandAlias("delete")
    public void removeShield(CommandSender sender, @Name("shield_id") String shieldName) {

    }

}
