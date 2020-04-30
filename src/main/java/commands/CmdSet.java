package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;

public class CmdSet implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();
    public static HashMap<String, HashMap<String, String>> configList = new HashMap<>();

    private void addToDatabase(String gid, String type, String value) {

        String sql = "INSERT INTO configTable VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE value = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, gid);
            pst.setString(2, type);
            pst.setString(3, value);
            pst.setString(4, value);

            pst.executeUpdate();


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeFromDatabase(String gid, String type) {

        String sql = "DELETE FROM cconfigTable WHERE gid = ? AND type = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, gid);
            pst.setString(2, type);

            pst.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public static void load() {

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery("SELECT  gid, type, value FROM configTable");


            while (rs.next()) {
                String gid = rs.getString(1);
                String type = rs.getString(2);
                String rid = rs.getString(3);

                if (!configList.containsKey(gid))
                    configList.put(gid, new HashMap<>());
                configList.get(gid).put(type, rid);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

            try {

                if (args.length >= 1) {

                    String gid = event.getGuild().getId();

                    if (!configList.containsKey(gid))
                        configList.put(gid, new HashMap<>());

                    Role mentioned;

                    switch (args[0]) {
                        case "verify":
                            mentioned = event.getMessage().getMentionedRoles().get(0);
                            configList.get(gid).put("verify", mentioned.getId());
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, "Successfully set @" + mentioned.getName() + " as a verify role!");
                            addToDatabase(gid, "verify", mentioned.getId());
                            break;
                        case "moderation":
                            mentioned = event.getMessage().getMentionedRoles().get(0);
                            configList.get(gid).put("moderation", mentioned.getId());
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, "Successfully set @" + mentioned.getName() + " as a moderation role!");
                            addToDatabase(gid, "moderation", mentioned.getId());
                            break;
                        case "modlog":
                            String tcid = args[1];
                            if (event.getGuild().getTextChannelById(tcid) != null) {
                                configList.get(gid).put("modlog", args[1]);
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, "Successfully set #" + event.getGuild().getTextChannelById(tcid).getName() + "as modlog!");
                                addToDatabase(gid, "modlog", args[1]);
                            } else {
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, "This textchannel doe not exist!");
                            }
                            break;
                        case "autorole":
                            mentioned = event.getMessage().getMentionedRoles().get(0);
                            configList.get(gid).put("autorole", mentioned.getId());
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, "Successfully set @" + mentioned.getName() + " as a auto role!");
                            addToDatabase(gid, "autorole", mentioned.getId());
                            break;
                        case "remove":
                            boolean exists = false;
                            switch (args[1]) {
                                case "verify":
                                case "moderation":
                                case "modlog":
                                case "autorole":
                                    exists = true;
                            }

                            if (exists && configList.get(gid).containsKey(args[1])) {
                                configList.get(gid).remove(args[1]);
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, "Successfully removed " + args[1] + " entry!");
                                removeFromDatabase(gid, args[1]);
                            }
                            break;
                        case "list":
                            StringBuilder sb = new StringBuilder();
                            if (configList.containsKey(gid)) {
                                for (String key : configList.get(gid).keySet()) {
                                    sb.append(":white_small_square:" + key + ": " + configList.get(gid).get(key) + "\n");
                                }
                                if (sb.length() <= 0) {
                                    sb.append("No config is set");
                                }

                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.BLUE, sb.toString());
                            }else {
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.BLUE, "No config is set");
                            }
                            break;
                        default:
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                    }
                } else {
                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                }
            } catch (IndexOutOfBoundsException e) {
                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL,
                        "You have to mention the role, you want to set up!");
            }
        } else {
            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL,
                    "ERROR!\nYou have to be admin to perform this command");
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_SET.toUpperCase() + " was executed by "
                + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return String.format("***USAGE***\n" +
                        ":white_small_square: `%s%s verify <role>` - Set verify role.\n" +
                        ":white_small_square: `%s%s moderation <role>` - Set moderation role.\n" +
                        ":white_small_square: `%s%s modlog <textChanneliId>` - Set modlog channel.\n" +
                        ":white_small_square: `%s%s autorole <role>` - Set autorole.\n" +
                        ":white_small_square: `%s%s list` - List all configs.\n" +
                        ":white_small_square: `%s%s remove <type>` - Removes the config setting.",
                Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET);
    }
}
