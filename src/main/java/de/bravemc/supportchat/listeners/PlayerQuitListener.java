package de.bravemc.supportchat.listeners;

import de.bravemc.supportchat.mysql.TicketManager;
import de.bravemc.supportchat.utils.TicketStatus;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        TicketManager ticketManager = new TicketManager();
        if (ticketManager.isTicketOpen(event.getPlayer().getUniqueId())) {
            ticketManager.updateStatus(ticketManager.getTicketID(event.getPlayer().getUniqueId(), TicketStatus.OPEN), TicketStatus.CLOSED);
            ticketManager.updateClosedDate(ticketManager.getTicketID(event.getPlayer().getUniqueId(), TicketStatus.OPEN));
        }
    }
}
