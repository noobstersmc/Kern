package net.noobsters.kern.paper.portal;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import net.noobsters.kern.paper.Kern;

@CommandAlias("portals")
public class PortalListeners extends BaseCommand implements Listener {

    private Kern instance;

    public PortalListeners(final Kern instance) {
        this.instance = instance;
        instance.getListenerManager().registerListener(this);
        instance.getCommandManager().registerCommand(this);
    }

    @CommandCompletion("@worlds")
    @Default
    public void findPortal(final Player player, @Name("world") final World world) {

    }
}
