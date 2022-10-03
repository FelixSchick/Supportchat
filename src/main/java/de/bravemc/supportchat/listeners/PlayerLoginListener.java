package de.bravemc.supportchat.listeners;

import de.bravemc.supportchat.SupportChat;
import de.bravemc.supportchat.mysql.SupporterManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerLoginListener implements Listener {
    private final SupporterManager manager = new SupporterManager();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("supportchat.supporter")) {
            if (!(manager.isSupporter(player.getUniqueId()))) {
                manager.insertSupporter(player.getUniqueId(), 0, true);
            }
            final boolean loggedIn = manager.isLoggedIn(player.getUniqueId());
            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Status§8: " + (loggedIn ? "§aeingeloggt" : "§causgeloggt")));
        }
    }
}
