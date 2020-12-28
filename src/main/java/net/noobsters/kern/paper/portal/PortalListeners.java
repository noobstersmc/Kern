package net.noobsters.kern.paper.portal;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import net.noobsters.kern.paper.Kern;

@CommandAlias("portals")
public class PortalListeners extends BaseCommand implements Listener {

    private Kern instance;
    private HashMap<String, Long> portalMap = new HashMap<>();

    public PortalListeners(final Kern instance) {
        this.instance = instance;
        instance.getListenerManager().registerListener(this);
        instance.getCommandManager().registerCommand(this);
    }

    @CommandCompletion("@worlds")
    @Default
    public void findPortal(final Player player, @Name("world") final String world) {
        var w = Bukkit.getWorld(world);
        if (w != null) {
            player.sendMessage("Unloading world " + world);
            Bukkit.unloadWorld(w, false);
        } else {
            WorldCreator wc = new WorldCreator(world);
            if (world.contains("_nether"))
                wc.environment(Environment.NETHER);
            wc.createWorld();

            player.sendMessage("Created world " + world);
        }

    }

    @Subcommand("pc")
    public void onPortalCreate(final Player player) {
        createPortal(player.getLocation().getBlock());
    }

    @EventHandler
    public void exit(EntityPortalExitEvent e) {
        Bukkit.broadcastMessage("exited");

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().distance(e.getTo()) > 0 && portalMap.containsKey(e.getPlayer().getUniqueId().toString())
                && e.getTo().getBlock().getType() != Material.NETHER_PORTAL) {
            portalMap.remove(e.getPlayer().getUniqueId().toString());
        }

    }

    ArrayList<Player> p = new ArrayList<>();

    @EventHandler
    public void enterPortal(EntityPortalEnterEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        final var player = (Player) e.getEntity();

        // Bukkit.broadcastMessage("portal");
        if (p.contains(player)) {
            return;
        }
        p.add(player);

        // portalMap.put(id, System.currentTimeMillis());
        Bukkit.broadcastMessage("Coso");

        final var loc = player.getLocation();
        if (loc.getWorld().getEnvironment() == Environment.NORMAL) {
            var nether = Bukkit.getWorld(loc.getWorld().getName() + "_nether");
            if (nether != null) {
                Bukkit.broadcastMessage("step1");
                var ratioedLocation = of(nether, Math.floor(loc.getX() / 8), loc.getY(), Math.floor(loc.getZ() / 8));
                var min = ratioedLocation.clone().add(-64, 0, -64);
                min.setY(5);
                Bukkit.broadcastMessage(min.toString());
                var max = ratioedLocation.clone().add(64, 0, 64);
                max.setY(124);
                Bukkit.broadcastMessage(max.toString());
                var cuboid = new Cuboid(min, max);

                Bukkit.broadcastMessage("step2");
                var list = new ArrayList<Block>();

                var bl = cuboid.getBlocks();

                Bukkit.broadcastMessage("step3");
                for (var c : bl) {
                    var type = c.getType();
                    var l = c.getLocation();
                    if (type == Material.NETHER_PORTAL) {
                        Bukkit.broadcastMessage(l.toString());
                        Bukkit.broadcastMessage(c.getBlockData().toString());
                        Bukkit.broadcastMessage("return " + type);
                        player.teleportAsync(l);
                        return;
                    } else if (type == Material.AIR) {
                        list.add(c);
                    }
                }

                Bukkit.broadcastMessage("step4");

                var loc2 = list.get(0).getLocation();
                createPortal(loc2.getBlock());
                player.teleport(loc2);
                Bukkit.broadcastMessage("Random");

                Bukkit.broadcastMessage("step5");

            }

        }

    }

    private void createPortal(Block b) {

        Block fireBlock = null;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 5; y++) {
                if ((x == 1 || x == 2) && y >= 1 && y <= 3) {
                    if (fireBlock == null)
                        fireBlock = b.getRelative(x, y, 0);
                    continue;
                }
                b.getRelative(x, y, 0).setType(Material.OBSIDIAN);
            }

        }
        fireBlock.setType(Material.FIRE);

    }

    private static Location of(World world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    /**
     * Cancel vanilla behavior
     */

    @EventHandler
    public void cancelPortal(PlayerPortalEvent e) {
        Bukkit.broadcastMessage("Portal Event");
        e.setCancelled(true);
    }

}
