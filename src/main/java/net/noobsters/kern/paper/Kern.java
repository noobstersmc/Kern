package net.noobsters.kern.paper;

import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.noobsters.kern.paper.chat.ChatManager;
import net.noobsters.kern.paper.commands.GlobalMute;
import net.noobsters.kern.paper.commands.SpecChat;
import net.noobsters.kern.paper.condor.CondorManager;
import net.noobsters.kern.paper.listeners.ListenerManager;
import net.noobsters.kern.paper.punishments.PunishmentManager;
import net.noobsters.kern.paper.shield.ShieldCMD;
import net.noobsters.kern.paper.shield.ShieldManager;

public class Kern extends JavaPlugin {
  // GUI tutorial: https://github.com/MrMicky-FR/FastInv
  // Scoreboard Tutorial: https://github.com/MrMicky-FR/FastBoard
  // Commands Tutorial: https://github.com/aikar/commands/wiki/Using-ACF

  private @Getter PaperCommandManager commandManager;
  private @Getter ListenerManager listenerManager;
  private @Getter ChatManager chatManager;
  private @Getter ShieldManager shieldManager;
  private @Getter PunishmentManager punishmentManager;
  private @Getter CondorManager condorManager;

  private static @Getter Kern instance;

  @Override
  public void onEnable() {

    instance = this;

    // managers
    chatManager = new ChatManager(this);
    shieldManager = new ShieldManager(this);
    commandManager = new PaperCommandManager(this);
    listenerManager = new ListenerManager(this);

    // commands
    chatManager = new ChatManager(this);

    // commands
    commandManager.registerCommand(new ShieldCMD(this));
    commandManager.registerCommand(new SpecChat(this));
    commandManager.registerCommand(new GlobalMute(this));

    /** Do this last always */
    this.punishmentManager = new PunishmentManager(this);
    this.condorManager = new CondorManager(this);

  }

  @Override
  public void onDisable() {
    punishmentManager.getMongoHynix().getMongoClient().close();

  }

}