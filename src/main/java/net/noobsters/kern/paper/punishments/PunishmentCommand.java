package net.noobsters.kern.paper.punishments;

import java.text.ParseException;
import java.util.UUID;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.utils.PlayerDBUtil;

@RequiredArgsConstructor
@CommandAlias("punizione")
public class PunishmentCommand extends BaseCommand {
    private @NonNull @Getter Kern instance;

    @CommandCompletion("@players")
    @Subcommand("check")
    public void checkPlayer(CommandSender sender, @Flags("other") Player target) {
        var profile = instance.getPunishmentManager().getOrCreatePlayerProfile(target.getUniqueId().toString());
        sender.sendMessage(profile.toString());

    }

    @Subcommand("unban|pardon")
    public void unBan(CommandSender sender, @Name("name") String nameOrID) {
        /** Find the player or its id anywhere */
        var id = getId(nameOrID);
        if (id == null) {
            var offlinePlayer = Bukkit.getOfflinePlayerIfCached(nameOrID);
            if (offlinePlayer != null) {
                id = offlinePlayer.getUniqueId();
            } else {
                var playerProf = PlayerDBUtil.getPlayerObject(nameOrID);
                if (playerProf == null) {
                    sender.sendMessage("Couldn't find a minecraft player named " + nameOrID);
                    return;
                }
                id = UUID.fromString(playerProf.get("id").getAsString());
            }
        }
        /** Pop the first punishment */
        var profile = instance.getPunishmentManager().getCollection().findOneAndUpdate(Filters.eq("_id", id.toString()),
                Updates.popFirst("bans"));
        if (profile != null && !profile.getBans().isEmpty()) {
            sender.sendMessage("Ban " + profile.getBans().get(0) + " has been removed");
        } else {
            sender.sendMessage("Player was found but no bans where found on their profile");
        }
    }

    @CommandCompletion("@players")
    @Subcommand("ban")
    public void banPlayer(CommandSender sender, String nameOrID, String duration, String reason) throws ParseException {
        /* Figure out wheter it is a name or a uuid */
        PlayerProfile profile;
        var id = getId(nameOrID);
        if (id != null) {
            profile = instance.getPunishmentManager().getOrCreatePlayerProfile(id.toString());
        } else {
            /* If it was a name, check if bukkit known the uuid, otherwise call playerdb */
            var offlinePlayer = Bukkit.getOfflinePlayerIfCached(nameOrID);
            if (offlinePlayer != null) {
                profile = instance.getPunishmentManager()
                        .getOrCreatePlayerProfile(offlinePlayer.getUniqueId().toString());
            } else {
                // Query the user's data
                var playerProf = PlayerDBUtil.getPlayerObject(nameOrID);
                if (playerProf == null) {
                    sender.sendMessage("Couldn't find a minecraft player named " + nameOrID);
                    return;
                }
                var actualId = playerProf.get("id").getAsString();
                profile = instance.getPunishmentManager().getOrCreatePlayerProfile(actualId);
            }
        }
        /** Parse the duration from stirng to ms */
        var durationParsed = BanUnits.parseString(duration);
        /** Now that we have the profile of the player, let's create a ban object */
        var ban = Punishment.of(sender.getName(), reason, System.currentTimeMillis() + durationParsed,
                System.currentTimeMillis(), PunishmentType.BAN, false);
        /** Now execute the ban */
        ban.performPunishment(instance.getPunishmentManager().getCollection(), profile);
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
