package net.noobsters.kern.paper.shield;

import java.util.Arrays;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
@CommandPermission("tpworld.cmd")
@CommandAlias("shield|banner")
public class ShieldCMD extends BaseCommand {

    private @NonNull Kern instance;

    @Default
    public void shieldMenu(Player sender) {
        sender.sendMessage("Soon...");
    }

    @Subcommand("list-players")
    @CommandPermission("shield.admin")
    public void listPlayers(CommandSender sender) {
        var list = instance.getShieldManager().getPlayerCurrentShield();
        sender.sendMessage(ChatColor.YELLOW + "Shield Player List: " + list.toString());
    }

    @Subcommand("list")
    @CommandPermission("shield.admin")
    public void chooseShield(CommandSender sender) {
        var list = instance.getShieldManager().getGlobalShieldList();
        sender.sendMessage(ChatColor.YELLOW + "Shield List: " + list.toString());
    }

    @Subcommand("reset|default")
    @CommandPermission("shield.admin")
    public void reset(Player player) {
        var shieldManager = instance.getShieldManager();
        var playerCurrentShield = shieldManager.getPlayerCurrentShield();
        var uuid = player.getUniqueId().toString();
        if(playerCurrentShield.containsKey(uuid)){
            playerCurrentShield.remove(uuid);

            var inventory = player.getInventory().getContents();
            var customShield = playerCurrentShield.get(uuid);
            Arrays.stream(inventory).filter(item -> item != null && item.getType() == Material.SHIELD).forEach(shield -> {
                if (playerCurrentShield.containsKey(uuid)) {
                    // change shield to custom
                    shieldManager.setCustomBanner(shield, customShield);
                } else {
                    // default shield
                    shieldManager.removeCustomBanner(shield);
                }
            });
            player.sendMessage(ChatColor.GREEN + "Shield updated to default.");

        }else{

            player.sendMessage(ChatColor.RED + "Your shield is already default.");
        }   
    }

    @Subcommand("update")
    @CommandPermission("shield.choose")
    public void chooseShield(Player player, String name) {
        var globalShieldList = instance.getShieldManager().getGlobalShieldList();
        var playerCurrentShield = instance.getShieldManager().getPlayerCurrentShield();
        var uuid = player.getUniqueId().toString();

        if (!globalShieldList.containsKey(name)) {
            player.sendMessage(ChatColor.RED + "Couldn't find custom shield " + name + ".");
        } else {
            playerCurrentShield.put(uuid, globalShieldList.get(name));
            player.sendMessage(ChatColor.GREEN + "Custom shield updated to " + name + ".");
        }
    }

    @Subcommand("create")
    @CommandPermission("shield.admin")
    public void createGlobalShield(Player player, String name, @Optional Integer customModelData) {
        var banner = player.getInventory().getItemInMainHand().clone();
        if (banner == null || !banner.getType().toString().endsWith("_BANNER")) {
            player.sendMessage(ChatColor.RED + "Couldn't find banner.");

        }else {

            var color = DyeColor.WHITE;

            switch (banner.getType()) {
                case BLACK_BANNER:
                    color = DyeColor.BLACK;
                    break;
                case BLUE_BANNER:
                    color = DyeColor.BLUE;
                    break;
                case BROWN_BANNER:
                    color = DyeColor.BROWN;
                    break;
                case CYAN_BANNER:
                    color = DyeColor.CYAN;
                    break;
                case GRAY_BANNER:
                    color = DyeColor.GRAY;
                    break;
                case GREEN_BANNER:
                    color = DyeColor.GREEN;
                    break;
                case LIGHT_BLUE_BANNER:
                    color = DyeColor.LIGHT_BLUE;
                    break;
                case LIGHT_GRAY_BANNER:
                    color = DyeColor.LIGHT_GRAY;
                    break;
                case LIME_BANNER:
                    color = DyeColor.LIME;
                    break;
                case MAGENTA_BANNER:
                    color = DyeColor.MAGENTA;
                    break;
                case ORANGE_BANNER:
                    color = DyeColor.ORANGE;
                    break;
                case PINK_BANNER:
                    color = DyeColor.PINK;
                    break;
                case PURPLE_BANNER:
                    color = DyeColor.PURPLE;
                    break;
                case RED_BANNER:
                    color = DyeColor.RED;
                    break;
                case YELLOW_BANNER:
                    color = DyeColor.YELLOW;
                    break;

                default:
                    break;
            }

            if(customModelData == null) customModelData = 0;

            var bannerMeta = (BannerMeta) banner.getItemMeta();

            var newBanner = new CustomShield(name, bannerMeta.getPatterns(), color, customModelData);
            instance.getShieldManager().getGlobalShieldList().put(name, newBanner);
            player.sendMessage(ChatColor.GREEN + "Global banner saved as " + name + ".");
        }
    }

}