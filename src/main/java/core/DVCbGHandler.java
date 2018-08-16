package core;

import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.requests.restaction.pagination.AuditLogPaginationAction;

import java.io.*;
import java.util.*;

public class DVCbGHandler extends ListenerAdapter {

    public static HashMap<String, String> vcNames = new HashMap<>();

    public static void save() {

        File path = new File("SERVER_SETTINGS/");
        if (!path.exists())
            path.mkdir();

        HashMap<String, String> out = new HashMap<>();


        vcNames.forEach((vcID, vcName) -> out.put(vcID, vcName));

        try {
            FileOutputStream fos = new FileOutputStream("SERVER_SETTINGS/vcNames.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void load() {
        File file = new File("SERVER_SETTINGS/vcNames.dat");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, String> out = (HashMap<String, String>) ois.readObject();
                ois.close();

                out.forEach((vcID, vcName) -> vcNames.put(vcID, vcName));

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
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

        for (Member m : members) {
            if (m.getGame() != null) {
                for (Member m1 : members) {
                    if (m1.getGame() != null) {
                        if (m.getUser().getId().equals(m1.getUser().getId())) {
                            if (m.getGame().getName().equalsIgnoreCase(m1.getGame().getName())) {
                                if (!games.containsKey(m.getGame().getName())) {
                                    games.put(m.getGame().getName(), 1);
                                } else {
                                    games.replace(m.getGame().getName(), games.get(m.getGame().getName()), games.get(m.getGame().getName() + 1));
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
        } catch (NoSuchElementException e) {
            return "";
        } catch (NullPointerException e) {
            return vc.getMembers().get(0).getGame().getName();
        }

        if (i > (members.size() / 2)) {
            if (getKeysFromValue(games, i).size() == 1) {
                return getKeysFromValue(games, i).get(0).toString();
            }
        }

        return "";
    }

    private static void setNameToGame(VoiceChannel vc) {
        if ((vc.getGuild().getAfkChannel() == null || !vc.getId().equals(vc.getGuild().getAfkChannel().getId())) && !getMostGame(vc).isEmpty()) {
            vc.getManager().setName(getMostGame(vc)).queue();
        } else {
            setNameToDefault(vc);
        }
    }

    private static void setNameToDefault(VoiceChannel vc) {
        vc.getManager().setName(vcNames.get(vc.getId())).queue();
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        for (VoiceChannel vc : event.getGuild().getVoiceChannels()) {
            vcNames.put(vc.getId(), vc.getName());
        }
        save();
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        vcNames.put(event.getChannel().getId(), event.getChannel().getName());
        save();
    }

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        vcNames.remove(event.getChannel().getId(), event.getChannel().getName());
        save();
    }

    @Override
    public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {

        AuditLogPaginationAction auditLogs = event.getGuild().getAuditLogs();
        auditLogs.type(ActionType.CHANNEL_UPDATE);
        auditLogs.limit(1); // take first
        auditLogs.queue((entries) -> {
            if (entries.isEmpty()) return;
            AuditLogEntry entry = entries.get(0);
            if (!entry.getUser().getId().equals(event.getJDA().getSelfUser().getId())) {
                vcNames.replace(event.getChannel().getId(), event.getOldValue(), event.getNewValue());
                save();
            }
        });
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        for (VoiceChannel vc : event.getGuild().getVoiceChannels()) {
            vcNames.remove(vc.getId(), vc.getName());
        }
        save();
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        setNameToGame(event.getChannelJoined());
    }

    @Override
    public void onUserUpdateGame(UserUpdateGameEvent event) {
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
