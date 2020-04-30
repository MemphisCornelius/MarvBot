package commands;

import core.ServerSettingsHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.Color;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.HashMap;

/**
 * Created by zekro on 08.09.2017 / 20:25
 * DiscordBot.commands.guildAdministration
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */

public class CmdAutochannel implements Command, Serializable {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    // Hier werden die Autochannels mit der dazugehörigen Guild registriert
    private static HashMap<VoiceChannel, Guild> autochans = new HashMap<>();

    public static HashMap<String, String> autoChanName = new HashMap();

    // Getter für Autochannel register
    public static HashMap<VoiceChannel, Guild> getAutochans() {
        return autochans;
    }

    // Getter für VoiceChannel by ID
    private static VoiceChannel getVchan(String id, Guild g) {
        return g.getVoiceChannelById(id);
    }

    // Getter für Guild by ID
    private static Guild getGuild(String id, JDA jda) {
        return jda.getGuildById(id);
    }

    // Sender für Error Messages
    private void error(TextChannel tc, String content) {
        tc.sendMessage(new EmbedBuilder().setColor(Color.red).setDescription(content).build()).queue();
    }

    // Sender für normale Messages
    private void msg(TextChannel tc, String content) {
        tc.sendMessage(new EmbedBuilder().setColor(Color.green).setDescription(content).build()).queue();
    }

    /*
        Wenn der VC nicht "null" und noch nicht im Register ist,
        dann wird dieser dem hinzugefügt und in die Save File gespeichert.
    */

    private void setChan(String vcid, Guild g, TextChannel tc) {
        VoiceChannel vc = getVchan(vcid, g);

        if (vc == null) {
            error(tc, String.format("Voice channel with the ID `%s` does not exist.", vcid));
        } else if (autochans.containsKey(vc)) {
            error(tc, "This channel is already set as an auto channel.");
        } else {
            autochans.put(vc, g);

            msg(tc, String.format("Successfully set voice channel `%s` as auto channel.", vc.getName()));

            CmdDVCbGIgnore.add(vcid, g.getId());

            String sql = "INSERT INTO autoChan VALUES (?, ?)";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, vc.getId());
                pst.setString(2, g.getId());

                pst.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void setName(String vcid, Guild g, TextChannel tc, String name) {

        VoiceChannel vc = getVchan(vcid, g);

        if (vc == null || !autochans.containsKey(vc)) {
            error(tc, String.format("Voice channel with the ID `%s` is not a autochannel.", vcid));
        }else {
            if(name.length() < 1 || name.length()> 95) {
                error(tc, String.format("The name must be in range of 1 and 95!"));
            }

            autoChanName.put(vcid, name);

            String sql = "UPDATE autoChan SET name = ? WHERE vcid = ? AND gid = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, name);
                pst.setString(2, vcid);
                pst.setString(3, g.getId());
                pst.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            msg(tc, String.format("Successfully set auto channel name to `%s` of voice channel `%s` as auto channel.", name, vc.getName()));
        }
    }

    private void unsetName(String vcid, Guild g, TextChannel tc) {

        VoiceChannel vc = getVchan(vcid, g);

        if (vc == null) {
            error(tc, String.format("Voice channel with the ID `%s` does not exist.", vcid));
        }else {

            autoChanName.remove(vcid);
            msg(tc, String.format("Successfully removed auto channel name of voice channel `%s` as auto channel.", vc.getName()));

            String sql = "UPDATE autoChan SET name = NULL WHERE vcid = ? AND gid = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, vcid);
                pst.setString(2, g.getId());
                pst.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    /*
        Wenn der VC nicht "null" ist und im Register eingetragen ist,
        dann wird dieser aus dem Register entfernt und in die Save File geschrieben.
    */
    private void unsetChan(String id, Guild g, TextChannel tc) {
        VoiceChannel vc = getVchan(id, g);

        if (vc == null) {
            error(tc, String.format("Voice channel with the ID `%s` does not exist.", id));
        } else if (!autochans.containsKey(vc)) {
            error(tc, String.format("Voice channel `%s` is not set as auto channel.", vc.getName()));
        } else {
            autochans.remove(vc);

            msg(tc, String.format("Successfully unset auto channel state of `%s`.", vc.getName()));

            CmdDVCbGIgnore.remove(id);

            String sql = "DELETE FROM autoChan WHERE vcid = ? AND gid = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst = con.prepareStatement(sql)) {

                pst.setString(1, vc.getId());
                pst.setString(2, g.getId());

                pst.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /*
        Selbes wie oben für den Autochannel Hanlder, wenn der VC des
        Autochannels gelöscht wird.
    */
    public static void unsetChan(VoiceChannel vc) {
        autochans.remove(vc);
        CmdDVCbGIgnore.remove(vc.getId());

        String sql = "DELETE FROM autoChan WHERE vcid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, vc.getId());

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
        Nimmt alle VoiceChannels aus dem Register mit der Guild, von der
        aus der Command ausgeführt wurde und sendet sie als Embed Message.
    */
    private void listChans(Guild guild, TextChannel tc) {
        StringBuilder sb = new StringBuilder().append("**Auto Channel**\n\n");
        autochans.keySet().stream()
                .filter(c -> autochans.get(c).equals(guild))
                .forEach(c -> sb.append(String.format(":white_small_square:   `%s` *(%s)*\n", c.getName(), c.getId())));
        tc.sendMessage(new EmbedBuilder().setDescription(sb.toString()).build()).queue();
    }


    /*
        Läd das Register mit Guild-Getter und VC-Getter aus der Save File,
        wenn diese existiert.
    */
    public static void load(JDA jda) {
        try (Connection con = DriverManager.getConnection(url, usr, pw);
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery("SELECT vcid, gid, name FROM autoChan");

            while (rs.next()) {
                String vcid = rs.getString(1);
                String gid = rs.getString(2);
                String name = rs.getString(3);
                VoiceChannel vc = jda.getVoiceChannelById(vcid);

                if (vc != null) {

                autochans.put(vc, jda.getGuildById(gid));
                autoChanName.put(vcid, name);
            }
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

        Guild g = event.getGuild();
        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (event.getMember().hasPermission(event.getTextChannel(), Permission.ADMINISTRATOR)) {

            // Help Message, wenn keine Channel ID angegeben ist.
            if (args.length < 1) {
                error(tc, help());
                return false;
            }

            switch (args[0]) {

                case "list":
                case "show":
                    listChans(g, tc);
                    break;
                case "add":
                case "set":
                    // Nimmt "set"/"add" aus den Arguments und übergibt nur die VC ID als Argument.
                    if (args.length < 2) {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                    } else
                        setChan(args[1], g, tc);
                    break;
                case "name":
                    if (args.length < 3) {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                    } else {

                        String name = "";
                        for (int i = 2; i < args.length; i ++) { name += args[i] + " ";}
                        setName(args[1], g, tc, name.trim());
                    }
                    break;
                case "unname":
                    if (args.length < 2) {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                    } else {
                        unsetName(args[1], g, tc);
                    }
                    break;

                case "remove":
                case "delete":
                case "unset":
                    // Selbes wie in Z. 194
                    if (args.length < 2)
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                    else
                        unsetChan(args[1], g, tc);
                    break;
                default:
                    // Wen ein nicht definiertes ARguemnt angegeben wurde wird eine Help MSG ausgegeben.
                    MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
            }
        } else {
            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                    "Missing permissons!\n\n\nYou need administrator-permissons to execute this command!");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_AUTOCHAN.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return String.format("**USAGE:**\n" +
                        ":white_small_square:  `%s%s set <Channel ID>`  -  Set voice chan as auto channel\n" +
                        ":white_small_square:  `%s%s unset <Channel ID>`  -  Unset voice chan as auto chan\n" +
                        ":white_small_square:  `%s%s name <Channel ID> <name>`  -  Set name of autochannel\n" +
                        ":white_small_square:  `%s%s unname <Channel ID>`  -  Unset name of autochannel\n" +
                        ":white_small_square:  `%s%s list`  -  Display all registered auto chans\n",
                Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN);
    }
}
