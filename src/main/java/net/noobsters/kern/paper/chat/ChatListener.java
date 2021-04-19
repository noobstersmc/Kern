package net.noobsters.kern.paper.chat;

import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.chat.Chat;
import net.noobsters.kern.paper.Kern;

@RequiredArgsConstructor
public class ChatListener implements Listener {
    private Kern instance;
    private static String format = "&7{prefix}{name}{suffix}:&f {message}";
    private HashMap<UUID, Long> chatCoolDown = new HashMap<>();

    public ChatListener(final Kern instance) {
        this.instance = instance;

    }

    /*
     * Chat Channels
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void chatChannel(AsyncPlayerChatEvent e) {
        var player = e.getPlayer();
        var msg = e.getMessage();

        // Edge cases start

        if (msg.startsWith("!")) {
            e.setMessage(msg.replaceFirst("!", ""));
            if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("uhc.chat.spec")) {
                sendSpecMessage(player, msg);
                e.setCancelled(true);
            }
            return;
        } else if (msg.startsWith("@") && player.hasPermission("uhc.chat.spec")) {
            e.setMessage(msg.replaceFirst("@", ""));
            sendSpecMessage(player, msg);
            e.setCancelled(true);
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR && !player.hasPermission("uhc.chat.spec")) {
            sendSpecMessage(player, msg);
            e.setCancelled(true);
            return;
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void checkForMute(AsyncPlayerChatEvent e) {
        if (instance.getChatManager().isGlobalmute() && !e.getPlayer().hasPermission("globalmute.talk")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "Globalmute is Enabled.");
        }
    }

    void sendSpecMessage(Player sender, String message) {
        var msg = ChatColor.GRAY + "[SPEC] "
                + colorize(replacedFormat(format.replace("{name}", sender.getName()), sender)).replace("{message}",
                        message);

        Bukkit.getOnlinePlayers().forEach(all -> {
            if (!instance.getChatManager().isSpecChat() || all.getGameMode() == GameMode.SPECTATOR
                    || all.hasPermission("uhc.chat.spec")) {
                all.sendMessage(msg);
            }
        });
        Bukkit.getLogger().info(msg);

    }

    /*
     * Vault Handlers
     */
    @EventHandler
    public void onServiceChange(ServiceRegisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            instance.getChatManager().refreshVault();
        }
    }

    @EventHandler
    public void onServiceChange(ServiceUnregisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            instance.getChatManager().refreshVault();
        }
    }

    /*
     * Chat formatters.
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void coolDown(AsyncPlayerChatEvent e) {

        if (!chatCoolDown.containsKey(e.getPlayer().getUniqueId())
                || (chatCoolDown.get(e.getPlayer().getUniqueId()) - System.currentTimeMillis()) <= 0) {
            chatCoolDown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + 2_000);
        } else if (chatCoolDown.containsKey(e.getPlayer().getUniqueId()) && !e.getPlayer().hasPermission("chat.spam")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + "You can chat every 2 seconds.");
        }

    }

    @EventHandler(priority = EventPriority.LOW)
    public void setFormat(AsyncPlayerChatEvent e) {
        e.setFormat(getFormatted());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChatHigh(AsyncPlayerChatEvent e) {
        String format = e.getFormat();
        var vaultChat = instance.getChatManager().getVaultChat();
        if (vaultChat != null && format.contains("{prefix}")) {
            format = format.replace("{prefix}", vaultChat.getPlayerPrefix(e.getPlayer()));
        }
        if (vaultChat != null && format.contains("{suffix}")) {
            format = format.replace("{suffix}", vaultChat.getPlayerSuffix(e.getPlayer()));
        }
        format = format.replace("{name}", e.getPlayer().getName());

        e.setFormat(colorize(format));
    }

    private String replacedFormat(String format, Player p) {
        var vaultChat = instance.getChatManager().getVaultChat();
        if (vaultChat != null && format.contains("{prefix}")) {
            format = format.replace("{prefix}", vaultChat.getPlayerPrefix(p));
        }
        if (vaultChat != null && format.contains("{suffix}")) {
            format = format.replace("{suffix}", vaultChat.getPlayerSuffix(p));
        }
        return format.replace("{name}", p.getName());
    }

    private String getFormatted() {
        return format.replace("{message}", "%2$s").replace("{name}", "%1$s");
    }

    private String colorize(String message) {
        return translateHexColorCodes("&#", "", message);
    }

    private String translateHexColorCodes(String startTag, String endTag, String message) {
        final var COLOR_CHAR = '\u00A7';
        final Pattern hexPattern = Pattern.compile(startTag + "([A-Fa-f0-9]{6})" + endTag);
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer,
                    COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR
                            + group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR
                            + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }
}