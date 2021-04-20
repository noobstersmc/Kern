package net.noobsters.kern.paper.condor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bson.conversions.Bson;
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

@CommandPermission("condor.cmd")
@CommandAlias("tokens")
public class CondorProfileCMD extends BaseCommand {
    private @Getter CondorManager condorManager;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public CondorProfileCMD(CondorManager condorManager) {
        this.condorManager = condorManager;
    }

    @CommandCompletion("Integer")
    @Subcommand("get-all")
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

    @Subcommand("get-token")
    public void getProfileFromToken(CommandSender sender, @Name("userToken") String token) {
        var condorProfile = condorManager.getCondorCollection().find(Filters.eq("token", token)).first();
        if (condorProfile != null) {
            sender.sendMessage("Query found:\n" + gson.toJson(condorProfile));

        } else {
            sender.sendMessage("Couldn't find a profile for your given token.");
        }

    }

    @CommandCompletion("token @condor_fields")
    @Subcommand("edit-token")
    public void editProfileToken(CommandSender sender, @Default("own") @Name("userToken") String token,
            @Name("field-to-edit") String fieldName, @Name("new-field-value") String value) {
        if (token.equalsIgnoreCase("own") && sender instanceof Player) {
            token = ((Player) sender).getUniqueId().toString();
        }

        var collection = condorManager.getCondorCollection();

        var update = obtainFieldUpdate(fieldName, value);
        if (update != null) {
            var profile = collection.findOneAndUpdate(Filters.eq("token", token), update);
            if (profile != null) {
                sender.sendMessage(ChatColor.GREEN + "Profile " + profile.getName() + " has been updated:\n"
                        + ChatColor.GRAY + gson.toJson(profile) + ChatColor.GREEN + "\nUpdate: \n" + ChatColor.GRAY
                        + update.toString());

            }
        } else {
            sender.sendMessage(ChatColor.RED + "The field you wish to update is not known.\nTry again with:"
                    + ChatColor.YELLOW + " name, token, credits, limit, or super!");
        }

    }

    private Bson obtainFieldUpdate(String fieldName, String newValue) {
        switch (fieldName.toLowerCase()) {
            case "name": {
                return Updates.set("name", newValue);
            }
            case "token": {
                return Updates.set("token", newValue);
            }
            case "credits": {
                return Updates.set("credits", Double.parseDouble(newValue));
            }
            case "limit": {
                return Updates.set("instance_limit", Integer.parseInt(newValue));
            }
            case "super": {
                return Updates.set("super_token", Boolean.parseBoolean(newValue));
            }
        }
        return null;
    }

}
