package net.noobsters.kern.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
@CommandPermission("globalmute.cmd")
@CommandAlias("globalmute")
public class GlobalMute extends BaseCommand {
    private @NonNull Kern instance;

    @Default
    public void onCommand(CommandSender sender) {
        var chat = instance.getChatManager();
        chat.setGlobalmute(!chat.isGlobalmute());

        Bukkit.broadcastMessage(ChatColor.of("#2be49c")
                + (chat.isGlobalmute() ? "Globalmute Enabled." : "Globalmute Disabled."));

    }

}
