package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CmdDVCbGIgnore implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    private static void add(String vcid, String gid ) {

        String sql = "INSERT INTO dvcbgignore VALUES (?, ?)";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, vcid);
            pst.setString(2, gid);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void remove(String id) {

        String sql = "DELETE FROM dvcbgignore WHERE vcid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static List<String> list(String gid) {

        List<String> list = new ArrayList<>();

        String sql = "SELECT vcid FROM dvcbgignore WHERE gid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, gid);
            ResultSet rs =  pst.executeQuery();

            while (rs.next()) {
                list.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<String> getDVCbGIgnore() {

        List<String> list = new ArrayList<>();

        String sql = "SELECT vcid FROM dvcbgignore";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            ResultSet rs =  pst.executeQuery();

            while (rs.next()) {
                list.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

            if (args.length >= 1) {

                List<String> list = list(event.getGuild().getId());

                switch (args[0]) {

                    case "add":
                    case "set":
                        try {
                            if (event.getGuild().getVoiceChannelById(args[1]) != null) {
                                if (!list.contains(args[1])) {
                                    add(args[1], event.getGuild().getId());
                                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, String.format(":white_check_mark: Succesfully added %s to ignore list.", args[1]));
                                }else {
                                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, "This channel is already on the ignore-list.");
                                }
                            } else {
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                            }
                        } catch (NumberFormatException e) {
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                        }
                        break;

                    case "remove":
                    case "unset":
                        try {
                            if (event.getGuild().getVoiceChannelById(args[1]) != null) {
                                if (list.contains(args[1])) {
                                    remove(args[1]);
                                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, String.format(":white_check_mark: Succesfully removed %s to ignore list.", args[1]));
                                }else {
                                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, "This channel isn't even on the ignore-list.");
                                }
                            } else {
                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                            }
                        } catch (NumberFormatException e) {
                            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                        }

                        break;

                    case "list":
                        List<String> ls = list(event.getGuild().getId());

                        StringBuilder sb = new StringBuilder();
                        sb.append("**DVCbG ignore list:\n\n**");

                        if (!ls.isEmpty()) {
                            for (String str : ls) {
                                sb.append(":white_small_square: " + str);
                            }
                        } else {
                            sb.append("There are no ignored channels.");
                        }

                        MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.BLUE, sb.toString());

                        break;

                    default:
                        MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
                }
            } else {
                MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, help());
            }
        }else {
            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL, "You don't have permissions to do that.");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return String.format("**USAGE:**\n" +
                ":white_small_square:  `%s%s set <Chan ID>`  -  Set DVCbG-ignore channel\n" +
                ":white_small_square:  `%s%s unset <Chan ID>`  -  Unset DVCbG-ignore channel\n" +
                ":white_small_square:  `%s%s list`  -  Display all DVCbG-ignore channels\n",
                Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE);
    }
}
