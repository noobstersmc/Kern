package net.noobsters.core.paper;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.noobsters.core.paper.Listeners.ListenerManager;
import net.noobsters.core.paper.Listeners.ShieldListeners;
import net.noobsters.core.paper.shield.Shields;

/**
 * Core
 */
public class Core extends JavaPlugin {
    private @Getter YML shieldPatterns;
    private @Getter Shields shields;
    private @Getter PaperCommandManager commandManager;
    private @Getter ListenerManager listenerManager;

    // GUI tutorial: https://github.com/MrMicky-FR/FastInv
    // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
    // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF

    private static @Getter Core instance;

    @Override
    public void onEnable() {

        instance = this;

        commandManager = new PaperCommandManager(this);
        listenerManager = new ListenerManager(this);
        shieldPatterns = new YML(instance.getDataFolder(), "shields", false);
        shields = new Shields(this);

        this.getServer().getPluginManager().registerEvents(new ShieldListeners(this), this);

    }

    @Override
    public void onDisable() {

    }

}