package net.noobsters.kern.paper.punishments.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.noobsters.kern.paper.Kern;
import net.noobsters.kern.paper.punishments.database.objects.Ban;

@CommandAlias("ban")
public class BanCommand extends BaseCommand {
    private Kern instance;

    public BanCommand(Kern instance) {
        this.instance = instance;
        instance.getCommandManager().registerCommand(this, true);
    }

    @Default
    public void onDefault(Player sender, @Flags("@other") OnlinePlayer target, int seconds, String reason) {
        Ban.create(reason, sender.getUniqueId(), System.currentTimeMillis(),
                System.currentTimeMillis() + (seconds * 1_000)).execute(target.getPlayer());
    }
}
