package net.noobsters.kern.paper.shield.jcedeno.commands;

import java.util.concurrent.CompletableFuture;

import com.mongodb.MongoWriteException;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldInvalidTypeException;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;

@CommandPermission("kern.shield")
@CommandAlias("sh")
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

    @CommandCompletion("<shieldName>")
    @CommandAlias("delete")
    public void removeShield(CommandSender sender, @Name("shield_id") String shieldName) {

    }

}
