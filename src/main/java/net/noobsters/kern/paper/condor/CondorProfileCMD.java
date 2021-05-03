package net.noobsters.kern.paper.condor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
    /*
     * TODO: Known bug, the command returns the old state of the profile but it does
     * increment the credits.
     */

    public CondorProfileCMD(CondorManager condorManager) {
        this.condorManager = condorManager;
    }

    @CommandCompletion("@players")
    @Subcommand("info")
    public void getInfo(CommandSender sender, @Name("token") String tokenIdName) {
        CompletableFuture.runAsync(() -> {
            var profile = getProfile(tokenIdName, null, null);
            if (profile != null) {
                sender.sendMessage(profile.stringifiedSummary());
            } else {
                sender.sendMessage(ChatColor.RED + "A profile couldn't be found with the token " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));
    }

    @CommandCompletion("@players <credits>")
    @Subcommand("set credits")
    public void setCondorProfile(CommandSender sender, @Name("token") String tokenIdName,
            @Name("credits") Integer credits) {
        CompletableFuture.runAsync(() -> {
            var profile = getOrCreateProfile(sender, tokenIdName);
            if (profile != null) {
                var updatedProfile = getCollection().findOneAndUpdate(Filters.eq("token", profile.getToken()),
                        Updates.set("credits", credits),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
                if (updatedProfile != null) {
                    sender.sendMessage(ChatColor.GREEN + "Profile for " + tokenIdName + " was found and updated to:"
                            + ChatColor.RESET + "\n" + profile.stringifiedSummary());
                } else {
                    sender.sendMessage(ChatColor.RED + "No condor profile could be updated for " + tokenIdName);

                }

            } else {
                sender.sendMessage(ChatColor.RED + "No condor profile could be found or created for " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    @CommandCompletion("@players <credits>")
    @Subcommand("add credits")
    public void addCredits(CommandSender sender, @Name("token") String tokenIdName, @Name("credits") Integer credits) {
        CompletableFuture.runAsync(() -> {
            var profile = getOrCreateProfile(sender, tokenIdName);
            if (profile != null) {
                var updatedProfile = getCollection().findOneAndUpdate(Filters.eq("token", profile.getToken()),
                        Updates.inc("credits", credits),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
                if (updatedProfile != null) {
                    sender.sendMessage(ChatColor.GREEN + "Profile for " + tokenIdName
                            + " was found and its credits were incremented by " + credits + " to:" + ChatColor.RESET
                            + "\n" + profile.stringifiedSummary());
                } else {
                    sender.sendMessage(ChatColor.RED + "No condor profile could be updated for " + tokenIdName);

                }

            } else {
                sender.sendMessage(ChatColor.RED + "No condor profile could be found or created for " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));
    }

    @CommandCompletion("@players <credits>")
    @Subcommand("remove credits")
    public void removeCredits(CommandSender sender, @Name("token") String tokenIdName,
            @Name("credits") Integer credits) {
        CompletableFuture.runAsync(() -> {
            var profile = getOrCreateProfile(sender, tokenIdName);
            if (profile != null) {
                var updatedProfile = getCollection().findOneAndUpdate(Filters.eq("token", profile.getToken()),
                        Updates.inc("credits", (-credits)),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
                if (updatedProfile != null) {
                    sender.sendMessage(ChatColor.GREEN + "Profile for " + tokenIdName
                            + " was found and its credits were decreased by " + (-credits) + " to:" + ChatColor.RESET
                            + "\n" + profile.stringifiedSummary());
                } else {
                    sender.sendMessage(ChatColor.RED + "No condor profile could be updated for " + tokenIdName);

                }

            } else {
                sender.sendMessage(ChatColor.RED + "No condor profile could be found or created for " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));
    }

    @CommandCompletion("@players <instance_limit>")
    @Subcommand("set instance-limit")
    public void setInstanceLimit(CommandSender sender, @Name("token") String tokenIdName,
            @Name("instance_limit") Integer instanceLimit) {
        CompletableFuture.runAsync(() -> {
            var profile = getProfile(tokenIdName, null, null);
            if (profile != null) {
                var updatedProfile = getCollection().findOneAndUpdate(Filters.eq("token", profile.getToken()),
                        Updates.set("instance_limit", instanceLimit),
                        new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER));
                if (updatedProfile != null) {
                    sender.sendMessage(ChatColor.GREEN + "Profile for " + tokenIdName + " was found and updated to:"
                            + ChatColor.RESET + "\n" + profile.stringifiedSummary());
                } else {
                    sender.sendMessage(ChatColor.RED + "No condor profile could be updated for " + tokenIdName);

                }

            } else {
                sender.sendMessage(ChatColor.RED + "No condor profile could be found or created for " + tokenIdName);
            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    /**
     * Utility function to obtain the profile for an specified token or player, if
     * present
     * 
     * @param tokenIdName     Token, UUID, or Name of the player.
     * @param offlineFunction Function<{@link OfflinePlayer}, {@link CondorProfile}>
     *                        that runs if not null when the offlinePlayer query
     *                        doesn't return a profile.
     * @param jsonFunction    Function<{@link JsonObject}, {@link CondorProfile}>
     *                        that runs if not null when the
     *                        {@link PlayerDBUtil#getPlayerObject(String)} query
     *                        doesn't return a profile.
     * @return {@link CondorProfile} or null if not found, or not created by any of
     *         the provided functions.
     */
    private CondorProfile getProfile(String tokenIdName, Function<OfflinePlayer, CondorProfile> offlineFunction,
            Function<JsonObject, CondorProfile> jsonFunction) {
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
            /**
             * If the query didn't find a profile, allow the function, if present, to
             * execute arbitrary instructions
             */
            if (offlineFunction != null) {
                var maybeNewProfile = offlineFunction.apply(cached);
                if (maybeNewProfile != null) {
                    return maybeNewProfile;
                }
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
                /**
                 * If the query didn't find a profile, allow the function, if present, to
                 * execute arbitrary instructions
                 */
                if (jsonFunction != null) {
                    var maybeNewProfile = jsonFunction.apply(playerInfoQuery);
                    if (maybeNewProfile != null) {
                        return maybeNewProfile;
                    }
                }

            }
        }

        /** If code ever reacher down here, return null */
        return null;
    }

    /**
     * Utility function that looks for a condor profile or creates it if one is not
     * found.
     * 
     * @param tokenIdName Token, UUID, or Name of the player.
     * @return Either a found or created {@link CondorProfile}.
     */
    private CondorProfile getOrCreateProfile(CommandSender sender, String tokenIdName) {
        final var collection = getCollection();
        return getProfile(tokenIdName, (ofp) -> {
            /** Define variables */
            var id = ofp.getUniqueId().toString();
            var username = ofp.getName();
            /** Create local profile */
            var newProfile = CondorProfile.createDefaults(id, username);
            /** Insert onto database */
            collection.insertOne(newProfile);
            /** Log back to sender and return new object */
            sender.sendMessage(ChatColor.YELLOW
                    + String.format("A new profile has been created for %s (%s) during the ofp stage.", username, id));
            return newProfile;
        }, (json) -> {
            /** Define variables */
            var id = json.get("id").getAsString();
            var username = json.get("username").getAsString();
            /** Create local profile */
            var newProfile = CondorProfile.createDefaults(id, username);
            /** Insert onto database */
            collection.insertOne(newProfile);
            /** Log back to sender and return new object */
            sender.sendMessage(ChatColor.YELLOW
                    + String.format("A new profile has been created for %s (%s) during the json stage.", username, id));
            return newProfile;
        });
    }

    /**
     * Util function to quickly acces condor collection.
     * 
     * @return Same as {@link CondorManager#getCondorCollection()}
     */
    private MongoCollection<CondorProfile> getCollection() {
        return condorManager.getCondorCollection();
    }
}
