package net.noobsters.kern.paper.chat;

import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.chat.Chat;
import net.noobsters.kern.paper.Kern;

public class ChatManager {
    private Kern instance;
    private @Getter Chat vaultChat;
    private @Getter @Setter boolean specChat = true;
    private @Getter @Setter boolean globalmute = false;
    
    public ChatManager(Kern instance) {
        this.instance = instance;
        refreshVault();

    }

    public void refreshVault() {
        Chat vaultChat = instance.getServer().getServicesManager().load(Chat.class);
        if (vaultChat != this.vaultChat) {
            instance.getLogger().info(
                    "New Vault Chat implementation registered: " + (vaultChat == null ? "null" : vaultChat.getName()));
        }
        this.vaultChat = vaultChat;
    }

}