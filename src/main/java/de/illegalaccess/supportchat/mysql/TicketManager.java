package de.illegalaccess.supportchat.mysql;

import de.illegalaccess.supportchat.utils.TicketStatus;
import de.illegalaccess.supportchat.utils.UUIDManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

public class TicketManager {
    //Datenbank 2: ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    private Connection connection = MySQL.getInstance().getConnection();

    //insert new ticket with ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    public void insertTicket(String userUUID, String supUUIDs, TicketStatus status){
        Random random = new Random();
        int randomInt = random.nextInt(999999999);
        String sql = "INSERT INTO tickets (ticketID, userUUID, supUUIDs, status) VALUES ('"+randomInt+"', '"+userUUID+"', '"+supUUIDs+"', '"+status.toString()+"')";
        try {
            connection.prepareStatement(sql).executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get ticketID from userUUID

    //get ticketID if status = status
    public int getTicketID(String userUUID, TicketStatus status){
        String sql = "SELECT ticketID FROM tickets WHERE userUUID = '"+userUUID+"' AND status = '"+status.toString()+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ticketID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    //check if ticket exists and status closed by userUUID


    //get supUUIDs in List from ticketID
    public List<UUID> getSupUUIDs(int ticketID) {
        List<UUID> sups = new ArrayList<>();
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT supUUIDs FROM tickets WHERE ticketID = '" +ticketID + "'").executeQuery();
            if (resultSet.next()) {
                if (resultSet.getString("supUUIDs").contains(",")) {
                    for (String uuids : resultSet.getString("supUUIDs").split(",")) {
                        try {

                            if (uuids == "" || uuids == null) {
                                continue;
                            } else {
                                UUID uuid = UUID.fromString(uuids.replace(",", ""));
                                sups.add(uuid);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (resultSet.getString("supUUIDs").equals("")) {
                        return sups;
                    } else {
                        UUID uuid = UUID.fromString(resultSet.getString("supUUIDs"));
                        sups.add(uuid);
                    }

                }
            }
            return sups;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sups;
    }

    //get userUUID from ticketID
    public String getUserUUID(int ticketID){
        String sql = "SELECT userUUID FROM tickets WHERE `ticketID` = '"+ticketID+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("userUUID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //check if useruuid exist and status is open
    public boolean isTicketOpen(String userUUID){
        String sql = "SELECT status FROM tickets WHERE `userUUID` = '"+userUUID+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("status").equalsIgnoreCase("open");
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //add supUUIDs to list ticket
    public void addSups(int ticketID, String supUUID){
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT supUUIDs FROM tickets WHERE ticketID = '" + ticketID + "'").executeQuery();
            if (resultSet.next()) {
                String supUUIDs = resultSet.getString("supUUIDs");
                if (supUUIDs.equals("")) {
                    supUUIDs = supUUID + "";
                } else {
                    supUUIDs += "," + supUUID;
                }
                connection.prepareStatement("UPDATE tickets SET supUUIDs = '" + supUUIDs + "' WHERE ticketID = '" + ticketID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get creatingDate from ticketID
    public Timestamp getCreatingDate(int ticketID){
        String sql = "SELECT creatingDate FROM tickets WHERE ticketID = '"+ticketID+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("creatingDate");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get deleteDate from ticketID
    public Timestamp getClosedDate(int ticketID){
        String sql = "SELECT closedDate FROM tickets WHERE ticketID = '"+ticketID+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("closedDate");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //get Status from ticketID
    public TicketStatus getStatus(int ticketID){
        String sql = "SELECT status FROM tickets WHERE ticketID = '"+ticketID+"'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();
            if (resultSet.next()) {
                return TicketStatus.valueOf(resultSet.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //update ticketStatus
    public void updateStatus(int ticketID, TicketStatus status){
        String sql = "UPDATE tickets SET status = '" + status.toString() + "' WHERE ticketID = '" + ticketID + "'";
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
