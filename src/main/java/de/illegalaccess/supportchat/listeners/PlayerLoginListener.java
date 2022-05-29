package de.illegalaccess.supportchat.listeners;

import de.illegalaccess.supportchat.mysql.SupporterManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PlayerLoginListener implements Listener {
    private final SupporterManager manager = new SupporterManager();

    @EventHandler (priority = EventPriority.LOWEST)
    public void onLogin(PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("supportchat.supporter")) {
            if(!(manager.isSupporter(player.getUniqueId().toString()))) {
                manager.insertSupporter(player.getUniqueId().toString(), 0,0, true);
            }
        }
    }
}
