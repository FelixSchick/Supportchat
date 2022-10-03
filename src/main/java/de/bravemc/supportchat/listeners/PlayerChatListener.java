package de.bravemc.supportchat.listeners;

import de.bravemc.supportchat.SupportChat;
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
    private final TicketManager ticketManager;
    private final SupporterManager supporterManager;

    public PlayerChatListener(){
        ticketManager = new TicketManager();
        supporterManager = new SupporterManager();
    }

    @EventHandler
    public void onChat(final ChatEvent event) {
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        if (!(supporterManager.isSupporter(player.getUniqueId()))) {
            if (ticketManager.getTicketID(player.getUniqueId(), TicketStatus.OPEN) != 0) {
                if (!(event.getMessage().startsWith("/"))) {
                    event.setCancelled(true);
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7" + player.getDisplayName() + "§8: §7" + event.getMessage()));
                    for (final UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId(), TicketStatus.OPEN))) {
                        if (uuids != null) {
                            if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                final ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                all.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§e" + player.getName() + " §8» " + player.getDisplayName() + "§8: §7" + event.getMessage()));
                            }
                        }
                    }
                }
            }
        }
    }
}
