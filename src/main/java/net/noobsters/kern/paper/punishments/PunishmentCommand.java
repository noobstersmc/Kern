package net.noobsters.kern.paper.punishments;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.mongodb.client.model.Filters;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.profiles.PlayerProfile;
import net.noobsters.kern.paper.profiles.ProfileManager;
import net.noobsters.kern.paper.punishments.exceptions.ExceptionHandlers;
import net.noobsters.kern.paper.punishments.gui.PunizioneInfoGui;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

@RequiredArgsConstructor
@CommandAlias("punizione")
public class PunishmentCommand extends BaseCommand {
    private @NonNull @Getter Kern instance;

    @CommandCompletion("@players")
    @Subcommand("profile")
    public void reviewCommand(Player sender, @Name("name") String nameOrId) {
        var uid = getId(nameOrId);
        var profile = instance.getProfileManager().getCollection()
                .find(Filters.eq(uid != null ? "_id" : "name", nameOrId)).first();

        if (profile != null) {
            var gui = new PunizioneInfoGui(profile).getGui();
            gui.open(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "The profile you requested hasn't joined the server before...");
        }

    }

    @CommandCompletion("@players <duration> <reason>")
    @Subcommand("ban|b")
    public void banCommand(CommandSender sender, @Name("name") String nameOrId, @Name("Duration") String duration,
            @Name("Reason") String reason) {
        CompletableFuture.runAsync(() -> {
            var target = Bukkit.getOfflinePlayerIfCached(nameOrId);
            UUID uuid = null;

            if (target == null) {
                var id = getId(nameOrId);
                if (id != null) {
                    uuid = id;
                }
            } else {
                uuid = target.getUniqueId();
            }

            if (uuid != null) {
                var cachedProfile = ProfileManager.getCache().get(uuid.toString());

                var durationParsed = BanUnits.parseString(duration);
                /** Now that we have the profile of the player, let's create a ban object */
                var ban = Punishment.of(sender.getName(), reason, System.currentTimeMillis() + durationParsed,
                        System.currentTimeMillis(), PunishmentType.BAN, false);

                if (cachedProfile != null) {
                    cachedProfile.commitPunishment(ban, instance.getProfileManager().getCollection());

                    sender.sendMessage(ChatColor.GREEN + "You've banned " + nameOrId + " with the reason " + reason
                            + " and duration " + duration);
                    return;
                }

            }
            sender.sendMessage("Couldn't find a player " + nameOrId);
        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    @CommandCompletion("@players <duration> <reason>")
    @Subcommand("mute|m")
    public void muteCommand(CommandSender sender, @Name("name") String nameOrId, @Name("Duration") String duration,
            @Name("Reason") String reason) {
        CompletableFuture.runAsync(() -> {
            var target = Bukkit.getOfflinePlayerIfCached(nameOrId);
            UUID uuid = null;

            if (target == null) {
                var id = getId(nameOrId);
                if (id != null) {
                    uuid = id;
                } else {
                    var playerLookupObject = PlayerDBUtil.getPlayerObject(nameOrId);
                    if (playerLookupObject != null) {
                        uuid = UUID.fromString(playerLookupObject.get("id").getAsString());
                    } else {
                        sender.sendMessage(ChatColor.RED + "No player named " + nameOrId
                                + " could be found in the mojang record.");
                        return;
                    }
                }
            } else {
                uuid = target.getUniqueId();
            }

            if (uuid != null) {
                var cachedProfile = ProfileManager.getCache().get(uuid.toString());
                if (cachedProfile == null) {
                    System.out.println(ChatColor.YELLOW + "[Punizione] Player " + nameOrId
                            + "is not cached. Performing a lookup.");
                    try {
                        var optionalProfile = instance.getProfileManager().queryPlayer(uuid).get();
                        if (optionalProfile.isPresent()) {
                            cachedProfile = optionalProfile.get();
                        } else {
                            return;
                        }
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + e.toString());
                        e.printStackTrace();
                        return;
                    }
                }

                var durationParsed = BanUnits.parseString(duration);
                /** Now that we have the profile of the player, let's create a ban object */
                var mute = Punishment.of(sender.getName(), reason, System.currentTimeMillis() + durationParsed,
                        System.currentTimeMillis(), PunishmentType.MUTE, false);

                if (cachedProfile != null) {
                    cachedProfile.commitPunishment(mute, instance.getProfileManager().getCollection());

                    sender.sendMessage(ChatColor.GREEN + "You've muted " + nameOrId + " with the reason " + reason
                            + " and duration " + duration);
                    return;
                }

            }
            sender.sendMessage("Couldn't find a player " + nameOrId);
        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    @CommandCompletion("@players")
    @Subcommand("unmute|um")
    public void unMute(CommandSender sender, @Name("name") String nameOrId) {
        CompletableFuture.runAsync(() -> {
            var target = Bukkit.getOfflinePlayerIfCached(nameOrId);
            UUID uuid = null;
            PlayerProfile profile = null;

            if (target == null) {
                var id = getId(nameOrId);
                if (id != null) {
                    uuid = id;
                } else {
                    var playerLookupObject = PlayerDBUtil.getPlayerObject(nameOrId);
                    if (playerLookupObject != null) {
                        uuid = UUID.fromString(playerLookupObject.get("id").getAsString());
                    } else {
                        sender.sendMessage(ChatColor.RED + "No player named " + nameOrId
                                + " could be found in the mojang record.");
                        return;
                    }
                }
            } else {
                uuid = target.getUniqueId();
            }

            if (uuid != null) {
                var cachedProfile = ProfileManager.getCache().get(uuid.toString());

                if (cachedProfile != null) {
                    profile = cachedProfile;
                } else {
                    // If not cached, check the database.
                    System.out.println(ChatColor.YELLOW + "[Punizione] Player " + nameOrId
                            + "is not cached. Performing a lookup.");
                    try {
                        var optionalProfile = instance.getProfileManager().queryPlayer(uuid).get();
                        if (optionalProfile.isPresent()) {
                            profile = optionalProfile.get();
                        } else {
                            sender.sendMessage(ChatColor.RED + nameOrId
                                    + " can't be unmuted because they haven't joined the server before.");
                            return;
                        }
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.RED + e.toString());
                        e.printStackTrace();
                        return;
                    }
                }

                var mute = profile.isMuted();
                if (mute == null) {
                    sender.sendMessage(ChatColor.RED + profile.getName() + " is not currently muted.");
                    return;
                }
                // TODO: Pardon the muted player
                final var prof = profile;
                profile.pardonPunishment(mute, instance.getProfileManager().getCollection()).thenAccept((c) -> {
                    if (c) {
                        sender.sendMessage(ChatColor.GREEN + "You've succesful unmuted " + prof.getName());

                    } else {
                        sender.sendMessage(
                                ChatColor.RED + "Couldn't pardon " + prof.getName() + "'s mute " + mute.toString());

                    }
                });

            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));

    }

    @CommandCompletion("@players")
    @Subcommand("unban|pardon")
    public void unBan(CommandSender sender, @Name("name") String nameOrId) {
        CompletableFuture.runAsync(() -> {
            var target = Bukkit.getOfflinePlayerIfCached(nameOrId);
            UUID uuid = null;
            PlayerProfile profile = null;

            if (target == null) {
                var id = getId(nameOrId);
                if (id != null) {
                    uuid = id;
                } else {
                    var playerLookupObject = PlayerDBUtil.getPlayerObject(nameOrId);
                    if (playerLookupObject != null) {
                        uuid = UUID.fromString(playerLookupObject.get("id").getAsString());
                    } else {
                        sender.sendMessage(ChatColor.RED + "No player named " + nameOrId
                                + " could be found in the mojang record.");
                        return;
                    }
                }
            } else {
                uuid = target.getUniqueId();
            }

            if (uuid != null) {
                try {
                    var optionalProfile = instance.getProfileManager().queryPlayer(uuid).get();
                    if (optionalProfile.isPresent()) {
                        profile = optionalProfile.get();
                    } else {
                        sender.sendMessage(ChatColor.RED + nameOrId
                                + " can't be unbanned because they haven't joined the server before.");
                        return;
                    }
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + e.toString());
                    e.printStackTrace();
                    return;
                }

                var ban = profile.isBanned();
                if (ban == null) {
                    sender.sendMessage(ChatColor.RED + profile.getName() + " is not currently banned.");
                    return;
                }
                // TODO: Pardon the banned player
                final var prof = profile;
                profile.pardonPunishment(ban, instance.getProfileManager().getCollection()).thenAccept((c) -> {
                    if (c) {
                        sender.sendMessage(ChatColor.GREEN + "You've succesful unbanned " + prof.getName());

                    } else {
                        sender.sendMessage(
                                ChatColor.RED + "Couldn't pardon " + prof.getName() + "'s ban " + ban.toString());

                    }
                });

            }

        }).handle((result, ex) -> ExceptionHandlers.handleVoidWithSender(result, ex, sender));
    }

    UUID getId(String str) {
        try {
            return UUID.fromString(str);
        } catch (Exception e) {
        }
        return null;
    }

    @CommandCompletion("@players")
    @Subcommand("record")
    public void playerRecord(CommandSender sender, String name) {
        var str = new StringBuilder("----------------\n");
        var profile = PlayerDBUtil.getPlayerObject(name);
        if (profile == null) {
            sender.sendMessage("Couldn't find a minecraft player named " + name);
            return;
        }
        var playerName = profile.get("username").getAsString();
        var playerUUID = profile.get("id").getAsString();
        str.append("name: " + playerName + "\n");
        str.append("uuid: " + playerUUID + "\n");
        str.append("previous names: ");
        var nameHistory = profile.getAsJsonObject("meta").getAsJsonArray("name_history");
        var iter = nameHistory.iterator();
        while (iter.hasNext()) {
            var oldName = iter.next().getAsJsonObject();
            str.append(oldName.get("name").getAsString() + (iter.hasNext() ? ", " : "."));
        }
        str.append("\n----------------");

        sender.sendMessage(str.toString());

    }

}
