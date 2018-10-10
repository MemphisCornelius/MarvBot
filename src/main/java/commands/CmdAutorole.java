package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CmdAutorole implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();


    private static void add(String rid, String gid) {

        String sql = "INSERT INTO autoroles VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, rid);
            pst.setString(2, gid);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void remove(String rid) {

        String sql = "DELETE FROM autoroles WHERE rid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, rid);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasREntry(String rid) {

        String sql = "SELECT * FROM autoroles WHERE rid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, rid);
            ResultSet rs = pst.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean hasGEntry(String gid) {

        String sql = "SELECT * FROM autoroles WHERE rid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, gid);
            ResultSet rs = pst.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static List<String> list(String gid) {

        List<String> rlist = new ArrayList<>();
        String sql = "SELECT rid FROM autoroles WHERE gid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, gid);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                rlist.add(rs.getString(1));
            }

            return rlist;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getAutoroles(String gid) {
        return list(gid);
    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        String g = event.getGuild().getId();
        User user = event.getAuthor();
        Member member = event.getMember();
        TextChannel tc = event.getTextChannel();
        String rid;

        if (!member.hasPermission(Permission.ADMINISTRATOR)) {
            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                    "ERROR!\n\n You do not have the permisson to do that!");
        }else {

            if (args.length > 0) {

                switch (args[0]) {
                    case "set":
                    case "add":
                        try {
                            rid = event.getMessage().getMentionedRoles().get(0).getId();
                            if (!hasREntry(rid)) {
                                MessageMask.msg(tc, user, Color.GREEN, ":white_check_mark: Successfully set role as autorole.");
                                add(rid, g);
                            } else {
                                MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                        "ERROR!\n\n This role is already a autorole!");
                            }
                        } catch (IndexOutOfBoundsException e) {
                            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                    "ERROR!\n\nYou have to name the role you want to add!");
                        }
                        break;

                    case "unset":
                    case "delete":
                    case "remove":
                        if (hasGEntry(g)) {
                            try {
                                rid = event.getMessage().getMentionedRoles().get(0).getId();
                                if (hasREntry(rid)) {
                                    MessageMask.msg(tc, user, Color.GREEN, ":white_check_mark: Successfully unset role as autorole. ");
                                    remove(rid);
                                } else {
                                    MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                            "ERROR!\n\nThis role isn`t a autorole!");
                                }
                            } catch (IndexOutOfBoundsException e) {
                                MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                        "ERROR!\n\nYou have to name the role you want to remove!");
                            }
                        } else {
                            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                    "ERROR!\n\nThere is no role you can remove!");
                        }

                        break;

                    case "show":
                    case "list":

                        StringBuilder sb = new StringBuilder();
                        List<String> list = list(g);

                        sb.append("All autololes:\n\n");

                        if (list != null) {
                            if (!list.isEmpty()) {
                                for (String r : list) {
                                    sb.append(":white_small_square: ");
                                    sb.append(event.getGuild().getRoleById(r).getAsMention());
                                    sb.append("\n");
                                }
                            }else {
                                sb.append("There is no autorole");
                            }

                        }else {
                            sb.append("Whoooops\nSometing went wrong!");
                        }



                        MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.BLUE, sb.toString());

                        break;

                    default:
                        MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                "ERROR!\n\nYou have to use one of these keywords: set, add, uset, remove, delete, show list!\n\nFor more information use " +
                                        Config.PREFIX + Config.CMD_HELP + " " + Config.CMD_AUTOROLE);
                }
            } else {
                MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                        "ERROR! \n\nInvalid arguments!\nUse `" + Config.PREFIX + Config.CMD_HELP + " " + Config.CMD_AUTOROLE + "` to get more information about it. ");
            }
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_AUTOROLE.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
