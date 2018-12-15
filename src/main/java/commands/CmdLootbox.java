package commands;

import battle_of_discordia.Inventory;
import battle_of_discordia.Item;
import battle_of_discordia.Player;
import core.ServerSettingsHandler;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class CmdLootbox implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    private String getLootbox(Inventory inv, int rn) {
        String lootbox;

        if (rn <= 50) {
            inv.add(new Item(1), 1);
            lootbox = "common";
        } else if (rn <= 70) {
            inv.add(new Item(2), 1);
            lootbox = "uncommon";
        } else if (rn <= 85) {
            inv.add(new Item(3), 1);
            lootbox = "rare";
        } else if (rn <= 95) {
            inv.add(new Item(4), 1);
            lootbox = "epic";
        } else {
            inv.add(new Item(5), 1);
            lootbox = "legendary";
        }

        return lootbox;
    }

    private static Color getColorByLootbox(int rn) {

        if (rn <= 50) {
            return new Color(126, 126, 126);
        } else if (rn <= 70) {
            return new Color(23, 162, 63);
        } else if (rn <= 85) {
            return new Color(31, 73, 191);
        } else if (rn <= 95) {
            return new Color(186, 0, 161);
        } else {
            return new Color(228, 189, 36);
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        int rn = new Random().nextInt(101);

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (Player.hasEntry(user.getId())) {
            Player p = new Player(user.getId());
            Inventory inv = p.getInv();

            String dateTimeQuery = "SELECT datetimes FROM resets WHERE pid = ? AND reset = 'lootbox'";
            String insert = "INSERT INTO resets(reset, datetimes, pid) VALUES ('lootbox', ?, ?)";
            String update = "UPDATE resets SET datetimes = ? WHERE reset = 'lootbox' AND pid = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst0 = con.prepareStatement(dateTimeQuery);
                 PreparedStatement pst1 = con.prepareStatement(insert);
                 PreparedStatement pst2 = con.prepareStatement(update)){

                pst0.setString(1, p.getId());
                ResultSet rs = pst0.executeQuery();

                if(rs.next()) {
                    Duration dur = Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now());
                    if(dur.toMillis() >= 0) {

                        pst2.setString(1, LocalDateTime.now().plusDays(1).format(Config.formatter));
                        pst2.setString(2, p.getId());
                        pst2.executeUpdate();

                       MessageMask.msg(tc, user, getColorByLootbox(rn), String.format("You have obtained a %s lootbox", getLootbox(inv, rn)));


                    }else {
                        MessageMask.msg(tc, user, Color.RED, String.format("Wait %s houres to get your next lootbox!", Math.abs(dur.toHours())));
                    }
                }else {
                    pst1.setString(1, LocalDateTime.now().plusDays(1).format(Config.formatter));
                    pst1.setString(2, p.getId());
                    pst1.executeUpdate();

                    MessageMask.msg(tc, user, getColorByLootbox(rn), String.format("You have obtained a `%s lootbox`", getLootbox(inv, rn)));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else {
            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                    String.format("You first have to register a player with `%s%s register <playername>`", Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA));
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
