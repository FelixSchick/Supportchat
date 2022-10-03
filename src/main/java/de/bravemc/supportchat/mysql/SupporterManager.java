package de.bravemc.supportchat.mysql;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SupporterManager {

    public void insertSupporter(UUID supUUID, int ticketCounter, boolean isLoggedIn) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("INSERT INTO supporters (supUUID, ratings, ticketCounter, isLoggedIn) VALUES ('" + supUUID + "','" + "" + "', '" + ticketCounter + "', '" + (isLoggedIn ? 1 : 0) + "')"));
    }

    public void updateSupporter(UUID supUUID, int ratings, int ticketCounter) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE supporters SET ratings = " + ratings + ", ticketCounter = " + ticketCounter + " WHERE supUUID = '" + supUUID + "'"));
    }

    public List<Integer> getRatings(UUID supUUID) {
        List<Integer> ratings = Lists.newArrayList();
        try {
            ResultSet resultSet = MySQL.getInstance().qry("SELECT ratings, ticketCounter, lastActivity FROM supporters WHERE supUUID = '" + supUUID + "'");
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

    @SneakyThrows
    public boolean isSupporter(UUID supUUID) {
        return CompletableFuture.supplyAsync(() -> {
            final ResultSet resultSet = MySQL.getInstance().qry("SELECT * FROM supporters WHERE supUUID='" + supUUID + "';");
            try {
                if (resultSet.next()) {
                    return true;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return false;
        }).get();
    }

    @SneakyThrows
    public int getTicketCounter(UUID supUUID) {

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

    }

    @SneakyThrows
    public Timestamp getLastActivity(UUID supUUID) {
        return CompletableFuture.supplyAsync(() -> MySQL.getInstance().qry("SELECT `lastActivity` FROM supporters WHERE supUUID = '" + supUUID + "'")).thenApplyAsync(resultSet -> {
            if (resultSet != null) {
                try {
                    if (resultSet.next()) {
                        return resultSet.getTimestamp("lastActivity");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }).get();
    }

    public void updateLastActivity(UUID supUUID) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().update("UPDATE supporters SET `lastActivity` = NOW() WHERE supUUID = '" + supUUID + "'"));
    }

    public void addRating(UUID supUUID, int rating) {
        CompletableFuture.supplyAsync(() -> MySQL.getInstance().qry("SELECT ratings FROM supporters WHERE supUUID = '" + supUUID + "'")).thenAcceptAsync(resultSet -> {
            try {
                if (resultSet.next()) {
                    String ratings = resultSet.getString("ratings");
                    if (ratings.isEmpty()) {
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

    public void addTicketCounter(UUID supUUID) {
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

    @SneakyThrows
    public double getAverageRating(UUID supUUID) {
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
    }

    @SneakyThrows
    public boolean isLoggedIn(UUID supUUID) {
        return CompletableFuture.supplyAsync(() -> MySQL.getInstance().qry("SELECT isLoggedIn FROM supporters WHERE supUUID = '" + supUUID + "'")).thenApplyAsync(resultSet -> {
            if (resultSet != null) {
                try {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("isLoggedIn");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return false;
        }).get();
    }

    public void toggleIsLoggedIn(UUID supUUID) {
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