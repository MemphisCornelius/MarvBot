package core;

import commands.CmdDVCbGIgnore;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivityOrderEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import util.Config;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.*;

public class DVCbGHandler extends ListenerAdapter {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();


    private static void put(String vcid, String vcname) {

        String sql = "INSERT INTO vcnames VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, vcid);
            pst.setString(2, vcname);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void remove(String vcid) {

        String sql = "DELETE FROM vcnames WHERE vcid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, vcid);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void replace(String vcid, String newValue) {

        String sql = "UPDATE vcnames SET vcname = ? WHERE vcid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, newValue);
            pst.setString(2, vcid);
            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private static String getName(String vcid) {

        String sql = "SELECT vcname FROM vcnames WHERE vcid = ?";

        try (Connection conn = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, vcid);
            ResultSet rs = pst.executeQuery();
            rs.next();

            return rs.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    private static List<Object> getKeysFromValue(Map<?, ?> hm, Object value) {
        List<Object> list = new ArrayList<>();
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                list.add(o);
            }
        }
        return list;
    }

    private static String getMostGame(VoiceChannel vc) {

        HashMap<String, Integer> games = new HashMap<>();
        List<Member> members = vc.getMembers();
        Collection<Integer> values;
        int i;

        if (members.size() == 1) {
                return vc.getMembers().get(0).getActivities().size() > 0 ? vc.getMembers().get(0).getActivities().get(0).getName() : "";
        }

        for (Member m : members) {
            if (m.getActivities().size() > 0) {
                for (Member m1 : members) {
                    if (m1.getActivities().size() > 0) {
                        if (!m.getUser().getId().equals(m1.getUser().getId())) {
                            if (m.getActivities().get(0).getName().equalsIgnoreCase(m1.getActivities().get(0).getName())) {
                                if (!games.containsKey(m.getActivities().get(0).getName())) {
                                    games.put(m.getActivities().get(0).getName(), 1);
                                } else {
                                    int newValue = games.get(m.getActivities().get(0).getName()) + 1;
                                    games.replace(m.getActivities().get(0).getName(), games.get(m.getActivities().get(0).getName()), newValue);
                                }
                            }
                        }
                    }
                }
            }
        }

        try {
            values = games.values();
            i = Collections.max(values);
        } catch (NoSuchElementException | NullPointerException e) {
            return "";
        }

        if (i > (members.size() / 2) && getKeysFromValue(games, i).size() == 1) {
            return getKeysFromValue(games, i).get(0).toString();
        }

        return "";
    }

    private static void setNameToGame(VoiceChannel vc) {

        List<Member> members = vc.getMembers();
        List<String> memberIDs = new ArrayList<>();

        for (Member m : members) {
            memberIDs.add(m.getUser().getId());
        }

        if ((memberIDs.size() == 2) && memberIDs.contains(Config.OWNERID) && memberIDs.contains(Config.ARTURID)) { //#martur
            vc.getManager().setName("#martur").queue();
        }else if (!getMostGame(vc).isEmpty() //checks if there is a most played game
                && !CmdDVCbGIgnore.getDVCbGIgnore().contains(vc.getId()) //checks if this vc is is not ignored
                && (vc.getGuild().getAfkChannel() == null || !vc.getId().equals(vc.getGuild().getAfkChannel().getId()))) /*checks if vc is not a afk-vc*/ {
            vc.getManager().setName(getMostGame(vc)).queue();
        } else {
            setNameToDefault(vc);
        }
    }

    private static void setNameToDefault(VoiceChannel vc) {
        if (!CmdDVCbGIgnore.getDVCbGIgnore().contains(vc.getId()))
            vc.getManager().setName(getName(vc.getId())).queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        for (VoiceChannel vc : event.getGuild().getVoiceChannels()) {
            put(vc.getId(), vc.getName());
        }
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        put(event.getChannel().getId(), event.getChannel().getName());
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        remove(event.getChannel().getId());
    }

    @Override
    public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {

        AuditLogPaginationAction auditLogs = event.getGuild().retrieveAuditLogs();
        auditLogs.type(ActionType.CHANNEL_UPDATE);
        auditLogs.limit(1);
        auditLogs.queue((entries) -> {
            if (entries.isEmpty()) return;
            AuditLogEntry entry = entries.get(0);

            if (!entry.getUser().getId().equals(event.getJDA().getSelfUser().getId()) ||
                    event.getNewName().contains("[AC]")) {
                replace(event.getChannel().getId(), event.getNewValue());
            }
        });
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        for (VoiceChannel vc : event.getGuild().getVoiceChannels()) {
            remove(vc.getId());
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        setNameToGame(event.getChannelJoined());
    }

    @Override
    public void onUserUpdateActivityOrder(@Nonnull UserUpdateActivityOrderEvent event) {
        if (event.getMember().getVoiceState().inVoiceChannel()) {
            setNameToGame(event.getMember().getVoiceState().getChannel());
        }
    }

    @Override
    public void onUserActivityStart(@Nonnull UserActivityStartEvent event) {
        if (event.getMember().getVoiceState().inVoiceChannel()) {
            setNameToGame(event.getMember().getVoiceState().getChannel());
        }
    }

    @Override
    public void onUserActivityEnd(@Nonnull UserActivityEndEvent event) {
        if (event.getMember().getVoiceState().inVoiceChannel()) {
            setNameToGame(event.getMember().getVoiceState().getChannel());
        }

    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        if (event.getChannelLeft().getMembers().size() > 0) {
            setNameToGame(event.getChannelLeft());
        } else {
            setNameToDefault(event.getChannelLeft());
        }
        setNameToGame(event.getChannelJoined());
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        if (event.getChannelLeft().getMembers().size() > 0) {
            setNameToGame(event.getChannelLeft());
        } else {
            setNameToDefault(event.getChannelLeft());
        }
    }

}
