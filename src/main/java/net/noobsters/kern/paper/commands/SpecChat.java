package net.noobsters.kern.paper.commands;

import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
@CommandPermission("admin.cmd")
@CommandAlias("specChat")
public class SpecChat extends BaseCommand {

    private @NonNull Kern instance;

    @Default
    public void toggleSpecChat(CommandSender sender) {
        var chat = instance.getChatManager();
        chat.setSpecChat(!chat.isSpecChat());
        sender.sendMessage("SpecChat " + chat.isSpecChat());
    }

}