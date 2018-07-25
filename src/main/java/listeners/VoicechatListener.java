package listeners;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;

public class VoicechatListener extends ListenerAdapter {

    private static HashMap<String, String> voiceChannelName = new HashMap<>();
    private HashMap<String, Integer> gameNumbers = new HashMap<>();

    public static HashMap<String, String> getVoiceChannelName() {
        return voiceChannelName;
    }

    private boolean compareLists(List<Member> list1, List<Member> list2) {

        int halfSize = list1.size() / 2;
        //System.out.println("called compareLists");
        for (Member m : list1) {
            for (Member m1 : list2) {


                if (m.getGame().getName().toLowerCase().equals(m1.getGame().getName().toLowerCase())) {
                    if (!gameNumbers.containsKey(m.getGame().getName().toLowerCase())) {
                        gameNumbers.put(m.getGame().getName().toLowerCase(), 1);
                    }else {
                        gameNumbers.replace(m.getGame().getName().toLowerCase(), gameNumbers.get(m.getGame().getName().toLowerCase()),gameNumbers.get(m.getGame().getName().toLowerCase() + 1));
                    }
                }
            }
        }

        Collection<Integer> values = gameNumbers.values();
        int i = Collections.max(values);

        return i > halfSize;

    }

    private static List<Object> getKeysFromValue(Map<?, ?> hm, Object value){
        List <Object>list = new ArrayList<>();
        for(Object o : hm.keySet()){
            if(hm.get(o).equals(value)) {
                list.add(o);
            }
        }
        return list;
    }

    private String getGame() {

        Collection<Integer> values = gameNumbers.values();
        int i = Collections.max(values);

        List games = getKeysFromValue(gameNumbers, i);

        if (games != null && games.size() == 1) {
            return games.get(0).toString();
        }else {
            return null;
        }
    }

    private void manageVoiceChannelname(VoiceChannel vc) {
        //System.out.println("called manageVoiceName");
        List<Member> members = vc.getMembers();

        try {
            if (vc.getGuild().getAfkChannel() == null || !vc.getId().equals(vc.getGuild().getAfkChannel().getId())) {
                if (compareLists(members, members)) {
                    if (getGame() != null) {
                        vc.getManager().setName(vc.getMembers().get(0).getGame().getName()).queue();
                        //System.out.println("changed to game");
                    }else {
                        vc.getManager().setName(voiceChannelName.get(vc.getId())).queue();
                        //System.out.println("changed to default");
                    }

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
            manageVoiceChannelname(event.getMember().getVoiceState().getChannel()); //?fix "causes IndexOutOfBoundsException: Index 0 out-of-bounds for length 0 in manageVoiceChannelname() in try"
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
