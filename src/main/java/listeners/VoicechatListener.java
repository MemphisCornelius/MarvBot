package listeners;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;

public class VoicechatListener extends ListenerAdapter {

    private static HashMap<String, String> voiceChannelName = new HashMap<>();

    private boolean compareLists(List<Member> list1, List<Member> list2) {

        int i = 0;
        int halfSize = list1.size() / 2;
        //System.out.println("called compareLists");
        for (Member m : list1) {
            for (Member m1 : list2) {

                if (!m.getGame().getName().toLowerCase().equals(m1.getGame().getName().toLowerCase())) {
                    i = i - 1;
                }else {
                    i = i + 1;
                }
            }
        }

        return i > halfSize;

    }

    public static HashMap<String, String> getVoiceChannelName() {
        return voiceChannelName;
    }

    private void manageVoiceChannelname(VoiceChannel vc) {
        //System.out.println("called manageVoiceName");
        List<Member> members = vc.getMembers();

        try {
            if (vc.getGuild().getAfkChannel() == null || !vc.getId().equals(vc.getGuild().getAfkChannel().getId())) {
                if (compareLists(members, members)) {
                    vc.getManager().setName(vc.getMembers().get(0).getGame().getName()).queue();
                    //System.out.println("changed to game");
                } else {
                    vc.getManager().setName(voiceChannelName.get(vc.getId())).queue();
                    //System.out.println("changed to default");
                }
            }
        } catch (NullPointerException e) {
            vc.getManager().setName(voiceChannelName.get(vc.getId())).queue();
        } catch (IndexOutOfBoundsException  e) {
        }
    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        //System.out.println("called voice join");
        VoiceChannel vc = event.getChannelJoined();
        if (!voiceChannelName.containsKey(vc.getId())) {
            voiceChannelName.put(vc.getId(), vc.getName());
        }
        manageVoiceChannelname(event.getChannelJoined());
    }

    @Override
    public void onUserUpdateGame(UserUpdateGameEvent event) {

        if (event.getMember().getVoiceState().inVoiceChannel()) {
            //System.out.println("called gameUpdate");
            manageVoiceChannelname(event.getMember().getVoiceState().getChannel()); //TODO: ?fix "causes IndexOutOfBoundsException: Index 0 out-of-bounds for length 0 in manageVoiceChannelname() in try"
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {

        //System.out.println("called voice move");

        VoiceChannel vcl = event.getChannelLeft();
        VoiceChannel vcj = event.getChannelJoined();

        if (vcl.getMembers().size() < 1) {
            //System.out.println("called reset vcName");
            vcl.getManager().setName(voiceChannelName.get(vcl.getId())).queue(); //IllegalArgumentException: Name may not be null (fixed?)
            voiceChannelName.remove(vcl.getId());
        } else {
            if (!voiceChannelName.containsKey(vcl.getId())) {
                voiceChannelName.put(vcl.getId(), vcl.getName());
            }
        }
        manageVoiceChannelname(vcl);
        manageVoiceChannelname(vcj);

        if (!voiceChannelName.containsKey(vcj.getId())) {
            voiceChannelName.put(vcj.getId(), vcj.getName());
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

        //System.out.println("called voice leave");
        VoiceChannel vc = event.getChannelLeft();
        if (event.getChannelLeft().getMembers().size() < 1) {
            //System.out.println("called reset vcName");
            vc.getManager().setName(voiceChannelName.get(vc.getId())).queue(); //IllegalArgumentException: Name may not be null (fixed?)
            voiceChannelName.remove(vc.getId());
        } else {
            manageVoiceChannelname(event.getChannelLeft());
        }
    }
}
