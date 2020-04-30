package core;

import commands.CmdAutochannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zekro on 08.09.2017 / 20:21
 * DiscordBot.listeners
 * dev.zekro.de - github.zekro.de
 * © zekro 2017
 */

public class AutochannelHandler extends ListenerAdapter {

    // Liste aller aktiven, erstellen Tempchannels
    List<VoiceChannel> active = new ArrayList<>();


    /*
        Wenn jemand einem VC joint und dieser im Autochannel Register ist
        wird ein VC mit <VC-Name + "[AC]"> erstellt und unter den Autochannel
        geschoben. Dannach wird der Member in den Tempchannel gemoved.
    */
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutochannel.getAutochans();
        VoiceChannel vc = event.getChannelJoined();
        Guild g = event.getGuild();

        if (autochans.containsKey(vc)) {

            String name;
            if (!CmdAutochannel.autoChanName.containsKey(vc.getId()) || CmdAutochannel.autoChanName.get(vc.getId()) == null) {
               name = (vc.getName() + " [AC]");
            }else {
                name = (CmdAutochannel.autoChanName.get(vc.getId())+ " [AC]");
            }

            vc.createCopy().setName(name).queue((nvc -> {

                if (!CmdAutochannel.autoChanName.containsKey(vc.getId()) || CmdAutochannel.autoChanName.get(vc.getId()) == null) {
                    nvc.getManager().setName(vc.getName() + " [AC]").queue();
                }else {
                    nvc.getManager().setName(CmdAutochannel.autoChanName.get(vc.getId())+ " [AC]").queue();
                }

                g.modifyVoiceChannelPositions().selectPosition((VoiceChannel) nvc).moveTo(vc.getPosition() + 1).queue();
                g.moveVoiceMember(event.getMember(), (VoiceChannel) nvc).queue();
                active.add((VoiceChannel) nvc);
            }));
        }
    }

    /*
        Wenn der geleavedte Channel in der Tempchannel Liste steht UND
        sich kein anderer Member mehr in dem Channel befindet, so wird dieser
        gelöscht und aus der Tempchannel Liste entfernt.
    */
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        VoiceChannel vc = event.getChannelLeft();

        if (active.contains(vc) && vc.getMembers().size() == 0) {
            active.remove(vc);
            vc.delete().queue();
        }
    }

    /*
        Beim joinen des Channels nach dem Moven:
        -> Selbes wie bei <onGuildVoiceJoin()>

        Beim leaven des Channels nach dem Moven:
        -> Selbes wie bei <onGuildVoiceLeave()>
    */
    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutochannel.getAutochans();
        Guild g = event.getGuild();

        VoiceChannel vc = event.getChannelJoined();

        if (autochans.containsKey(vc)) {

            String name;
            if (!CmdAutochannel.autoChanName.containsKey(vc.getId()) || CmdAutochannel.autoChanName.get(vc.getId()) == null) {
                name = (vc.getName() + " [AC]");
            }else {
                name = (CmdAutochannel.autoChanName.get(vc.getId())+ " [AC]");
            }

            VoiceChannel finalVc = vc;
            vc.createCopy().setName(name).queue((nvc -> {

                g.modifyVoiceChannelPositions().selectPosition((VoiceChannel) nvc).moveTo(finalVc.getPosition() + 1).queue();
                g.moveVoiceMember(event.getMember(), (VoiceChannel) nvc).queue();
                active.add((VoiceChannel) nvc);
            }));
        }

        vc = event.getChannelLeft();

        if (active.contains(vc) && vc.getMembers().size() == 0) {
            active.remove(vc);
            vc.delete().queue();
        }
    }

    /*
        Wenn sich der gelöschte Channel im Autochannel Register befindet, dann wird
        nach dem Löschen der Autochannel aus dem Register entfernt und dieses
        gespeichert in der Save File.
    */
    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        HashMap<VoiceChannel, Guild> autochans = commands.CmdAutochannel.getAutochans();
        if (autochans.containsKey(event.getChannel())) {
            commands.CmdAutochannel.unsetChan(event.getChannel());
        }
    }
}