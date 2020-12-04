package net.noobsters.core.paper;

import net.noobsters.core.paper.Listeners.ShieldListeners;
import net.noobsters.core.paper.shield.Shields;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * Core
 */
public class Core extends JavaPlugin{
    public static YML shields;
    // GUI tutorial: https://github.com/MrMicky-FR/FastInv
    // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
    // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF
    @Override
    public void onEnable() {
        Core.shields = new YML(getDataFolder(), "shields",false);
        new Shields(this);
        this.getServer().getPluginManager().registerEvents(new ShieldListeners(), this);

    }

    @Override
    public void onDisable() {

    }
    
}