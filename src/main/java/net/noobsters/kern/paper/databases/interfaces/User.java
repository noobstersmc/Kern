package net.noobsters.kern.paper.databases.interfaces;

import java.util.UUID;

/**
 * Created with the purpose of generalizing code instead of making it specific
 * to Bukkit, Velocity, or bungee. All implementations must return a UUID and
 * that's all we care about to keep track of something.
 */
public interface User {
    public UUID getUniquedId();

    default String stringifiedUUID() {
        return getUniquedId().toString();
    }

}
