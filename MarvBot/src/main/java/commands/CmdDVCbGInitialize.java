package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CmdDVCbGInitialize implements Command {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();
    private static Map<String, String> vcNames = new HashMap<>();


    private static void insertDatabase() {

        String sql = "INSERT INTO vcnames VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            for (Map.Entry<String, String> entry : vcNames.entrySet()) {

                pst.setString(1, entry.getKey());
                pst.setString(2, entry.getValue());
                pst.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        if (event.getAuthor().getId().equals(Config.OWNERID)) {

            for (Guild g : event.getJDA().getGuilds()) {
                for ( VoiceChannel vc : g.getVoiceChannels()) {

                    vcNames.put(vc.getId(), vc.getName());
                }
            }

            insertDatabase();

            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.GREEN, ":white_check_mark: Done!");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {

    }

    @Override
    public String help() {
        return null;
    }
}
