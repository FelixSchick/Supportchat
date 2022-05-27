package de.illegalaccess.supportchat.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SupporterManager {
    //Datenbank 1: supUUID, ratings, ticketCounter, lastActiviy
    private Connection connection = MySQL.getInstance().getConnection();

    //insert into supUUID, ratings, ticketCounter, lastActiviy into supporters
    public void insertSupporter(String supUUID, int ratings, int ticketCounter, Boolean isLoggedIn) {
        try {
            connection.prepareStatement("INSERT INTO supporters (supUUID, ratings, ticketCounter, isLoggedIn) VALUES ('" + supUUID + "',' " + ratings + "', '" + ticketCounter + "', '" +isLoggedIn+"')").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update ratings, ticketCounter, lastAcctiviy into supporters
    public void updateSupporter(String supUUID, int ratings, int ticketCounter) {
        try {
            connection.prepareStatement("UPDATE supporters SET ratings = " + ratings + ", ticketCounter = " + ticketCounter + " WHERE supUUID = '" + supUUID + "'").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get ratings in arry
    public List<Integer> getRatings(String supUUID) {
        List<Integer> ratings = new ArrayList<>();
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings, ticketCounter, lastAcctiviy FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                for (String rating : resultSet.getString("ratings").split(",")) {
                    try {
                        ratings.add(Integer.parseInt(rating.replace(",", "")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return ratings;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ratings;
    }

    //check if supporter exists
    public boolean isSupporter(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT * FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //get ticketCounter
    public int getTicketCounter(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ticketCounter FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("ticketCounter");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //get lastAcctiviy in timestamp
    public Timestamp getLastActivity(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT `lastActivity` FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                return resultSet.getTimestamp("lastActivity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //update lastActiviy
    public void updateLastActivity(String supUUID) {
        try {
            connection.prepareStatement("UPDATE supporters SET `lastActivity` = NOW() WHERE supUUID = '" + supUUID + "'").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add rating to ratings
    public void addRating(String supUUID, int rating) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                String ratings = resultSet.getString("ratings");
                if (ratings.equals("")) {
                    ratings = rating + "";
                } else {
                    ratings += "," + rating;
                }
                connection.prepareStatement("UPDATE supporters SET ratings = '" + ratings + "' WHERE supUUID = '" + supUUID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //add ticketCounter
    public void addTicketCounter(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ticketCounter FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                int ticketCounter = resultSet.getInt("ticketCounter");
                connection.prepareStatement("UPDATE supporters SET ticketCounter = " + (ticketCounter + 1) + " WHERE supUUID = '" + supUUID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //get durchschnittliche rating from getRatings
    public double getAverageRating(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                String ratings = resultSet.getString("ratings");
                if (ratings.equals("")) {
                    return 0;
                } else {
                    String[] ratingsArray = ratings.split(",");
                    double sum = 0;
                    for (String rating : ratingsArray) {
                        sum += Integer.parseInt(rating.replace(",", ""));
                    }
                    return sum / ratingsArray.length;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //check if supporter isLoggedIn
    public boolean isLoggedIn(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT isLoggedIn FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("isLoggedIn");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //toggle isLoggedIn
    public void toggleIsLoggedIn(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT isLoggedIn FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                boolean isLoggedIn = resultSet.getBoolean("isLoggedIn");
                connection.prepareStatement("UPDATE supporters SET isLoggedIn = " + !isLoggedIn + " WHERE supUUID = '" + supUUID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
