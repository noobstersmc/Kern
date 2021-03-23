package net.noobsters.kern.paper.punishments;

import java.text.ParseException;
import java.util.UUID;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

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
        var profile = instance.getPunishmentManager().getOrCreatePlayerProfile(target.getUniqueId());
        sender.sendMessage(profile.toString());

    }

    @CommandCompletion("@players")
    @Subcommand("unban|pardon")
    public void unBan(CommandSender sender, @Name("name") String nameOrID) {
        /** Find the player or its id anywhere */
        var profile = instance.getPunishmentManager().queryPlayerIfPresent(nameOrID);
        /** Query the first ban that is still active */
        var query = Filters.and(Filters.eq("_id", profile.getUuid()), Filters.elemMatch("bans",
                Filters.and(Filters.ne("canceled", true), Filters.lt("expiration", System.currentTimeMillis()))));
        /** Execute an update */
        var update = instance.getPunishmentManager().getCollection().updateOne(query,
                Updates.set("bans.$.canceled", true));
        /** Log it to the sender */
        sender.sendMessage(update.toString());
    }

    @CommandCompletion("@players")
    @Subcommand("ban")
    public void banPlayer(CommandSender sender, @Name("Name") String nameOrID, @Name("Duration") String duration,
            @Name("Reason") String reason) throws ParseException {
        /* Figure out wheter it is a name or a uuid */
        PlayerProfile profile = instance.getPunishmentManager().getOrCreatePlayerProfile(nameOrID);
        /** Parse the duration from stirng to ms */
        var durationParsed = BanUnits.parseString(duration);
        /** Now that we have the profile of the player, let's create a ban object */
        var ban = Punishment.of(sender.getName(), reason, System.currentTimeMillis() + durationParsed,
                System.currentTimeMillis(), PunishmentType.BAN, false);
        /** Now execute the ban */
        ban.performPunishment(instance.getPunishmentManager().getCollection(), profile);
    }

    @CommandCompletion("@players")
    @Subcommand("mute")
    public void mutePlayer(CommandSender sender, @Name("Name") String nameOrID, @Name("Duration") String duration,
            @Name("Reason") String reason) throws ParseException {
        /* Figure out wheter it is a name or a uuid */
        PlayerProfile profile = instance.getPunishmentManager().getOrCreatePlayerProfile(nameOrID);
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
