package de.illegalaccess.supportchat.mysql;

import de.illegalaccess.supportchat.utils.TicketStatus;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class TicketManager {
    //Datenbank 2: ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    private Connection connection = MySQL.getInstance().getConnection();

    //insert new ticket with ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    public void insertTicket(int ticketID, String userUUID, String supUUIDs, String creatingDate, String deleteDate, TicketStatus status){
        Random random = new Random();
        int randomInt = random.nextInt(999999999);
        String sql = "INSERT INTO tickets (ticketID, userUUID, supUUIDs, status) VALUES ("+ticketID+", "+userUUID+", "+supUUIDs+", "+status.toString().toLowerCase(Locale.ROOT)+")";
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get ticketID from userUUID
    public int getTicketID(String userUUID){
        String sql = "SELECT ticketID FROM tickets WHERE userUUID = "+userUUID;
        try {
            return connection.prepareStatement(sql).executeQuery().getInt("ticketID");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //get supUUIDs in List from ticketID
    public List<UUID> getSupUUIDs(int ticketID) {
        List<UUID> sups = new ArrayList<>();
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings, ticketCounter, lastAcctiviy FROM tickets WHERE ticketID = '" +ticketID + "'").executeQuery();
            if (resultSet.next()) {
                for (String rating : resultSet.getString("ratings").split(",")) {
                    try {
                        sups.add(UUID.fromString(rating.replace(",", "")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return sups;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sups;
    }

    //add supUUIDs to list ticket
    public void addSups(int ticketID, String supUUID){
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings FROM tickets WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                String supUUIDs = resultSet.getString("supUUIDs");
                if (supUUIDs.equals("")) {
                    supUUIDs = supUUID + "";
                } else {
                    supUUIDs += "," + supUUID;
                }
                connection.prepareStatement("UPDATE tickets SET ratings = '" + supUUIDs + "' WHERE supUUID = '" + supUUID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get creatingDate from ticketID
    public Timestamp getCreatingDate(int ticketID){
        String sql = "SELECT creatingDate FROM tickets WHERE ticketID = "+ticketID;
        try {
            return connection.prepareStatement(sql).executeQuery().getTimestamp("creatingDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get deleteDate from ticketID
    public Timestamp getClosedDate(int ticketID){
        String sql = "SELECT closedDate FROM tickets WHERE ticketID = "+ticketID;
        try {
            return connection.prepareStatement(sql).executeQuery().getTimestamp("closedDate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get Status from ticketID
    public TicketStatus getStatus(int ticketID){
        String sql = "SELECT status FROM tickets WHERE ticketID = "+ticketID;
        try {
            return TicketStatus.valueOf(connection.prepareStatement(sql).executeQuery().getString("status").toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //update ticketStatus
    public void updateStatus(int ticketID, TicketStatus status){
        String sql = "UPDATE tickets SET status = '" + status.toString().toLowerCase() + "' WHERE ticketID = '" + ticketID + "'";
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update closedDate
    public void updateClosedDate(int ticketID){
        String sql = "UPDATE tickets SET closedDate = CURRENT_TIMESTAMP WHERE ticketID = '" + ticketID + "'";
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
