package net.noobsters.core.paper.Listeners;



import net.noobsters.core.paper.shield.ShieldPattern;
import net.noobsters.core.paper.shield.Shields;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class ShieldListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onCraft(PrepareItemCraftEvent event)  {
        if (event.getRecipe() != null) {
           String permissions = event.getView().getPlayer().getEffectivePermissions().stream()
                    .filter(permission -> permission.getPermission().startsWith("noobsters.shield") && permission.getPermission().contains("active"))
                    .collect(Collectors.toList())
                    .get(0).getPermission();

            if (!permissions.isEmpty()) {
                ItemStack result = event.getRecipe().getResult();
                if (result.getType().equals(Material.SHIELD)) {
                    ItemMeta meta = result.getItemMeta();
                    BlockStateMeta bmeta = (BlockStateMeta) meta;
                    Banner banner = (Banner) bmeta.getBlockState();

                    ShieldPattern shieldPattern = Shields.getPatterns().stream()
                            .filter(pattern -> permissions.contains(pattern.getName()))
                            .collect(Collectors.toList()).get(0);


                    banner.setBaseColor(shieldPattern.getBackground());
                    banner.setPatterns(shieldPattern.getPatterns());

                    bmeta.setBlockState(banner);
                    result.setItemMeta(bmeta);
                    event.getInventory().setResult(result);
                }
            }
            return;
        }
    }
}
