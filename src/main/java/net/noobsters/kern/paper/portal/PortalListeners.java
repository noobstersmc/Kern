package net.noobsters.kern.paper.portal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Subcommand("shape")
    public void findShape(final Player player) {
        var target_block = player.getTargetBlock(5);
        if (target_block != null && target_block.getType() == Material.NETHER_PORTAL) {
            var relatives = List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH,
                    BlockFace.SOUTH);
            var block_matrix = new Block[2][3];

            /*
             * $$$$ $00$ $00$ $00$ $$$$
             */

            var np = Material.NETHER_PORTAL;

            var up = target_block.getRelative(BlockFace.UP);
            var down = target_block.getRelative(BlockFace.DOWN);
            var east = target_block.getRelative(BlockFace.EAST);
            var west = target_block.getRelative(BlockFace.WEST);
            var north = target_block.getRelative(BlockFace.NORTH);
            var south = target_block.getRelative(BlockFace.SOUTH);

            if (isPortalBlock(north) || isPortalBlock(south)) {

                int count = 0;
                //TODO: CONTINUE HERE, LOCATE THE SHAPE AS A 2D matrix instead of collection of blocks.
                var relatives_list = List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH);
                var col = new ArrayList<Block>();
                for (var rel : relatives_list) {
                    count++;
                    var rel_block = target_block.getRelative(rel);
                    if (rel_block.getType() == Material.NETHER_PORTAL) {
                        col.add(rel_block);
                        for (var rel_loop : relatives_list) {

                            count++;
                            var rel_block_loop = rel_block.getRelative(rel_loop);
                            if (rel_block_loop.getType() == Material.NETHER_PORTAL)
                                col.add(rel_block_loop);
                        }
                    }
                }

                Bukkit.broadcastMessage("North-South portal type");
                Bukkit.broadcastMessage("North is " + north.getType() + ", South is " + south.getType());
                Bukkit.broadcastMessage("Up is " + up.getType() + ", Down is " + down.getType());

                col.forEach(all -> {
                    Bukkit.broadcastMessage(all.toString());
                });
                Bukkit.broadcastMessage(count + " checks");
                // Find Lower corner

            } else if (isPortalBlock(east) || isPortalBlock(west)) {

                var relatives_list = List.of(BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.WEST);

                Bukkit.broadcastMessage("East-west portal type");

                Bukkit.broadcastMessage("East is " + east.getType() + ", West is " + west.getType());

                Bukkit.broadcastMessage("Up is " + up.getType() + ", Down is " + down.getType());
            } else {
                Bukkit.broadcastMessage("Not a known portal type?");
            }

        }
    }

    private boolean isPortalBlock(Block b) {
        return b.getType() == Material.NETHER_PORTAL;
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

        // new PlayerPortalEvent(entity, from, to, TeleportCause.NETHER_PORTAL, 128,
        // true, 16);

        var min = ratioed_target.clone().add(-64, 0, -64);
        min.setY(30);
        var max = ratioed_target.clone().add(64, 0, 64);
        max.setY(100);

        double last_distance = Integer.MAX_VALUE;
        Location closest_loc = null;

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY()
                    && y <= Math.min(max.getBlockY(), target.getMaxHeight()); y++) {
                for (int z = max.getBlockZ(); z >= min.getBlockZ(); z--) {
                    var block = target.getBlockAt(x, y, z);
                    if (block.getType() == Material.NETHER_PORTAL) {
                        entity.teleport(block.getLocation());
                        return true;
                    } else if (block.getType() != Material.AIR
                            && block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                        var loc = block.getLocation();
                        var d = loc.distance(ratioed_target);
                        if (d < last_distance) {
                            last_distance = d;
                            closest_loc = loc;
                        }
                    }
                }
            }
        }

        var portal = Portal.createPortal(closest_loc != null ? closest_loc : ratioed_target);
        entity.teleport(portal.getTeleportLocation());

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
