package net.noobsters.kern.paper.portal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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

    @Subcommand("list")
    public void portalsList(CommandSender sender) {
        if (p.isEmpty()) {
            sender.sendMessage("No players in list");
        } else {

            p.forEach(all -> {
                sender.sendMessage(all);
            });
        }
    }

    @Subcommand("clear")
    public void portalsListClear(CommandSender sender) {
        p.clear();
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

    /**
     * NETHER ALGO STARTS
     */

    @Data
    @AllArgsConstructor(staticName = "of")
    public static class Portal {
        ArrayList<Block> frame;
        ArrayList<Block> portal_blocks;
        
        public static Portal createPortal(){

            return of(null, null);

        }
    }
    
    /**
     * NETHER ALGO ENDS
     */

    ArrayList<String> p = new ArrayList<>();
    HashMap<Cuboid, Location> locations = new HashMap<>();

    @EventHandler
    public void enterPortal(EntityPortalEnterEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        final var player = (Player) e.getEntity();

        if (p.contains(player.getUniqueId().toString())) {
            return;
        }
        var oneByThreematrix = new int[][][] {};

        p.add(player.getUniqueId().toString());

        final var loc = player.getLocation();
        if (loc.getWorld().getEnvironment() == Environment.NORMAL) {
            var nether = Bukkit.getWorld(loc.getWorld().getName() + "_nether");
            if (nether != null) {
                var ratioedLocation = of(nether, Math.floor(loc.getX()), loc.getY(), Math.floor(loc.getZ()));
                var min = ratioedLocation.clone().add(-64, 0, -64);
                min.setY(100);
                var max = ratioedLocation.clone().add(64, 0, 64);
                max.setY(30);
                var cuboid = new Cuboid(min, max);
                var list = new ArrayList<Block>();

                var bl = cuboid.getBlocks();
                Location teleport = null;

                for (var c : bl) {
                    var type = c.getType();
                    var l = c.getLocation();
                    if (type == Material.NETHER_PORTAL) {
                        Bukkit.broadcastMessage("portal_found");
                        oneByThreematrix[0][1][2] = 1;
                        teleport = l;
                        break;
                    } else if (type == Material.AIR) {
                        list.add(c);
                    }
                }

                if (teleport != null) {
                    final var ftp = teleport;

                    player.teleport(ftp);
                    p.remove(player.getUniqueId().toString());
                    Bukkit.broadcastMessage("found_teleport\n" + ftp.toString());

                } else {
                    // make ir higher
                    teleport = list.get(0).getLocation();
                    list.stream().max(Comparator.comparingInt(Block::getY)).ifPresent(a -> {

                        final var ftp = createPortal(a);

                        player.teleport(ftp);
                        p.remove(player.getUniqueId().toString());
                        Bukkit.broadcastMessage("random_teleport\n" + ftp.toString());
                    });

                    // TODO: Create portal as an object with the structure positions easy to access.
                }

            }

        } else {
            p.remove(player.getUniqueId().toString());
        }

    }

    private Location createPortal(Block b) {

        Block fireBlock = null;

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 5; y++) {
                for (int z = 0; z < 3; z++) {
                    if (z == 1) {
                        if ((x == 1 || x == 2) && y >= 1 && y <= 3) {
                            var relative = b.getRelative(x, y, z);
                            if (fireBlock == null)
                                fireBlock = relative;
                            relative.setType(Material.AIR);
                            continue;
                        }
                        b.getRelative(x, y, z).setType(Material.OBSIDIAN);
                    } else if (y == 0) {

                        b.getRelative(x, y, z).setType(Material.OBSIDIAN);
                    } else {
                        b.getRelative(x, y, z).setType(Material.AIR);

                    }

                }
            }

        }
        fireBlock.setType(Material.FIRE);
        return fireBlock.getLocation();
    }

    @EventHandler
    public void onLeafDecay(LeavesDecayEvent e) {
        e.setCancelled(true);

    }

    private static Location of(World world, double x, double y, double z) {
        return new Location(world, x, y, z);
    }

    /**
     * Cancel vanilla behavior
     */

    @EventHandler
    public void cancelPortal(PlayerPortalEvent e) {
        e.setCancelled(true);
    }

}
