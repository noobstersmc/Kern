package net.noobsters.kern.paper.punishments.exceptions;

import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;

public class ExceptionHandlers {

    /**
     * Utility function to reduce lines of code and handle all possible exception of
     * futures.
     * 
     * @param result    Provided by
     *                  {@link CompletableFuture#handle(java.util.function.BiFunction)}
     * @param exception Provided{@link CompletableFuture#handle(java.util.function.BiFunction)}
     * @return True or false if anything went wrong.
     */
    public static Boolean handleException(Boolean result, Throwable exception) {
        if (exception != null) {
            broadcastException(exception);
            return false;
        }
        return result;
    }

    public static Boolean handleVoid(Void v, Throwable exception) {
        if (exception != null) {
            broadcastException(exception);
            return false;
        }
        return true;

    }

    public static Boolean handleVoidWithSender(Void v, Throwable exception, CommandSender sender) {
        if (exception != null) {
            exception.printStackTrace();
            sender.sendMessage(ChatColor.RED + exception.getCause().toString());
            return false;
        }
        return true;

    }

    private static void broadcastException(Throwable exception) {
        exception.printStackTrace();
        Bukkit.broadcast(ChatColor.RED + exception.getCause().toString(), "admin.debug");
    }

}
