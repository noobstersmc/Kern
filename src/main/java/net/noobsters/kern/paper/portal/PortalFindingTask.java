package net.noobsters.kern.paper.portal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;

public class PortalFindingTask extends BukkitRunnable{

    private Cuboid cuboid;
    private Iterator<Block> iter;
    private Long initial_time;
    private @Getter List<Block> b = new ArrayList<>();

    public PortalFindingTask(Location loc1, Location loc2){
        this.cuboid = new Cuboid(loc1, loc2);
        this.iter = cuboid.iterator();

    }

    @Override
    public void run() {
        if(initial_time == null)
            initial_time = System.currentTimeMillis();
            
        final var loop_start_time = System.currentTimeMillis();
        while(iter.hasNext() && (System.currentTimeMillis() - loop_start_time) < 250){
            var next = iter.next();
            if(next != null && next.getType() == Material.NETHER_PORTAL){
                b.add(next);
            }
        }

        if(iter.hasNext()){
            Bukkit.broadcastMessage("Waiting a tick");
        }else{
            Bukkit.broadcastMessage("Finished iterating. Complexity time = " + (System.currentTimeMillis() - initial_time));
            b.forEach(all ->{
                Bukkit.broadcastMessage(all.toString());
            });
            this.cancel();
        }

    }
    
}
