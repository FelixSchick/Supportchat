package de.bravemc.supportchat.mysql;

import de.bravemc.supportchat.SupportChat;

import java.sql.*;

public class MySQL {

    private static MySQL instance;
    private Connection connection;
    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL() {
        host = SupportChat.getInstance().getConfig().getString("MySQL.host");
        port = SupportChat.getInstance().getConfig().getInt("MySQL.port");
        database = SupportChat.getInstance().getConfig().getString("MySQL.database");
        username = SupportChat.getInstance().getConfig().getString("MySQL.username");
        password = SupportChat.getInstance().getConfig().getString("MySQL.password");
    }

    public static MySQL getInstance() {
        if (instance == null) {
            instance = new MySQL();
        }
        return instance;
    }

    @Override
    protected void finalize() {
        disconnect();
    }

    public void connect() {
        try {
            final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
            connection = DriverManager.getConnection(url, username, password);
            System.out.println(SupportChat.getInstance().getPrefix() + "§aDie verbindung zur MySQL-Datenbank wurde hergestellt.");
        } catch (SQLException exception) {
            exception.fillInStackTrace();
            System.out.println(SupportChat.getInstance().getPrefix() + "§cEin Fehler ist aufgetreten, bitte überprüfe deine config!");
        }
    }

    public void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException exception) {
            exception.fillInStackTrace();
            System.out.println(SupportChat.getInstance().getPrefix() + "§cEin Fehler ist aufgetreten, bitte überprüfe deine config!");
        }
    }

    public int update(String qry) {
        reconnect();
        try {
            PreparedStatement statement = connection.prepareStatement(qry);
            return statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println(SupportChat.getInstance().getPrefix() + "§cEin Fehler ist aufgetreten, Exception: update error");
        }
        return -1;
    }

    public ResultSet qry(String query) {
        reconnect();
        ResultSet rs = null;

        try {
            PreparedStatement st = connection.prepareStatement(query);
            rs = st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            connect();
        }
        return rs;
    }

    //Datenbank 1: supUUID, ratings, ticketCounter, lastActivity
    //Datenbank 2: ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status
    public void createTables() {
        update("CREATE TABLE IF NOT EXISTS `supporters` ( `supUUID` VARCHAR(36), `ratings` TEXT(65535), isLoggedIn BOOLEAN , `ticketCounter` INT(255), `lastActivity` TIMESTAMP, PRIMARY KEY (`supUUID`)) ENGINE = InnoDB;");
        update("CREATE TABLE IF NOT EXISTS `tickets` ( `ticketID` VARCHAR(16), `userUUID` VARCHAR(36), `supUUIDs` TEXT(65535), `creatingDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `closedDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `status` VARCHAR(10), PRIMARY KEY (`ticketID`)) ENGINE = InnoDB;");
    }

    public boolean isConnected(){
        if(connection != null){
            try {
                if(!connection.isClosed()){
                    if(connection.isValid(10)){
                        return true;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private void reconnect() {
       if(!isConnected()){
           disconnect();
           connect();
       }
    }
}
