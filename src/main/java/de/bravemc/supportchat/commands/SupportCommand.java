package de.bravemc.supportchat.commands;

import com.google.common.collect.Lists;
import de.bravemc.supportchat.SupportChat;
import de.bravemc.supportchat.mysql.SupporterManager;
import de.bravemc.supportchat.mysql.TicketManager;
import de.bravemc.supportchat.utils.TicketStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class SupportCommand extends Command implements TabExecutor {

    public SupportCommand() {
        super("support");
    }

    private static TextComponent generateRatingStar(String text, String value, String value1) {
        TextComponent star1 = new TextComponent(text);
        star1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(value)));
        star1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, value1));
        return star1;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        SupporterManager supporterManager = new SupporterManager();
        TicketManager ticketManager = new TicketManager();
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length == 0) {
                if (supporterManager.isSupporter(player.getUniqueId())) {
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDu bist bereits ein Supporter! Du darfst kein Ticket öffnen :P"));
                    return;
                }
                if (ticketManager.getTicketID(player.getUniqueId(), TicketStatus.OPEN) == 0) {

                    TextComponent main = new TextComponent(SupportChat.getInstance().getPrefix() + "§7Der Spieler §e" + player.getDisplayName() + " §7benötigt hilfe.");
                    main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§7Klicke hier um das Ticket zu öffnen!")));
                    main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support join " + player.getName()));

                    for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                        if (all.hasPermission("supportchat.notify")) {
                            if (supporterManager.isLoggedIn(all.getUniqueId())) {
                                all.sendMessage(main);
                            }
                        }
                    }
                    ticketManager.insertTicket(player.getUniqueId(), "", TicketStatus.OPEN);
                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast ein neues Ticket erstellt."));
                }
            } else if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("join")) {
                    if (player.hasPermission("supportchat.join")) {
                        if (args.length == 2) {
                            if (supporterManager.isLoggedIn(player.getUniqueId())) {
                                SupportChat.getInstance().getUuidFetcher().fetchUUIDAsync(args[1], uuid -> {
                                    if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                        if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN)).contains(player.getUniqueId()))) {
                                            ticketManager.addSups(ticketManager.getTicketID(uuid, TicketStatus.OPEN), player.getUniqueId());
                                            supporterManager.addTicketCounter(player.getUniqueId());
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist dem Suppportchat beigetreten."));
                                            for (UUID sup : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN))) {
                                                ProxiedPlayer supPlayer = ProxyServer.getInstance().getPlayer(sup);
                                                supPlayer.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§e" + args[1] + " §8» §e" + player.getDisplayName() + "§8: §7Herzlich willkommen im offiziellen Support von BraveMC.de wie kann ich dir weiterhelfen?"));
                                            }
                                            if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                                                final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
                                                target.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + player.getDisplayName() + "§8: §7Herzlich willkommen im offiziellen Support von BraveMC.de wie kann ich dir weiterhelfen?"));
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist bereits dem Ticket beigetreten."));
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dieser Spieler ist in keinem Supportchat."));
                                    }
                                });
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist nicht eingeloggt."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Benutze §e/support join <Spieler>"));
                        }
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDu hast keine Rechte um diesen Befehl zu nutzen."));
                    }
                } else if (args[0].equalsIgnoreCase("send")) {
                    if (player.hasPermission("supportchat.send")) {
                        if (args.length >= 2) {
                            if (supporterManager.isLoggedIn(player.getUniqueId())) {
                                SupportChat.getInstance().getUuidFetcher().fetchUUIDAsync(args[1], uuid -> {
                                    if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                        StringBuilder message = new StringBuilder();
                                        for (int i = 2; i < args.length; i++) {
                                            message.append(args[i]).append(" ");
                                        }
                                        if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                                            final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
                                            target.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7" + player.getDisplayName() + "§8: §7" + message));

                                            for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN))) {
                                                if (uuids != null) {
                                                    if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                                        ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                                        all.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§e" + args[1] + " §8» " + player.getDisplayName() + "§8: §7" + message));
                                                    }
                                                }
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + " §7ist nicht online."));
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + " §7hat kein Ticket."));
                                    }

                                });
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist nicht eingeloggt."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Benutze §e/support send <Spieler> <Nachricht>"));
                        }
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDu hast keine Rechte um diesen Befehl zu nutzen."));
                    }
                } else if (args[0].equalsIgnoreCase("close")) {
                    if (args.length == 2) {
                        if (supporterManager.isLoggedIn(player.getUniqueId()) && supporterManager.isSupporter(player.getUniqueId())) {
                            SupportChat.getInstance().getUuidFetcher().fetchUUIDAsync(args[1], uuid -> {
                                if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                    ticketManager.updateClosedDate(ticketManager.getTicketID(uuid, TicketStatus.OPEN));
                                    ticketManager.updateStatus(ticketManager.getTicketID(uuid, TicketStatus.OPEN), TicketStatus.CLOSED);
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + args[1] + " §7geschlossen."));
                                    if (ProxyServer.getInstance().getPlayer(uuid) != null) {
                                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(uuid);
                                        target.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dein Ticket wurde von §e" + player.getDisplayName() + " §7geschlossen."));
                                        TextComponent main = new TextComponent(SupportChat.getInstance().getPrefix() + "Du kannst nun den Support bewerten(mit einem Klick auf die Sterne):\n");

                                        TextComponent star1 = generateRatingStar("§8[§6✩§8] ", "§61 §7Stern Bewertung", "/support rate 1");

                                        TextComponent star2 = generateRatingStar("§8[§6✫✫§8] ", "§62 §7Sterne Bewertung", "/support rate 2");

                                        TextComponent star3 = generateRatingStar("§8[§6✫✫✫§8] ", "§63 §7Sterne Bewertung", "/support rate 3");

                                        TextComponent star4 = generateRatingStar("§8[§6✫✫✫✫§8] ", "§64 §7Sterne Bewertung", "/support rate 4");

                                        TextComponent star5 = generateRatingStar("§8[§6✫✫✫✫✫§8]", "§65 §7Sterne Bewertung", "/support rate 5");

                                        generatorRating(target, main, star1, star2, star3, star4, star5);
                                    }
                                    if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED)).toArray().length == 0)) {
                                        for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED))) {
                                            if (uuids != null) {
                                                if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                                    ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                                    supporterManager.updateLastActivity(uuids);
                                                    all.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§8[§e" + args[1] + "§8] §7Das Ticket wurde von §e" + player.getName() + " §7geschlossen."));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dieser Spieler ist in keinem Supportchat."));
                                }
                            });
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist entweder kein Teammitglied oder du bist §causgeloggt."));
                        }
                    } else {
                        final UUID uuid = player.getUniqueId();
                        if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                            ticketManager.updateClosedDate(ticketManager.getTicketID(uuid, TicketStatus.OPEN));
                            ticketManager.updateStatus(ticketManager.getTicketID(uuid, TicketStatus.OPEN), TicketStatus.CLOSED);

                            if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED)).toArray().length == 0)) {
                                for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED))) {
                                    if (uuids != null) {
                                        if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                            final ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                            supporterManager.updateLastActivity(uuids);
                                            all.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§8[§e" + player.getName() + "§8] §7Das Ticket wurde von §e" + player.getName() + " §7geschlossen."));
                                        }
                                    }
                                }
                            }

                            TextComponent main = new TextComponent(SupportChat.getInstance().getPrefix() + "§7Du hast dein Ticket §cgeschlossen. \n §7Du kannst nun den Support bewerten (mit einem Klick auf die Sterne):\n");

                            TextComponent star1 = generateRatingStar("§8[§6✩§8] ", "§61 §7Stern Bewertung", "/support rate 1");

                            TextComponent star2 = generateRatingStar("§8[§6✫✫§8] ", "§62 §7Sterne Bewertung", "/support rate 2");

                            TextComponent star3 = generateRatingStar("§8[§6✫✫✫§8] ", "§63 §7Sterne Bewertung", "/support rate 3");

                            TextComponent star4 = generateRatingStar("§8[§6✫✫✫✫§8] ", "§64 §7Sterne Bewertung", "/support rate 4");

                            TextComponent star5 = generateRatingStar("§8[§6✫✫✫✫✫§8]", "§65 §7Sterne Bewertung", "/support rate 5");

                            generatorRating(player, main, star1, star2, star3, star4, star5);
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast kein offenes Ticket."));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("rate")) {
                    if (args.length == 2) {
                        if (ticketManager.getTicketID(player.getUniqueId(), TicketStatus.CLOSED) != 0) {
                            if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId(), TicketStatus.CLOSED)).toArray().length == 0)) {
                                for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId(), TicketStatus.CLOSED))) {
                                    if (uuids != null) {
                                        supporterManager.addRating(uuids, Integer.parseInt(args[1]));
                                    }
                                }
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast dein Ticket bewertet."));
                                ticketManager.updateStatus(ticketManager.getTicketID(player.getUniqueId(), TicketStatus.CLOSED), TicketStatus.DELETED);
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Es waren keine Supporter in deinem Ticket."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast kein offenes Ticket."));
                        }
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Benutzung: /support rate <1-5>"));
                    }
                } else {
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss");
                    if (args[0].equalsIgnoreCase("stats")) {
                        if (args.length == 2) {
                            if (player.hasPermission("supportchat.stats.others")) {
                                SupportChat.getInstance().getUuidFetcher().fetchUUIDAsync(args[1], uuid -> {
                                    if (supporterManager.isSupporter(uuid)) {
                                        double rating = supporterManager.getAverageRating(uuid);

                                        Timestamp lastActiviy = supporterManager.getLastActivity(uuid);
                                        boolean isLoggedIn = supporterManager.isLoggedIn(uuid);
                                        int ticketCount = supporterManager.getTicketCounter(uuid);
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Die durchschnittliche von " + args[1] + " ist Bewertung: §e" + new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP).floatValue() + "§7/5"));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7" + args[1] + " hat §e" + ticketCount + "§7 Tickets bearbeitet."));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7" + args[1] + " war zuletzt aktiv am §e" + dateFormat.format(lastActiviy.toInstant().toEpochMilli()) + "§7."));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7" + args[1] + " ist §e" + (isLoggedIn ? "§aangemeldet" : "§cnicht angemeldet") + "§7."));
                                    } else
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dieser Spieler ist kein Supporter."));
                                });
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast keine Rechte."));
                            }
                        } else {
                            if (player.hasPermission("supportchat.stats.self")) {
                                if (supporterManager.isSupporter(player.getUniqueId())) {
                                    double rating = supporterManager.getAverageRating(player.getUniqueId());
                                    Timestamp lastActivity = supporterManager.getLastActivity(player.getUniqueId());
                                    boolean isLoggedIn = supporterManager.isLoggedIn(player.getUniqueId());
                                    int ticketCount = supporterManager.getTicketCounter(player.getUniqueId());
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Deine durchschnittliche Bewertung: §e" + new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP).floatValue() + "§7/5"));
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast §e" + ticketCount + "§7 Tickets bearbeitet."));
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du warst zuletzt aktiv am §e" + dateFormat.format(lastActivity.toInstant().toEpochMilli()) + "§7."));
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist §e" + (isLoggedIn ? "§aangemeldet" : "§cnicht angemeldet") + "§7."));
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du bist §ckein Supporter§7."));
                                }
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                            }
                        }
                    } else if (args[0].equalsIgnoreCase("move")) {
                        if (player.hasPermission("supportchat.move")) {
                            if (args.length == 3) {
                                if (ProxyServer.getInstance().getPlayer(args[1]) == null) {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Der Spieler ist offline"));
                                    return;
                                }

                                final UUID supUUID = ProxyServer.getInstance().getPlayer(args[1]).getUniqueId();
                                if (supporterManager.isSupporter(supUUID)) {
                                    if (ProxyServer.getInstance().getPlayer(args[2]) == null) {
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Der Spieler ist offline"));
                                        return;
                                    }
                                    int ticketID = ticketManager.getTicketID(ProxyServer.getInstance().getPlayer(args[2]).getUniqueId(), TicketStatus.OPEN);
                                    if (ticketID != 0) {
                                        if (!(ticketManager.getSupUUIDs(ticketID).contains(supUUID))) {
                                            ticketManager.addSups(ticketID, supUUID);
                                            supporterManager.addTicketCounter(supUUID);
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + ticketID + " §7den Supporter §e" + args[1] + " §7übergeben."));

                                            if (ProxyServer.getInstance().getPlayer(supUUID) != null) {
                                                final ProxiedPlayer sup = ProxyServer.getInstance().getPlayer(supUUID);
                                                sup.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + ticketID + " §7erhalten."));
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Das Ticket §e" + ticketID + " §7ist bereits dem Supporter §e" + args[1] + " §7zugewiesen."));
                                        }
                                    } else {
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dieser Spieler hat kein offenes Ticket."));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Dieser Spieler ist kein Supporter."));
                                }
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Benutze: §e/support move <Spieler> <TicketID>"));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                        }
                    } else if (args[0].equalsIgnoreCase("history")) {
                        if (player.hasPermission("supportchat.history")) {
                            if (args.length == 2) {
                                SupportChat.getInstance().getUuidFetcher().fetchUUIDAsync(args[1], targetUUID -> {
                                    if (ticketManager.getTickets(targetUUID).isEmpty()) {
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Der Spieler hat kein Ticket"));
                                        return;
                                    }
                                    for (int ticketID : ticketManager.getTickets(targetUUID)) {
                                        String userUUID = ticketManager.getUserUUID(ticketID);
                                        Timestamp creatingDate = ticketManager.getCreatingDate(ticketID);
                                        Timestamp closedDate = ticketManager.getClosedDate(ticketID);
                                        TicketStatus status = ticketManager.getStatus(ticketID);

                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Ticket-ID: §e" + ticketID));
                                        SupportChat.getInstance().getUuidFetcher().fetchNameAsync(UUID.fromString(userUUID), name -> {
                                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Spieler: §e" + name));
                                        });
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Erstellt am: §e" + dateFormat.format(creatingDate.toInstant().toEpochMilli())));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Geschlossen am: §e" + dateFormat.format(closedDate.toInstant().toEpochMilli())));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Supporter: §e"));
                                        for (UUID supUUID : ticketManager.getSupUUIDs(ticketID)) {
                                            SupportChat.getInstance().getUuidFetcher().fetchNameAsync(supUUID, name -> {
                                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§8- §e" + name));
                                            });
                                        }
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Status: §e" + status.getStatus()));
                                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + " "));
                                    }
                                });
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Benutze: §e/support history <Spieler>"));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                        }
                    } else if (args[0].equalsIgnoreCase("notify")) {
                        if (player.hasPermission("supportchat.notify")) {
                            if (supporterManager.isSupporter(player.getUniqueId())) {
                                if (supporterManager.isLoggedIn(player.getUniqueId())) {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast dich erfolgreich §causgelogt."));
                                    supporterManager.toggleIsLoggedIn(player.getUniqueId());
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§7Du hast dich erfolgreich §aeingelogt."));
                                    supporterManager.toggleIsLoggedIn(player.getUniqueId());
                                }
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDu bist kein Teammitglied."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                        }
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cNutze /support <close, history, notify, send, join, stats, move> [<Nachricht/Spielername>]"));
                    }
                }

            } else {
                player.sendMessage(TextComponent.fromLegacyText(SupportChat.getInstance().getPrefix() + "§cNutze /support um eine Supportanfrage zu erstellen§8."));
            }
        }
    }

    private void generatorRating(ProxiedPlayer target, TextComponent main, TextComponent star1, TextComponent star2, TextComponent star3, TextComponent star4, TextComponent star5) {
        main.addExtra(star1);
        main.addExtra(star2);
        main.addExtra(star3);
        main.addExtra(star4);
        main.addExtra(star5);
        target.sendMessage(main);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (player.hasPermission("supportchat.supporter")) {
                if (args.length == 1) {
                    return Arrays.asList("close", "history", "notify", "send", "join", "stats", "move");
                } else {
                    return Lists.newArrayList();
                }
            } else {
                if (args.length == 1) {
                    return Collections.singletonList("close");
                } else {
                    return Lists.newArrayList();
                }
            }
        } else {
            return Lists.newArrayList();
        }
    }
}
