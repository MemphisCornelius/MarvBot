package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.util.*;

public class CmdLootbox implements Command {


    private static HashMap<String, LocalDateTime> userIDs = new HashMap<>();

    private static void save() {

        File path = new File("SERVER_SETTINGS/");
        if (!path.exists()) {
            path.mkdir();
        }

        HashMap<String, LocalDateTime> out = new HashMap<>();

        userIDs.forEach((id, d) -> out.put(id, d));


        try {
            FileOutputStream fos = new FileOutputStream("SERVER_SETTINGS/userIDs_for_lootboxes_with_date.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void load() {

        File file = new File("SERVER_SETTINGS/userIDs_for_lootboxes_with_date.dat");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, LocalDateTime> out = (HashMap<String, LocalDateTime>) ois.readObject();
                ois.close();

                out.forEach((d, id) -> userIDs.put(d, id));

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
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
            return "n_l";
        } else {
            return "n_e";
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

        String url = ServerSettingsHandler.getDBURL();
        String usr = ServerSettingsHandler.getDBUS();
        String password = ServerSettingsHandler.getDBPW();

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (userIDs.containsKey(event.getAuthor().getId()) && oneOrMoreDaysAfter(userIDs.get(event.getAuthor().getId()), LocalDateTime.now()) || !userIDs.containsKey(event.getAuthor().getId())) {

            MessageMask.msg(tc, user, getColorByLootbox(getLootboxCode(rn)), "You've obtained a " + getNameByLootbox(getLootboxCode(rn)) + " lootbox!");

            System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + " obtained a " + getNameByLootbox(getLootboxCode(rn)) + " lootbox");

            if (userIDs.containsKey(event.getAuthor().getId())) {
                userIDs.replace(event.getAuthor().getId(), userIDs.get(event.getAuthor().getId()), LocalDateTime.now());
            } else {
                userIDs.put(event.getAuthor().getId(), LocalDateTime.now());
            }
            save();

            try {
                Connection conn = DriverManager.getConnection(url, usr, password);
                Statement stmt = conn.createStatement();

                String sql = String.format("SELECT %s FROM lootboxes WHERE uid=%s", getLootboxCode(rn), event.getAuthor().getId());
                ResultSet rs = stmt.executeQuery(sql);
                rs.next();

                String test = rs.getString(1);
                int n = Integer.valueOf(test);


                sql = String.format("UPDATE lootboxes SET %s=%s WHERE uid=%s", getLootboxCode(rn), n + 1, event.getAuthor().getId());
                stmt.executeUpdate(sql);

                stmt.close();
                conn.close();

            } catch (SQLException e) {
                try {
                    Connection conn = DriverManager.getConnection(url, usr, password);
                    Statement stmt = conn.createStatement();

                    String sql = String.format("INSERT INTO lootboxes VALUES (%s, 0, 0, 0, 0, 0)", event.getAuthor().getId());
                    stmt.executeUpdate(sql);

                    sql = String.format("UPDATE lootboxes SET %s=%s WHERE uid=%s", getLootboxCode(rn), 1, event.getAuthor().getId());
                    stmt.executeUpdate(sql);

                    stmt.close();
                    conn.close();

                } catch (SQLException ex) {
                    System.out.println(String.format("[%s] %s", ex.getErrorCode(), ex.getMessage()));
                }
            }

        } else {

            Duration duration = Duration.between(userIDs.get(event.getAuthor().getId()), LocalDateTime.now());
            long diff = Math.abs(duration.toMillis());
            long diifH = diff / 3600000;

            MessageMask.msg(tc, user, Color.RED, String.format("Wait %shoures to get your next lootbox!", diifH));
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
