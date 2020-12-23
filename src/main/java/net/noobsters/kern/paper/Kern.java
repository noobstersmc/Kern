package net.noobsters.kern.paper;

import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.noobsters.kern.paper.databases.DatabaseManager;
import net.noobsters.kern.paper.listeners.ListenerManager;
import net.noobsters.kern.paper.listeners.ShieldListeners;
import net.noobsters.kern.paper.portal.PortalListeners;
import net.noobsters.kern.paper.shield.Shields;
import net.noobsters.kern.paper.shield.YML;

/**
 * Core
 */
public class Kern extends JavaPlugin {
    private @Getter YML shieldPatterns;
    private @Getter Shields shields;
    private @Getter PaperCommandManager commandManager;
    private @Getter ListenerManager listenerManager;
    private @Getter DatabaseManager databaseManager;

    // GUI tutorial: https://github.com/MrMicky-FR/FastInv
    // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
    // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF

    private static @Getter Kern instance;

    @Override
    public void onEnable() {
        instance = this;

        WorldCreator wc = new WorldCreator("world_nether");
        wc.environment(Environment.NETHER);
        wc.createWorld();
        
        //databaseManager = new DatabaseManager(this);
        
        commandManager = new PaperCommandManager(this);
        listenerManager = new ListenerManager(this);
        shieldPatterns = new YML(instance.getDataFolder(), "shields", false);
        shields = new Shields(this);

        new PortalListeners(this);



        this.getServer().getPluginManager().registerEvents(new ShieldListeners(this), this);

    }

    @Override
    public void onDisable() {
        //Disconnect from mongodb
        this.databaseManager.getMongoClient().close();

    }

}