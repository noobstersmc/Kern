package net.noobsters.kern.paper.commands;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
@CommandPermission("tpworld.cmd")
@CommandAlias("shield")
public class ShieldCMD extends BaseCommand {

    private @NonNull Kern instance;

    @Default
    public void shieldMenu(Player sender) {
        sender.sendMessage("Soon...");
    }

}