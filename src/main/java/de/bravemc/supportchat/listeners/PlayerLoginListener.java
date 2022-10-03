package de.bravemc.supportchat.listeners;

import de.bravemc.supportchat.SupportChat;
import de.bravemc.supportchat.mysql.SupporterManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class PlayerLoginListener implements Listener {
    private final SupporterManager manager;

    public PlayerLoginListener() {
        manager = new SupporterManager();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(final PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final UUID uuid = player.getUniqueId();
        if (player.hasPermission("supportchat.supporter")) {
            if (!(manager.isSupporter(uuid))) {
                manager.insertSupporter(uuid, 0, true);
            }
            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Status§8: " + (manager.isLoggedIn(uuid) ? "§aeingeloggt" : "§causgeloggt")));
        } else {
            if (manager.isSupporter(uuid)) {
                manager.deleteSupporter(uuid);
            }
        }
    }
}
