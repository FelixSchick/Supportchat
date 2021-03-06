package de.illegalaccess.supportchat.mysql;

import de.illegalaccess.supportchat.Supportchat;

import java.sql.*;

public class MySQL {

    private static MySQL instance;
    private Connection connection;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQL() {
        host = Supportchat.getInstance().getConfig().getString("MySQL.host");
        port = Supportchat.getInstance().getConfig().getInt("MySQL.port");
        database = Supportchat.getInstance().getConfig().getString("MySQL.database");
        username = Supportchat.getInstance().getConfig().getString("MySQL.username");
        password = Supportchat.getInstance().getConfig().getString("MySQL.password");
    }

    public static MySQL getInstance(){
        if(instance == null){
            instance = new MySQL();
        }
        return instance;
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
    }

    public void connect() {
        try {
            final String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true";
            System.out.println(url + "\nUser: " + username + "\nPasswort: " + password);
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("§7[§bMySQL§7] §aDie verbindung zur MySQL-Datenbank wurde hergestellt.");
        } catch (SQLException exception) {
            exception.fillInStackTrace();
            System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten, bitte überprüfe deine config!");
        }
    }

    public void disconnect() {
        try {
            if (connection != null) connection.close();
        } catch (SQLException exception) {
            exception.fillInStackTrace();
            System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten, bitte überprüfe deine config!");
        }
    }

    public void update(String qry) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(qry);
            statement.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("§7[§bMySQL§7] §cEin Fehler ist aufgetreten, Exeption: update error");
        }
    }

    public ResultSet qry(String query) {
        ResultSet rs = null;

        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            connect();
        }
        return rs;
    }
    //Datenbank 1: supUUID, ratings, ticketCounter, lastAcctiviy
    //Datenbank 2: ticketID, userUUID, supUUIDs, creatingDate, deleteDate, status


    public void createTabels() {
        update("CREATE TABLE IF NOT EXISTS `supporters` ( `supUUID` VARCHAR(36), `ratings` TEXT(65535), isLoggedIn BOOLEAN , `ticketCounter` INT(255), `lastActivity` TIMESTAMP, PRIMARY KEY (`supUUID`)) ENGINE = InnoDB;");
        update("CREATE TABLE IF NOT EXISTS `tickets` ( `ticketID` VARCHAR(16), `userUUID` VARCHAR(36), `supUUIDs` TEXT(65535), `creatingDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, `closedDate` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, `language` VARCHAR(10) , `status` VARCHAR(10), PRIMARY KEY (`ticketID`)) ENGINE = InnoDB;");
    }

    public Connection getConnection() {
        return connection;
    }

}
