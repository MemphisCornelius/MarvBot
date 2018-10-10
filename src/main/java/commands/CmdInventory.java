package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;

public class CmdInventory implements Command {

    private int[] getNumbers(User user) {
        String url = ServerSettingsHandler.getDBURL();
        String usr = ServerSettingsHandler.getDBUS();
        String password = ServerSettingsHandler.getDBPW();
        String sql = "SELECT n_c, n_u, n_r, n_e, n_l " +
                " FROM lootboxes " +
                " WHERE uid=" + user.getId();
        int n[] = new int[5];

        try (Connection conn = DriverManager.getConnection(url, usr, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            rs.next();
            for (int i = 0; i <= 4; i++){

               n[i] = Integer.valueOf(rs.getString(i + 1));
            }

            return n;

        }catch (SQLException ex) {
            System.out.println(String.format("[%s] %s", ex.getErrorCode(), ex.getMessage()));
            return null;
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        User user;

        if (args.length > 0) {

            try {
                user = event.getMessage().getMentionedMembers().get(0).getUser();
            }catch (IndexOutOfBoundsException e) {
                user = event.getMember().getUser();
            }
        }else {
            user = event.getMember().getUser();
        }

        int n[] = getNumbers(user);

        if (n != null) {

            String msg = "";

            if (n[0] > 0)
                msg = msg + "common lootbox(es): " + n[0];
            if (n[1] > 0)
                msg = msg + "\nuncommon lootbox(es): " + n[1];
            if (n[2] > 0)
                msg = msg + "\nrare lootbox(es): " + n[2];
            if (n[3] > 0)
                msg = msg + "\nepic lootbox(es): " + n[3];
            if (n[4] > 0)
                msg = msg + "\nlegendary lootbox(es): " + n[4];

            MessageMask.msg(event.getTextChannel(), user, Color.GREEN, "**INVENTORY**" +
                    "\n\n" + msg);

        }else
            MessageMask.msg(event.getTextChannel(), user, Color.GREEN, "**INVENTORY**" +
                    "\n\nYou have no items in your inventory");

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_INVENTORY.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
