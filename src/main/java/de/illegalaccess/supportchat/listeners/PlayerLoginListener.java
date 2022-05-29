package de.illegalaccess.supportchat.listeners;

import de.illegalaccess.supportchat.mysql.SupporterManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerLoginListener implements Listener {
    @EventHandler
    public void onLogin(PostLoginEvent event) {
        SupporterManager manager = new SupporterManager();
        ProxiedPlayer player = event.getPlayer();
        if (player.hasPermission("supportchat.supporter")) {
            if(!(manager.isSupporter(player.getUniqueId().toString()))) {
                manager.insertSupporter(player.getUniqueId().toString(), 0,0, true);
            }
        }
    }
}
