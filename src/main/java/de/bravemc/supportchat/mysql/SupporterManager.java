package de.bravemc.supportchat.mysql;

import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SupporterManager {

    public void insertSupporter(String supUUID, int ticketCounter, boolean isLoggedIn) {
        final int isLoggedInInt = isLoggedIn ? 1 : 0;

        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("INSERT INTO supporters (supUUID, ratings, ticketCounter, isLoggedIn) VALUES ('" + supUUID + "','" + "" + "', '" + ticketCounter + "', '" + isLoggedInInt + "')"));
    }

    public void updateSupporter(String supUUID, int ratings, int ticketCounter) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE supporters SET ratings = " + ratings + ", ticketCounter = " + ticketCounter + " WHERE supUUID = '" + supUUID + "'"));
    }

    public List<Integer> getRatings(String supUUID) {
        List<Integer> ratings = Lists.newArrayList();
        try {
            ResultSet resultSet = MySQL.getInstance().qry("SELECT ratings, ticketCounter, lastAcctiviy FROM supporters WHERE supUUID = '" + supUUID + "'");
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
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT * FROM supports WHERE supUUID='" + supUUID + "';");
                try {
                    return resultSet.next();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTicketCounter(String supUUID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT ticketCounter FROM supporters WHERE supUUID = '" + supUUID + "'");
                try {
                    if (resultSet.next()) {
                        return resultSet.getInt("ticketCounter");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Timestamp getLastActivity(String supUUID) {
        try {
            ResultSet resultSet = MySQL.getInstance().qry("SELECT `lastActivity` FROM supporters WHERE supUUID = '" + supUUID + "'");
            if (resultSet.next()) {
                return resultSet.getTimestamp("lastActivity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateLastActivity(String supUUID) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE supporters SET `lastActivity` = NOW() WHERE supUUID = '" + supUUID + "'"));
    }

    public void addRating(String supUUID, int rating) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().qry("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'")).thenAcceptAsync(resultSet -> {
            try {
                if (resultSet.next()) {
                    String ratings = resultSet.getString("ratings");
                    if (ratings == "") {
                        ratings = rating + ",";
                    } else {
                        ratings += rating + ",";
                    }
                    MySQL.getInstance().update("UPDATE supporters SET ratings = '" + ratings + "' WHERE supUUID = '" + supUUID + "'");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void addTicketCounter(String supUUID) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().qry("SELECT ticketCounter FROM supporters WHERE supUUID = '" + supUUID + "'")).thenAcceptAsync(resultSet -> {
            try {
                if (resultSet.next()) {
                    int ticketCounter = resultSet.getInt("ticketCounter");
                    MySQL.getInstance().update("UPDATE supporters SET ticketCounter = " + (ticketCounter + 1) + " WHERE supUUID = '" + supUUID + "'");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public double getAverageRating(String supUUID) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                final ResultSet resultSet = MySQL.getInstance().qry("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'");
                try {
                    if (resultSet.next()) {
                        String ratings = resultSet.getString("ratings");
                        if (Objects.equals(ratings, "")) {
                            return 0.0;
                        } else {
                            String[] ratingsArray = ratings.split(",");
                            double sum = 0.0;
                            for (String rating : ratingsArray) {
                                if (!Objects.equals(rating, "")) {
                                    sum += Integer.parseInt(rating.replace(",", ""));
                                }
                            }
                            return sum / (ratingsArray.length);
                        }
                    }
                    return 0.0;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isLoggedIn(String supUUID) {
        try {
            ResultSet resultSet = MySQL.getInstance().qry("SELECT isLoggedIn FROM supporters WHERE supUUID = '" + supUUID + "'");
            if (resultSet.next()) {
                return resultSet.getBoolean("isLoggedIn");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void toggleIsLoggedIn(String supUUID) {
        CompletableFuture.supplyAsync(() -> {
            ResultSet resultSet = MySQL.getInstance().qry("SELECT isLoggedIn FROM supporters WHERE supUUID = '" + supUUID + "'");
            try {
                if (resultSet.next()) {
                    boolean isLoggedIn = resultSet.getBoolean("isLoggedIn");
                    CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE supporters SET isLoggedIn = " + !isLoggedIn + " WHERE supUUID = '" + supUUID + "'"));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }
}