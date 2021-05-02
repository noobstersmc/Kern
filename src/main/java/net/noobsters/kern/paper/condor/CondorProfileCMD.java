package net.noobsters.kern.paper.condor;

import java.util.concurrent.CompletableFuture;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

@CommandPermission("condor.cmd")
@CommandAlias("tokens")
public class CondorProfileCMD extends BaseCommand {
    private @Getter CondorManager condorManager;

    public CondorProfileCMD(CondorManager condorManager) {
        this.condorManager = condorManager;
    }

    @CommandCompletion("@players")
    @Subcommand("info")
    public void getInfo(CommandSender sender, @Name("token") String tokenIdName) {
        CompletableFuture.runAsync(() -> {
            var profile = getProfile(tokenIdName);
            if (profile != null) {
                sender.sendMessage(profile.stringifiedSummary());
            } else {
                sender.sendMessage(ChatColor.RED + "A profile couldn't be found with the token " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    private CondorProfile getProfile(String tokenIdName) {
        /** Try to query token from mongo */
        var collection = getCollection();
        var prof = collection.find(Filters.or(Filters.eq("token", tokenIdName), Filters.eq("name", tokenIdName)))
                .first();
        /** Return profile if one was found */
        if (prof != null) {
            return prof;
        }
        /** Check if bukkit has a local cached version of the given string */
        var cached = Bukkit.getOfflinePlayerIfCached(tokenIdName);
        if (cached != null) {
            var query = collection.find(Filters.eq("token", cached.getUniqueId().toString())).first();
            if (query != null) {
                return query;
            }
        }
        /** Query info of whatever the string is */
        var playerInfoQuery = PlayerDBUtil.getPlayerObject(tokenIdName);
        /** If PlayerDB query returns an object, the player is valid */
        if (playerInfoQuery != null) {
            var id = playerInfoQuery.get("id");
            /** Ensure the ID is not null */
            if (id != null) {
                var query = collection.find(Filters.eq("token", id.getAsString())).first();
                /** If the response if not null, the player already has a condor profile */
                if (query != null) {
                    return query;
                }

            }
        }

        /** If code ever reacher down here, return null */
        return null;
    }

    /**
     * Util function to quickly acces condor collection.
     * 
     * @return Same as {@link CondorManager#getCondorCollection()}
     */
    private MongoCollection<CondorProfile> getCollection() {
        return condorManager.getCondorCollection();
    }

/* 
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

    @CommandCompletion("<token>")
    @Subcommand("get token")
    public void getProfileFromToken(CommandSender sender, @Name("userToken") String token) {
        var condorProfile = condorManager.getCondorCollection().find(Filters.eq("token", token)).first();
        if (condorProfile != null) {
            sender.sendMessage("Query found:\n" + gson.toJson(condorProfile));

        } else {
            sender.sendMessage("Couldn't find a profile for your given token.");
        }

    }

    @CommandCompletion("<token>")
    @Subcommand("get active")
    public void getInstances(CommandSender sender, @Name("userToken") String token) {

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
 */
}
