package net.noobsters.kern.paper.shield.jcedeno.commands;

import com.mongodb.MongoWriteException;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;
import net.noobsters.kern.paper.shield.jcedeno.exceptions.ShieldInvalidTypeException;
import net.noobsters.kern.paper.shield.jcedeno.objects.CustomShield;

@CommandPermission("kern.shield")
@CommandAlias("sh")
public class ShieldCMD extends BaseCommand {
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

    @CommandAlias("create")
    @CommandCompletion("<name>")
    public void createShield(Player sender, String shieldName) {
        var banner = sender.getInventory().getItemInMainHand().clone();
        try {
            var customShield = CustomShield.fromStack(shieldName, banner);
            var insertResult = shieldManager.getShieldCollection().insertOne(customShield);

            if (insertResult.getInsertedId() != null) {
                sender.sendMessage(ChatColor.GREEN + "You've created shield " + ChatColor.WHITE + customShield.getName()
                        + ChatColor.GREEN + " with the data\n" + ChatColor.WHITE + customShield.toString());
            } else {
                sender.sendMessage(ChatColor.RED + "The shield failed to be inserted.");
            }

        } catch (ShieldInvalidTypeException | MongoWriteException e) {
            ExceptionHandlers.handleVoidWithSender(null, e, sender);
        }
    }

}
