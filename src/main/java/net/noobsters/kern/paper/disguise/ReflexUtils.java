package net.noobsters.kern.paper.disguise;

import java.lang.reflect.Constructor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReflexUtils {
    public static final String OBC_PACKAGE = "org.bukkit.craftbukkit";
    public static final String NMS_PACKAGE = "net.minecraft.server";

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName()
            .substring(OBC_PACKAGE.length() + 1);

    public static String nmsClassName(String className) {
        return NMS_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static String obcClassName(String className) {
        return OBC_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static void setGameprofileName(Player player, String name) {
        try {
            var craftPlayerClass = Class.forName(ReflexUtils.obcClassName("entity.CraftPlayer"));
            var getProfileMethod = craftPlayerClass.getDeclaredMethod("getProfile");
            var profile = getProfileMethod.invoke(player);

            var playerProfile = profile;
            var nameField = playerProfile.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(playerProfile, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object getEntityPlayer(Player player) throws Exception {
        var craftPlayer = Class.forName(obcClassName("entity.CraftPlayer"));
        var entityPlayer = craftPlayer.getDeclaredMethod("getHandle");

        return entityPlayer.invoke(player);
    }

    public static Constructor<?> getPacket(String className, Class<?>... parameterTypes) throws Exception {
        var cons = Class.forName(nmsClassName(className)).getDeclaredConstructor(parameterTypes);
        return cons;
    }

    public static Object getPacketPlayOutPlayerInfo(ReflexPlayerInfoAction action, Player player) throws Exception {
        var entityPlayer = getEntityPlayer(player);
        

        return null;
    }

    public enum ReflexPlayerInfoAction {
        UPDATE_GAME_MODE(),

        UPDATE_LATENCY(),

        UPDATE_DISPLAY_NAME(),

        REMOVE_PLAYER();
    }

}
