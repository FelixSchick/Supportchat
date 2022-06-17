package de.illegalaccess.supportchat.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SupporterManager {

    private Connection connection = MySQL.getInstance().getConnection();

    public void insertSupporter(String supUUID, int ticketCounter, Boolean isLoggedIn) {
        int isLoggedInInt = 0;
        if (isLoggedIn)
            isLoggedInInt = 1;

        try {
            connection.prepareStatement("INSERT INTO supporters (supUUID, ratings, ticketCounter, isLoggedIn) VALUES ('" + supUUID + "','" + "" + "', '" + ticketCounter + "', '" +isLoggedInInt+"')").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateSupporter(String supUUID, int ratings, int ticketCounter) {
        try {
            connection.prepareStatement("UPDATE supporters SET ratings = " + ratings + ", ticketCounter = " + ticketCounter + " WHERE supUUID = '" + supUUID + "'").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void updateLastActivity(String supUUID) {
        try {
            connection.prepareStatement("UPDATE supporters SET `lastActivity` = NOW() WHERE supUUID = '" + supUUID + "'").executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addRating(String supUUID, int rating) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                String ratings = resultSet.getString("ratings");
                if (ratings == "") {
                    ratings = rating + ",";
                } else {
                    ratings += rating + ",";
                }
                connection.prepareStatement("UPDATE supporters SET ratings = '" + ratings + "' WHERE supUUID = '" + supUUID + "'").executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
    public double getAverageRating(String supUUID) {
        try {
            ResultSet resultSet = connection.prepareStatement("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'").executeQuery();
            if (resultSet.next()) {
                String ratings = resultSet.getString("ratings");
                if (ratings == "") {
                    return 0;
                } else {
                    String[] ratingsArray = ratings.split(",");
                    double sum = 0;
                    for (String rating : ratingsArray) {
                        if (rating != "") {
                            sum += Integer.parseInt(rating.replace(",", ""));
                        }
                    }
                    return sum / (ratingsArray.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
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
