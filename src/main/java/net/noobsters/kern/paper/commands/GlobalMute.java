package net.noobsters.kern.paper.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
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
    public void onCommand(CommandSender sender, @Optional Boolean bool) {
        var chat = instance.getChatManager();
        if(bool == null){
            chat.setGlobalmute(!chat.isGlobalmute());
        }else{
            chat.setGlobalmute(bool);
        }

        Bukkit.broadcastMessage(ChatColor.of("#2be49c")
                + (chat.isGlobalmute() ? "Globalmute Enabled." : "Globalmute Disabled."));

    }

}
