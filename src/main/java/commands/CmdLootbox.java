package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.Color;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class CmdLootbox implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String password = ServerSettingsHandler.getDBPW();
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS");

    private static void put(String uid, LocalDateTime dateTime) {

        String sql = "INSERT INTO usertime VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usr, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String dateTimeS = dateTime.format(formatter);

            stmt.setString(1, uid);
            stmt.setString(2, dateTimeS);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void replace(String uid, LocalDateTime dateTime) {

        String sql = "UPDATE usertime SET datetime = ? WHERE uid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String dateTimeS = dateTime.format(formatter);

            stmt.setString(1, dateTimeS);
            stmt.setString(2, uid);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static boolean oneOrMoreDaysAfter(LocalDateTime d1, LocalDateTime d2) {
        Duration duration = Duration.between(d1, d2);
        long diff = Math.abs(duration.toMillis());

        return diff >= 86400000/*ms = 1d*/;

    }

    private static String getLootboxCode(int rn) {

        if (rn <= 50) {
            return "n_c";
        } else if (rn <= 70) {
            return "n_u";
        } else if (rn <= 85) {
            return "n_r";
        } else if (rn <= 95) {
            return "n_e";
        } else {
            return "n_l";
        }
    }

    private static Color getColorByLootbox(String i) {

        switch (i) {
            case "n_c":
                return new Color(126, 126, 126);
            case "n_u":
                return new Color(23, 162, 63);
            case "n_r":
                return new Color(31, 73, 191);
            case "n_e":
                return new Color(186, 0, 161);
            case "n_l":
                return new Color(228, 189, 36);
            default:
                return null;
        }
    }

    private static String getNameByLootbox(String i) {

        switch (i) {
            case "n_c":
                return "common";
            case "n_u":
                return "uncommon";
            case "n_r":
                return "rare";
            case "n_e":
                return "epic";
            case "n_l":
                return "legendary";
            default:
                return null;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        int rn = new Random().nextInt(100) + 1;
        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();
        boolean hasEntry = false;
        LocalDateTime dateTime = null;

        String hasEntryQuery = "SELECT uid FROM usertime WHERE uid = ?";
        String dateTimeQuery = "SELECT datetime FROM usertime WHERE uid = ?";
        String lootboxNumberQuery = "SELECT " + getLootboxCode(rn) + " FROM lootboxes WHERE uid = ?";
        String insertQuery = "INSERT INTO lootboxes VALUES (?, 0, 0, 0, 0, 0)";
        String updateQuery = "UPDATE lootboxes SET " + getLootboxCode(rn) + " = ? WHERE uid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, password);
             PreparedStatement hasEntryStatment = conn.prepareStatement(hasEntryQuery);
             PreparedStatement dateTimeStatment = conn.prepareStatement(dateTimeQuery)) {

            hasEntryStatment.setString(1, user.getId());
            ResultSet rs = hasEntryStatment.executeQuery();
            hasEntry = rs.next();

            if (hasEntry) {
                dateTimeStatment.setString(1, user.getId());
                ResultSet rs1 = dateTimeStatment.executeQuery();
                rs1.next();
                dateTime = LocalDateTime.parse(rs1.getString(1), formatter);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!hasEntry || oneOrMoreDaysAfter(dateTime, LocalDateTime.now())) {

            MessageMask.msg(tc, user, getColorByLootbox(getLootboxCode(rn)), "You've obtained a " + getNameByLootbox(getLootboxCode(rn)) + " lootbox!");

            System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + " obtained a " + getNameByLootbox(getLootboxCode(rn)) + " lootbox");

            if (hasEntry) {
                replace(event.getAuthor().getId(), LocalDateTime.now());
            } else {
                put(event.getAuthor().getId(), LocalDateTime.now());
            }

            try (Connection conn = DriverManager.getConnection(url, usr, password);
                 PreparedStatement lootboxNumberStatment = conn.prepareStatement(lootboxNumberQuery);
                 PreparedStatement insertStatment = conn.prepareStatement(insertQuery);
                 PreparedStatement updateStatment = conn.prepareStatement(updateQuery)) {

                int n;

                lootboxNumberStatment.setString(1, user.getId());
                ResultSet rs = lootboxNumberStatment.executeQuery();

                boolean boo = rs.next();

                if (!boo) {
                    insertStatment.setString(1, user.getId());
                    insertStatment.executeUpdate();
                    n = 0;
                }else {
                    n = Integer.valueOf(rs.getString(1));
                }

                updateStatment.setString(1, String.valueOf(n + 1));
                updateStatment.setString(2, user.getId());
                updateStatment.executeUpdate();

            } catch (SQLException e) {
                    System.out.println(String.format("[%s] %s", e.getErrorCode(), e.getMessage()));
                    e.printStackTrace();
            }

        } else {

            DecimalFormat df = new DecimalFormat("###.##");
            Duration duration = Duration.between(dateTime, LocalDateTime.now());

            double diff = Math.abs(duration.toMillis());
            diff = 86400000/*ms = 1d*/ - diff;
            double diffH = diff / 3600000;

            MessageMask.msg(tc, user, Color.RED, String.format("Wait %s houres to get your next lootbox!", df.format(diffH)));
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_LOOTBOX.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
