package de.bravemc.supportchat.listeners;

import de.bravemc.supportchat.Supportchat;
import de.bravemc.supportchat.mysql.SupporterManager;
import de.bravemc.supportchat.mysql.TicketManager;
import de.bravemc.supportchat.utils.TicketStatus;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerChatListener implements Listener {
    @EventHandler
    public void onChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        TicketManager ticketManager = new TicketManager();
        SupporterManager supporterManager = new SupporterManager();
        if (!(supporterManager.isSupporter(player.getUniqueId().toString()))) {
            if (ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.OPEN) != 0) {
                if (!(event.getMessage().startsWith("/"))) {
                    event.setCancelled(true);
                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7" + player.getDisplayName() + "§8: §7" + event.getMessage()));

                    for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.OPEN))) {
                        if (uuids != null) {
                            if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                all.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + player.getName() + " §8» " + player.getDisplayName() + "§8: §7" + event.getMessage()));
                            }
                        }
                    }
                }
            }
        }
    }
}
