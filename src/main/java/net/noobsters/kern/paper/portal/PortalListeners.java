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
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;
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
import lombok.AllArgsConstructor;
import lombok.Data;
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

    @Subcommand("clear")
    public void portalsListClear(CommandSender sender) {
        p.clear();
    }

    @Subcommand("pc")
    @CommandCompletion("@worlds <Integer>")
    public void onPortalCreate(final Player player, World world, int ratio) {
        var initial_time = System.currentTimeMillis();
        travelDimensions(player, player.getLocation(), world, ratio);
        Bukkit.broadcastMessage("Took " + (System.currentTimeMillis() - initial_time) + "ms to teleport.");
    }

    /**
     * NETHER ALGO STARTS
     */

    // Portal Object
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Portal {
        Block[][] portal_blocks;
        Block[][][] frame;
        Block[][][] air_blocks;

        public static Portal createPortal(Location loc) {
            final var b = loc.getBlock();
            final var portalBlocks = new Block[4][5];
            final var frameBlocks = new Block[4][5][3];
            final var airBlocks = new Block[4][5][3];

            for (int x = 0; x < 4; x++) {
                for (int y = 0; y < 5; y++) {
                    for (int z = 0; z < 3; z++) {
                        if (z == 1) {
                            if ((x == 1 || x == 2) && y >= 1 && y <= 3) {
                                var relative = b.getRelative(x, y, z);
                                relative.setType(Material.AIR);
                                portalBlocks[x][y] = relative;
                                continue;
                            }
                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.OBSIDIAN);
                            frameBlocks[x][y][z] = relative;
                        } else if (y == 0) {

                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.OBSIDIAN);
                            frameBlocks[x][y][z] = relative;
                        } else {
                            var relative = b.getRelative(x, y, z);
                            relative.setType(Material.AIR);
                            airBlocks[x][y][z] = relative;
                        }

                    }
                }

            }

            portalBlocks[1][1].setType(Material.FIRE);

            return of(portalBlocks, frameBlocks, airBlocks);
        }

        public Location getTeleportLocation() {
            var left = portal_blocks[1][2];
            var right = portal_blocks[2][2];
            return new Location(left.getWorld(), (left.getX() + right.getX()) / 2, left.getY(),
                    (left.getZ() + right.getZ()) / 2);
        }
    }

    // Travel function
    public boolean travelDimensions(Entity entity, Location from, World target, int ratio) {
        final var ratioed_target = new Location(target, from.getX() * ratio, from.getY(), from.getZ() * ratio);

        var min = ratioed_target.clone().add(-64, 0, -64);
        min.setY(30);
        var max = ratioed_target.clone().add(64, 0, 64);
        max.setY(100);

        var cuboid_matrix = new ArrayList<Block>();
        var optimal = new ArrayList<Block>();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY()
                    && y <= Math.min(max.getBlockY(), target.getMaxHeight()); y++) {
                for (int z = max.getBlockZ(); z >= min.getBlockZ(); z--) {
                    var block = target.getBlockAt(x, y, z);
                    if (block.getType() == Material.NETHER_PORTAL) {
                        entity.teleport(block.getLocation());
                        cuboid_matrix.clear();
                        cuboid_matrix = null;

                        Bukkit.broadcastMessage("Found existing portal");
                        return true;
                    }
                    if (block.getType() != Material.AIR && block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        optimal.add(block);
                    }
                    cuboid_matrix.add(block);
                }
            }
        }
        // Find Closest one
        double last_distance = Integer.MAX_VALUE;
        Location closest_loc = null;
        // Loop and replace
        for (var optimal_loc : optimal) {
            var d = optimal_loc.getLocation().distance(ratioed_target);
            if (d < last_distance) {
                last_distance = d;
                closest_loc = optimal_loc.getLocation();
            }
        }
        // Create portal return true
        if (closest_loc != null) {
            Bukkit.broadcastMessage("Creating new portal");
            var portal = Portal.createPortal(closest_loc);
            entity.teleport(portal.getTeleportLocation());
            return true;
        } else {
            Bukkit.broadcastMessage("Creating new portal at 1:1");
            var portal = Portal.createPortal(ratioed_target);
            entity.teleport(portal.getTeleportLocation());
        }

        return false;
    }

    public void travelDimensions(Entity entity, Location from, World target) {
        travelDimensions(entity, from, target, 1);
    }

    /**
     * NETHER ALGO ENDS
     */

    HashMap<Player, Integer> p = new HashMap<>();
    HashMap<Player, Location> portalLocation = new HashMap<>();
    HashMap<Cuboid, Location> locations = new HashMap<>();

    @EventHandler
    public void enterPortal(EntityPortalEnterEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        final var player = (Player) e.getEntity();
        final var cd = p.get(player);
        portalLocation.put(player, player.getLocation());
        if (cd != null) {
            return;
        }

        Bukkit.broadcastMessage("Attempting.");
        travel(player, 50);

    }

    public void travel(Player player, int cd) {
        if (!p.containsKey(player)) {
            p.put(player, cd);
        }
        Bukkit.getScheduler().runTaskLater(instance, () -> {
            var block = player.getLocation();
            var distance = block.distance(portalLocation.get(player));
            if (distance == 0.0) {
                if (cd > 0) {
                    travel(player, (cd - 1));
                } else {
                    var world = player.getWorld();
                    if (world.getEnvironment() == Environment.NORMAL) {
                        var nether = Bukkit.getWorld(world.getName() + "_nether");
                        if (nether != null) {
                            travelDimensions(player, player.getLocation(), nether);
                        } else {
                            player.sendMessage("No nether world available");
                        }
                        Bukkit.getScheduler().runTaskLater(instance, () -> {
                            p.remove(player);
                        }, 40);
                    } else if (world.getEnvironment() == Environment.NETHER) {
                        var overworld = Bukkit.getWorld(world.getName().replace("_nether", ""));
                        if (overworld != null) {
                            travelDimensions(player, player.getLocation(), overworld);
                        } else {
                            player.sendMessage("No overworld available");
                        }
                        Bukkit.getScheduler().runTaskLater(instance, () -> {
                            p.remove(player);
                        }, 40);
                    }
                }

            } else {
                Bukkit.broadcastMessage("Cancelled");
                p.remove(player);
            }
        }, 1L);
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e) {
        e.setCancelled(true);

    }

    /**
     * Cancel vanilla behavior
     */

    @EventHandler
    public void cancelPortal(PlayerPortalEvent e) {
        e.setCancelled(true);
    }

}
