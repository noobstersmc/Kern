package net.noobsters.kern.paper.shield.jcedeno.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import lombok.Getter;
import net.noobsters.kern.paper.shield.jcedeno.ShieldManager;

@CommandPermission("kern.shield")
@CommandAlias("sh")
public class ShieldCMD extends BaseCommand {
    private @Getter ShieldManager shieldManager;

    /**
     * Constructor that auto-registers the command of this class.
     * 
     * @param instance Kern plugin instance
     */
    public ShieldCMD(final ShieldManager shieldManager) {
        this.shieldManager = shieldManager;
        /** Access shieldManager's instance and register using the command manager */
        shieldManager.getInstance().getCommandManager().registerCommand(this);

    }

}
