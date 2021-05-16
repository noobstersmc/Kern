package net.noobsters.kern.paper;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.noobsters.kern.paper.chat.ChatManager;
import net.noobsters.kern.paper.commands.GlobalMute;
import net.noobsters.kern.paper.condor.CondorManager;
import net.noobsters.kern.paper.guis.RapidManager;
import net.noobsters.kern.paper.listeners.ListenerManager;
import net.noobsters.kern.paper.profiles.ProfileManager;
import net.noobsters.kern.paper.shield.ShieldManager;
import net.noobsters.kern.paper.stats.StatsManager;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;

public class Kern extends JavaPlugin {
  private @Getter PaperCommandManager commandManager;
  private @Getter ListenerManager listenerManager;
  private @Getter ChatManager chatManager;
  private @Getter ShieldManager shieldManager;
  private @Getter CondorManager condorManager;
  private @Getter ProfileManager profileManager;
  private @Getter StatsManager statsManager;

  private static @Getter Kern instance;

  @Override
  public void onEnable() {

    instance = this;

    // managers
    chatManager = new ChatManager(this);
    commandManager = new PaperCommandManager(this);
    listenerManager = new ListenerManager(this);

    // commands
    chatManager = new ChatManager(this);

    // commands
    commandManager.registerCommand(new GlobalMute(this));

    /** Do this last always */
    this.condorManager = new CondorManager(this);
    this.profileManager = new ProfileManager(this);
    this.statsManager = new StatsManager(this);

    /** RapidInv manager */
    RapidManager.register(this);
  }

  @Override
  public void onDisable() {
    condorManager.getMongoHynix().getMongoClient().close();

  }

}