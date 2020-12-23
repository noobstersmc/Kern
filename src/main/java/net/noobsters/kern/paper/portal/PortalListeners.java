package net.noobsters.kern.paper.portal;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.noobsters.kern.paper.Kern;

public class PortalListeners implements Listener {

    private Kern instance;

    public PortalListeners(final Kern instance) {
        instance.getListenerManager().registerListener(this);
        this.instance = instance;

    }

    @EventHandler
    public void onCoso(EntityPortalEnterEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            var player = (Player) e.getEntity();
            var location = e.getLocation();
            var block = location.getBlock();
            var world = location.getWorld();
            if (world.getEnvironment() == Environment.NETHER && block.getType() == Material.NETHER_PORTAL) {
                var overworld = Bukkit.getWorld(world.getName().replace("_nether", ""));
                if (overworld != null) {
                    var portal = overworld.getBlockAt(new Location(overworld, location.getX() * 2, location.getY() * 2,
                            location.getZ() * 2, location.getYaw(), location.getPitch()));

                    var portal_blocks = locateNetherPortal(portal.getLocation(), 32);

                    for (var b : portal_blocks) {
                        for (var face : BlockFace.values()) {
                            var air = b.getRelative(face);
                            if (air.getType() == Material.AIR) {
                                player.teleport(air.getLocation());
                                return;
                            }
                        }
                    }
                }
            } else if (world.getEnvironment() == Environment.NORMAL && block.getType() == Material.NETHER_PORTAL) {
                var nether = Bukkit.getWorld(world.getName() + "_nether");
                if (nether != null) {
                    var portal = nether.getBlockAt(new Location(nether, location.getX() / 2, location.getY() / 2,
                            location.getZ() / 2, location.getYaw(), location.getPitch()));

                    var portal_blocks = locateNetherPortal(portal.getLocation(), 32);

                    for (var b : portal_blocks) {
                        for (var face : BlockFace.values()) {
                            var air = b.getRelative(face);
                            if (air.getType() == Material.AIR) {
                                player.teleport(air.getLocation());
                                return;
                            }
                        }
                    }
                }
            }

        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getMessage().startsWith("!") && e.getMessage().toLowerCase().contains("portal")) {
            Bukkit.getScheduler().runTask(instance, () -> locateNetherPortal(e.getPlayer().getLocation(), 32));
        }
    }

    public List<Block> locateNetherPortal(Location loc, int radius) {
        Bukkit.broadcastMessage("Locating portal");
        final long start_time = System.currentTimeMillis();
        var x = loc.getBlockX();
        var y = loc.getY();
        var z = loc.getZ();

        // Lowest point
        var x1 = x - radius;
        var y1 = y - radius;
        var z1 = z - radius;

        var x2 = x + radius;
        var y2 = y + radius;
        var z2 = z + radius;

        var cuboid = new Cuboid(new Location(loc.getWorld(), x1, y1, z1), new Location(loc.getWorld(), x2, y2, z2))
                .getBlocks();

        Bukkit.broadcastMessage((System.currentTimeMillis() - start_time) + "ms complexity");

        return cuboid.parallelStream().filter(b -> b.getType() == Material.NETHER_PORTAL).collect(Collectors.toList());

    }
}
