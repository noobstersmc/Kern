package net.noobsters.kern.paper.condor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.Filters;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@CommandPermission("condor.cmd")
@CommandAlias("condor")
public class CondorProfileCMD extends BaseCommand {
    private @Getter CondorManager condorManager;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CondorProfileCMD(CondorManager condorManager) {
        this.condorManager = condorManager;
    }

    @CommandCompletion("Integer")
    @Subcommand("get all")
    public void getAll(CommandSender sender, @Name("page") @Default("0") Integer page) {
        var offset = page;
        var query = condorManager.getCondorCollection().find().limit(5).skip((offset));
        var iter = query.iterator();
        var count = 0;
        while (iter.hasNext()) {
            count++;
            var nextElement = iter.next();
            sender.sendMessage(
                    ChatColor.GOLD + "" + (count + offset) + ". " + ChatColor.WHITE + gson.toJson(nextElement));
        }

    }

    @Subcommand("get token")
    public void getProfileFromToken(CommandSender sender, @Name("userToken") String token) {
        var condorProfile = condorManager.getCondorCollection().find(Filters.eq("token", token)).first();
        if (condorProfile != null) {
            sender.sendMessage("Query found:\n" + gson.toJson(condorProfile));

        } else {
            sender.sendMessage("Couldn't find a profile for your given token.");
        }

    }

    @CommandCompletion("@players")
    @Subcommand("get player")
    public void getProfileFromPlayer(CommandSender sender, @Name("playerName") @Flags("other") String playerName) {
        var uuid = condorManager.getInstance().getPunishmentManager().findUUIDOfName(playerName);
        if (uuid != null) {
            getProfileFromToken(sender, uuid.toString());
        } else {
            sender.sendMessage("Couldn't find a player with that name");
        }

    }

}
