package de.bravemc.supportchat.mysql;

import de.bravemc.supportchat.utils.TicketStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class TicketManager {
    //Datenbank 2: ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    //insert new ticket with ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    public void insertTicket(String userUUID, String supUUIDs, TicketStatus status) {
        Random random = new Random();
        int randomInt = random.nextInt(999999999);
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("INSERT INTO tickets (ticketID, userUUID, supUUIDs, status) VALUES ('" + randomInt + "', '" + userUUID + "', '" + supUUIDs + "',  '" + status.toString() + "')"));
    }

    //get ticketID from userUUID
    //get ticketID if status = status
    public int getTicketID(String userUUID, TicketStatus status) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT ticketID FROM tickets WHERE userUUID = '" + userUUID + "' AND status = '" + status.toString() + "'");
                try {
                    if (resultSet.next()) {
                        return resultSet.getInt("ticketID");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //get ticketID from userUUID order by creatingDate
    public int getTicketIDOrderd(String userUUID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT ticketID FROM tickets WHERE userUUID = '" + userUUID + "' ORDER BY creatingDate DESC");
                try {
                    if (resultSet.next()) {
                        return resultSet.getInt("ticketID");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //get all tickets from userUUID
    public List<Integer> getTickets(String userUUID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                List<Integer> tickets = new ArrayList<>();
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT ticketID FROM tickets WHERE userUUID = '" + userUUID + "'");
                try {
                    while (resultSet.next()) {
                        tickets.add(resultSet.getInt("ticketID"));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return tickets;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //check if ticket exists and status closed by userUUID
    //get supUUIDs in List from ticketID
    public List<UUID> getSupUUIDs(int ticketID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                List<UUID> sups = new ArrayList<>();
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT supUUIDs FROM tickets WHERE ticketID = '" + ticketID + "'");
                    if (resultSet.next()) {
                        if (resultSet.getString("supUUIDs").contains(",")) {
                            for (String uuids : resultSet.getString("supUUIDs").split(",")) {
                                try {
                                    if (!Objects.equals(uuids, "") || uuids != null) {
                                        UUID uuid = UUID.fromString(uuids.replace(",", ""));
                                        sups.add(uuid);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            final String supUUIDs = resultSet.getString("supUUIDs");
                            if (!supUUIDs.equals("")) {
                                UUID uuid = UUID.fromString(supUUIDs);
                                sups.add(uuid);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return sups;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //get userUUID from ticketID
    public String getUserUUID(int ticketID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT userUUID FROM tickets WHERE `ticketID` = '" + ticketID + "'");
                    if (resultSet.next()) {
                        return resultSet.getString("userUUID");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //check if useruuid exist and status is open
    public boolean isTicketOpen(String userUUID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT status FROM tickets WHERE userUUID='" + userUUID + "'");
                    if (resultSet.next()) {
                        return resultSet.getString("status").equalsIgnoreCase("open");
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //add supUUIDs to list ticket
    public void addSups(int ticketID, String supUUID) {
        CompletableFuture.supplyAsync(() -> {
            try {
                ResultSet resultSet = MySQL.getInstance().qry("SELECT supUUIDs FROM tickets WHERE ticketID = '" + ticketID + "'");
                if (resultSet.next()) {
                    String supUUIDs = resultSet.getString("supUUIDs");
                    if (supUUIDs.equals("")) {
                        supUUIDs = supUUID + "";
                    } else {
                        supUUIDs += "," + supUUID;
                    }
                    final String finalSupUUIDs = supUUIDs;
                    CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE tickets SET supUUIDs = '" + finalSupUUIDs + "' WHERE ticketID = '" + ticketID + "'"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    //get creatingDate from ticketID
    public Timestamp getCreatingDate(int ticketID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT creatingDate FROM tickets WHERE ticketID = '" + ticketID + "'");
                    if (resultSet.next()) {
                        return resultSet.getTimestamp("creatingDate");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //get deleteDate from ticketID
    public Timestamp getClosedDate(int ticketID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT closedDate FROM tickets WHERE ticketID = '" + ticketID + "'");
                    if (resultSet.next()) {
                        return resultSet.getTimestamp("closedDate");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //get Status from ticketID
    public TicketStatus getStatus(int ticketID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    ResultSet resultSet = MySQL.getInstance().qry("SELECT status FROM tickets WHERE ticketID = '" + ticketID + "'");
                    if (resultSet.next()) {
                        return TicketStatus.valueOf(resultSet.getString("status"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    //update ticketStatus
    public void updateStatus(int ticketID, TicketStatus status) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE tickets SET status = '" + status.toString() + "' WHERE ticketID = '" + ticketID + "'"));
    }

    //update closedDate
    public void updateClosedDate(int ticketID) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE tickets SET closedDate = CURRENT_TIMESTAMP WHERE ticketID = '" + ticketID + "'"));
    }
}
