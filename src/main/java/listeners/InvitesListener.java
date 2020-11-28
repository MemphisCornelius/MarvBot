package listeners;

import core.ServerSettingsHandler;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static commands.CmdSet.configList;

public class InvitesListener extends ListenerAdapter {

    private static final String url = ServerSettingsHandler.getDBURL();
    private static final String usr = ServerSettingsHandler.getDBUS();
    private static final String pw = ServerSettingsHandler.getDBPW();
    public static HashMap<String, HashMap<String, Integer>> memberInvites = new HashMap<>();

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        String gid = event.getGuild().getId();

        try {
            Connection conn = DriverManager.getConnection(url, usr, pw);
            Statement stmt = conn.createStatement();

            List<Invite> invites = event.getGuild().retrieveInvites().complete();
            HashMap<String, Integer> tmp = new HashMap<>();

            for (Invite inv : invites) {
                if (inv.getMaxAge() <= 0) {
                    if (tmp.containsKey(inv.getInviter().getId()))
                        tmp.put(inv.getInviter().getId(), tmp.get(inv.getInviter().getId()) + inv.getUses());
                    else
                        tmp.put(inv.getInviter().getId(), inv.getUses());
                }
            }

            if (!memberInvites.containsKey(gid))
                memberInvites.put(gid, new HashMap<>());

            if (!tmp.equals(memberInvites.get(gid))) {

                Map<String, Boolean> comp = areEqualKeyValues(tmp, memberInvites.get(gid));
                for (String key : comp.keySet()) {
                    if (!comp.get(key)) {
                        stmt.execute("INSERT INTO inviteTable(gid, uid, value) VALUES(" +
                                gid + ", " + key + ", " + memberInvites.get(gid).get(key) + ")" +
                                " ON DUPLICATE KEY UPDATE value = " + tmp.get(key));
                    }
                }
            }

            memberInvites.put(gid, tmp);
        } catch (InsufficientPermissionException | SQLException e) {
        }

        if (configList.containsKey(gid) && configList.get(gid).containsKey("inviteRole") && configList.get(gid).containsKey("inviteNumber")) {
            for (String key : memberInvites.get(gid).keySet()) {
                if (memberInvites.get(gid).get(key) >= Integer.parseInt(configList.get(gid).get("inviteNumber")) && event.getGuild().getRoleById(configList.get(gid).get("inviteRole")) != null) {
                    event.getGuild().addRoleToMember(key, event.getGuild().getRoleById(configList.get(gid).get("inviteRole"))).queue();
                }
            }
        }
    }


    private Map<String, Boolean> areEqualKeyValues(Map<String, Integer> first, Map<String, Integer> second) {
        return first.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(),
                        e -> e.getValue().equals(second.get(e.getKey()))));
    }

    public static void load() {

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             Statement st = con.createStatement()) {

            ResultSet rs = st.executeQuery("SELECT  gid, uid, value FROM inviteTable");

            while (rs.next()) {
                String gid = rs.getString(1);
                String uid = rs.getString(2);
                int value = rs.getInt(3);

                if (!memberInvites.containsKey(gid))
                    memberInvites.put(gid, new HashMap<>());
                memberInvites.get(gid).put(uid, value);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
