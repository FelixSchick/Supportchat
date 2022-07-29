package de.illegalaccess.supportchat.commands;

import com.google.common.collect.Lists;
import de.illegalaccess.supportchat.Supportchat;
import de.illegalaccess.supportchat.mysql.SupporterManager;
import de.illegalaccess.supportchat.mysql.TicketManager;
import de.illegalaccess.supportchat.utils.TicketLanguage;
import de.illegalaccess.supportchat.utils.TicketStatus;
import de.illegalaccess.supportchat.utils.UUIDManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

public class SupportCommand extends Command implements TabExecutor {

    public SupportCommand() {
        super("support");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        SupporterManager supporterManager = new SupporterManager();
        TicketManager ticketManager = new TicketManager();
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("German") || args[0].equalsIgnoreCase("English")) {
                    if (supporterManager.isSupporter(player.getUniqueId().toString())) {
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDu bist bereits ein Supporter! Du darfst kein Ticket öffnen :P"));
                        return;
                    }
                    if (args[0].equalsIgnoreCase("German")) {
                        if (ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.OPEN) == 0) {
                            TextComponent main = new TextComponent(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + player.getDisplayName() + " §8[§e" + args[0] + "§8] §7benötigt hilfe.");
                            main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klicke hier um das Ticket zu öffnen!").create()));
                            main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support join " + player.getName()));
                            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                                if (all.hasPermission("supportchat.notify")) {
                                    if (supporterManager.isLoggedIn(all.getUniqueId().toString())) {
                                        all.sendMessage(main);
                                    }
                                }
                            }
                            ticketManager.insertTicket(player.getUniqueId().toString(), "", TicketLanguage.GERMAN, TicketStatus.OPEN);
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast ein neues Ticket erstellt."));
                        }
                    } else if (args[0].equalsIgnoreCase("English")) {
                        if (ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.OPEN) == 0) {
                            TextComponent main = new TextComponent(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + player.getDisplayName() + " §8[§e" + args[0] + "§8] §7benötigt hilfe.");
                            main.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§7Klicke hier um das Ticket zu öffnen!").create()));
                            main.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support join " + player.getName()));
                            for (ProxiedPlayer all : ProxyServer.getInstance().getPlayers()) {
                                if (all.hasPermission("supportchat.notify")) {
                                    if (supporterManager.isLoggedIn(all.getUniqueId().toString())) {
                                        all.sendMessage(main);
                                    }
                                }
                            }
                            ticketManager.insertTicket(player.getUniqueId().toString(), "", TicketLanguage.ENGLISH, TicketStatus.OPEN);
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast ein neues Ticket erstellt."));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("join")) {
                    if (player.hasPermission("supportchat.join")) {
                        if (args.length == 2) {
                            if (supporterManager.isLoggedIn(player.getUniqueId().toString())) {
                                if (UUIDManager.getUUID(args[1]) != null) {
                                    String uuid = UUIDManager.getUUID(args[1]).toString();
                                    if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                        if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN)).contains(player.getUniqueId()))) {
                                            ticketManager.addSups(ticketManager.getTicketID(uuid, TicketStatus.OPEN), player.getUniqueId().toString());
                                            supporterManager.addTicketCounter(player.getUniqueId().toString());
                                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist dem Suppportchat beigetreten."));
                                            if (ticketManager.getLanguage(ticketManager.getTicketID(uuid, TicketStatus.OPEN)) == TicketLanguage.GERMAN) {
                                                for (UUID sup : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN))) {
                                                    ProxiedPlayer supPlayer = ProxyServer.getInstance().getPlayer(sup);
                                                    supPlayer.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + args[1] + " §8» §e" + player.getDisplayName() + "§8: §7Herzlich willkommen im offiziellen Support von BraveMC.de wie kann ich dir weiterhelfen?"));
                                                }
                                                if (ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null) {
                                                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                                                    target.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + args[1] + " §8» §e" + player.getDisplayName() + "§8: §7Herzlich willkommen im offiziellen Support von BraveMC.de wie kann ich dir weiterhelfen?"));
                                                }
                                            } else {
                                                for (UUID sup : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN))) {
                                                    ProxiedPlayer supPlayer = ProxyServer.getInstance().getPlayer(sup);
                                                    supPlayer.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + args[1] + " §8» §e" + player.getDisplayName() + "§8: §7Welcome to the official support of BraveMC.de how can I help you?"));
                                                }
                                                if (ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null) {
                                                    ProxiedPlayer target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                                                    target.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + args[1] + " §8» §e" + player.getDisplayName() + "§8: §7Welcome to the official support of BraveMC.de how can I help you?"));
                                                }
                                            }
                                        } else
                                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist bereits dem Ticket beigetreten."));
                                    } else
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler ist in keinem Supportchat."));
                                } else
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + "§8 konnte nicht gefunden werden."));
                            } else
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist nicht eingeloggt."));
                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Benutze §e/support join <Spieler>"));
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDu hast keine Rechte um diesen Befehl zu nutzen."));
                } else if (args[0].equalsIgnoreCase("send")) {
                    if (player.hasPermission("supportchat.send")) {
                        if (args.length >= 2) {
                            if (supporterManager.isLoggedIn(player.getUniqueId().toString())) {
                                if (UUIDManager.getUUID(args[1]) != null) {
                                    String uuid = UUIDManager.getUUID(args[1]).toString();
                                    if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                        StringBuilder message = new StringBuilder();
                                        for (int i = 2; i < args.length; i++) {
                                            message.append(args[i]).append(" ");
                                        }
                                        if (ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null) {
                                            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                                            target.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7" + player.getDisplayName() + "§8: §7" + message));

                                            for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.OPEN))) {
                                                if (uuids != null) {
                                                    if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                                        ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                                        all.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§e" + args[1] + " §8» " + player.getDisplayName() + "§8: §7" + message));
                                                    }
                                                }
                                            }
                                        } else {
                                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + " §7ist nicht online."));
                                        }
                                    } else
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + " §7hat kein Ticket."));
                                } else
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler §e" + args[1] + "§8 konnte nicht gefunden werden."));
                            } else
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist nicht eingeloggt."));
                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Benutze §e/support send <Spieler> <Nachricht>"));
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDu hast keine Rechte um diesen Befehl zu nutzen."));
                } else if (args[0].equalsIgnoreCase("close")) {
                    if (args.length == 2) {
                        if (supporterManager.isLoggedIn(player.getUniqueId().toString()) && supporterManager.isSupporter(player.getUniqueId().toString())) {
                            if (UUIDManager.getUUID(args[1]) != null) {
                                String uuid = UUIDManager.getUUID(args[1]).toString();
                                if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                                    ticketManager.updateClosedDate(ticketManager.getTicketID(uuid, TicketStatus.OPEN));
                                    ticketManager.updateStatus(ticketManager.getTicketID(uuid, TicketStatus.OPEN), TicketStatus.CLOSED);
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + args[1] + " §7geschlossen."));
                                    if (ProxyServer.getInstance().getPlayer(UUID.fromString(uuid)) != null) {
                                        ProxiedPlayer target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
                                        target.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dein Ticket wurde von §e" + player.getDisplayName() + " §7geschlossen."));
                                        TextComponent main = new TextComponent(Supportchat.getInstance().getPrefix() + "§8Du kannst nun den Support bewerten(mit einem Klick auf die Sterne):\n");

                                        TextComponent star1 = new TextComponent("§8[§6✩§8] ");
                                        star1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§61 §7Stern Bewertung").create()));
                                        star1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 1"));

                                        TextComponent star2 = new TextComponent("§8[§6✫✫§8] ");
                                        star2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§62 §7Sterne Bewertung").create()));
                                        star2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 2"));

                                        TextComponent star3 = new TextComponent("§8[§6✫✫✫§8] ");
                                        star3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§63 §7Sterne Bewertung").create()));
                                        star3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 3"));

                                        TextComponent star4 = new TextComponent("§8[§6✫✫✫✫§8] ");
                                        star4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§64 §7Sterne Bewertung").create()));
                                        star4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 4"));

                                        TextComponent star5 = new TextComponent("§8[§6✫✫✫✫✫§8]");
                                        star5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§65 §7Sterne Bewertung").create()));
                                        star5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 5"));

                                        main.addExtra(star1);
                                        main.addExtra(star2);
                                        main.addExtra(star3);
                                        main.addExtra(star4);
                                        main.addExtra(star5);
                                        target.sendMessage(main);
                                    }
                                    if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED)).toArray().length == 0)) {
                                        for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED))) {
                                            if (uuids != null) {
                                                if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                                    ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                                    supporterManager.updateLastActivity(uuids.toString());
                                                    all.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§8[§e" + args[1] + "§8] §7Das Ticket wurde von §e" + player.getName() + " §7geschlossen."));
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler ist in keinem Supportchat."));
                                }
                            } else{
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Das Ticket wurde nicht gefunden."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist entweder kein Teammitglied oder du bist §causgeloggt."));
                        }
                    } else {
                        final String uuid = player.getUniqueId().toString();
                        if (ticketManager.getTicketID(uuid, TicketStatus.OPEN) != 0) {
                            ticketManager.updateClosedDate(ticketManager.getTicketID(uuid, TicketStatus.OPEN));
                            ticketManager.updateStatus(ticketManager.getTicketID(uuid, TicketStatus.OPEN), TicketStatus.CLOSED);

                            if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED)).toArray().length == 0)) {
                                for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(uuid, TicketStatus.CLOSED))) {
                                    if (uuids != null) {
                                        if (ProxyServer.getInstance().getPlayer(uuids) != null) {
                                            ProxiedPlayer all = ProxyServer.getInstance().getPlayer(uuids);
                                            supporterManager.updateLastActivity(uuids.toString());
                                            all.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§8[§e" + player.getName() + "§8] §7Das Ticket wurde von §e" + player.getName() + " §7geschlossen."));
                                        }
                                    }
                                }
                            }

                            TextComponent main = new TextComponent(Supportchat.getInstance().getPrefix() + "§7Du hast dein Ticket §cgeschlossen. \n §7Du kannst nun den Support bewerten (mit einem Klick auf die Sterne):\n");

                            TextComponent star1 = new TextComponent("§8[§6✩§8] ");
                            star1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§61 §7Stern Bewertung").create()));
                            star1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 1"));

                            TextComponent star2 = new TextComponent("§8[§6✫✫§8] ");
                            star2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§62 §7Sterne Bewertung").create()));
                            star2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 2"));

                            TextComponent star3 = new TextComponent("§8[§6✫✫✫§8] ");
                            star3.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§63 §7Sterne Bewertung").create()));
                            star3.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 3"));

                            TextComponent star4 = new TextComponent("§8[§6✫✫✫✫§8] ");
                            star4.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§64 §7Sterne Bewertung").create()));
                            star4.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 4"));

                            TextComponent star5 = new TextComponent("§8[§6✫✫✫✫✫§8]");
                            star5.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§65 §7Sterne Bewertung").create()));
                            star5.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/support rate 5"));

                            main.addExtra(star1);
                            main.addExtra(star2);
                            main.addExtra(star3);
                            main.addExtra(star4);
                            main.addExtra(star5);

                            player.sendMessage(main);
                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast kein offenes Ticket."));
                    }
                } else if (args[0].equalsIgnoreCase("rate")) {
                    if (args.length == 2) {
                        if (ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.CLOSED) != 0) {
                            if (!(ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.CLOSED)).toArray().length == 0)) {
                                for (UUID uuids : ticketManager.getSupUUIDs(ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.CLOSED))) {
                                    if (uuids != null) {
                                        supporterManager.addRating(uuids.toString(), Integer.parseInt(args[1]));
                                    }
                                }
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast dein Ticket bewertet."));
                                ticketManager.updateStatus(ticketManager.getTicketID(player.getUniqueId().toString(), TicketStatus.CLOSED), TicketStatus.DELETED);
                            } else
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Es waren keine Supporter in deinem Ticket."));
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast kein offenes Ticket."));
                        }
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Benutzung: /support rate <1-5>"));
                } else if (args[0].equalsIgnoreCase("stats")) {
                    if (args.length == 2) {
                        if (player.hasPermission("supportchat.stats.others")) {
                            if (UUIDManager.getUUID(args[1]) != null) {
                                String uuid = UUIDManager.getUUID(args[1]).toString();
                                if (supporterManager.isSupporter(uuid)) {
                                    double rating = supporterManager.getAverageRating(uuid);

                                    Timestamp lastActiviy = supporterManager.getLastActivity(uuid);
                                    boolean isLoggedIn = supporterManager.isLoggedIn(uuid);
                                    int ticketCount = supporterManager.getTicketCounter(uuid);
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Die durchschnittliche von " + args[1] + " ist Bewertung: §e" + new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP).floatValue() + "§7/5"));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7" + args[1] + " hat §e" + ticketCount + "§7 Tickets bearbeitet."));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7" + args[1] + " war zuletzt aktiv am §e" + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(lastActiviy.toInstant().toEpochMilli()) + "§7."));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7" + args[1] + " ist §e" + (isLoggedIn ? "§aangemeldet" : "§cnicht angemeldet") + "§7."));
                                } else
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler ist kein Supporter."));
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler existiert nicht."));
                            }
                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast keine Rechte."));
                    } else {
                        if (player.hasPermission("supportchat.stats.self")) {
                            if (supporterManager.isSupporter(player.getUniqueId().toString())) {
                                double rating = supporterManager.getAverageRating(player.getUniqueId().toString());
                                Timestamp lastActiviy = supporterManager.getLastActivity(player.getUniqueId().toString());
                                boolean isLoggedIn = supporterManager.isLoggedIn(player.getUniqueId().toString());
                                int ticketCount = supporterManager.getTicketCounter(player.getUniqueId().toString());
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Deine durchschnittliche Bewertung: §e" + new BigDecimal(rating).setScale(2, RoundingMode.HALF_UP).floatValue() + "§7/5"));
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast §e" + ticketCount + "§7 Tickets bearbeitet."));
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du warst zuletzt aktiv am §e" + new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss").format(lastActiviy.toInstant().toEpochMilli()) + "§7."));
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist §e" + (isLoggedIn ? "§aangemeldet" : "§cnicht angemeldet") + "§7."));
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du bist §ckein Supporter§7."));
                            }
                        } else {
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                        }
                    }
                } else if (args[0].equalsIgnoreCase("move")) {
                    if (player.hasPermission("supportchat.move")) {
                        if (args.length == 3) {
                            String supUUID = UUIDManager.getUUID(args[1]).toString();
                            if (supporterManager.isSupporter(supUUID)) {
                                int ticketID = ticketManager.getTicketID(UUIDManager.getUUID(args[2]).toString(), TicketStatus.OPEN);
                                if (ticketID != 0) {
                                    if (!(ticketManager.getSupUUIDs(ticketID).contains(supUUID))) {
                                        ticketManager.addSups(ticketID, supUUID);
                                        supporterManager.addTicketCounter(supUUID);
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + ticketID + " §7den Supporter §e" + args[1] + " §7übergeben."));

                                        if (ProxyServer.getInstance().getPlayer(UUID.fromString(supUUID)) != null) {
                                            ProxiedPlayer sup = ProxyServer.getInstance().getPlayer(UUID.fromString(supUUID));
                                            sup.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast das Ticket §e" + ticketID + " §7erhalten."));
                                        }
                                    } else
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Das Ticket §e" + ticketID + " §7ist bereits dem Supporter §e" + args[1] + " §7zugewiesen."));

                                } else
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler hat kein offenes Ticket."));

                            } else
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler ist kein Supporter."));

                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Benutze: §e/support move <Spieler> <TicketID>"));
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                } else if (args[0].equalsIgnoreCase("history")) {
                    if (player.hasPermission("supportchat.history")) {
                        if (args.length == 2) {
                            if (ProxyServer.getInstance().getPlayer(args[1]) != null) {
                                ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[1]);
                                if (ticketManager.getTickets(target.getUniqueId().toString()).isEmpty())
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler hat kein Ticket"));
                                for (int ticketID : ticketManager.getTickets(target.getUniqueId().toString())) {
                                    String userUUID = ticketManager.getUserUUID(ticketID);
                                    Timestamp creatingDate = ticketManager.getCreatingDate(ticketID);
                                    Timestamp closedDate = ticketManager.getClosedDate(ticketID);
                                    TicketStatus status = ticketManager.getStatus(ticketID);

                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Ticket-ID: §e" + ticketID));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Spieler: §e" + UUIDManager.getName(UUID.fromString(userUUID))));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Erstellt am: §e" + creatingDate));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Geschlossen am: §e" + closedDate));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Supporter: §e"));
                                    for (UUID supUUID : ticketManager.getSupUUIDs(ticketID)) {
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§8- §e" + UUIDManager.getName(supUUID)));
                                    }
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Status: §e" + status.toString()));
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + " "));
                                }

                            } else {
                                if (UUIDManager.getUUID(args[1]) != null) {
                                    if (ticketManager.getTickets(UUIDManager.getUUID(args[1]).toString()).isEmpty())
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Der Spieler hat kein Ticket"));
                                    for (int ticketID : ticketManager.getTickets(UUIDManager.getUUID(args[1]).toString())) {
                                        String userUUID = ticketManager.getUserUUID(ticketID);
                                        Timestamp creatingDate = ticketManager.getCreatingDate(ticketID);
                                        Timestamp closedDate = ticketManager.getClosedDate(ticketID);
                                        TicketStatus status = ticketManager.getStatus(ticketID);

                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Ticket-ID: §e" + ticketID));
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Spieler: §e" + UUIDManager.getName(UUID.fromString(userUUID))));
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Erstellt am: §e" + creatingDate));
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Geschlossen am: §e" + closedDate));
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Supporter: §e"));
                                        for (UUID supUUID : ticketManager.getSupUUIDs(ticketID)) {
                                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "\t§8- §e" + UUIDManager.getName(supUUID)));
                                        }
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Status: §e" + status.toString()));
                                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + " "));
                                    }
                                } else {
                                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Dieser Spieler existiert nicht."));
                                }
                            }

                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Benutze: §e/support history <Spieler>"));
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                } else if (args[0].equalsIgnoreCase("notify")) {
                    if (player.hasPermission("supportchat.notify")) {
                        if (supporterManager.isSupporter(player.getUniqueId().toString())) {
                            if (supporterManager.isLoggedIn(player.getUniqueId().toString())) {
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast dich erfolgreich §causgelogt."));
                                supporterManager.toggleIsLoggedIn(player.getUniqueId().toString());
                            } else {
                                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§7Du hast dich erfolgreich §aeingelogt."));
                                supporterManager.toggleIsLoggedIn(player.getUniqueId().toString());
                            }
                        } else
                            player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDu bist kein Teammitglied."));
                    } else
                        player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cDazu hast du keine Rechte."));
                } else {
                    player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cNutze /support <close, history, notify, send, join, stats, move> [<Nachricht/Spielername>]"));
                }

            } else {
                player.sendMessage(TextComponent.fromLegacyText(Supportchat.getInstance().getPrefix() + "§cNutze /support <German/English>/<close>."));
            }
        }
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
                    return Arrays.asList("German", "English", "close");
                } else {
                    return Lists.newArrayList();
                }
            }
        } else {
            return Lists.newArrayList();
        }
    }
}
