package listeners;

import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Config;
import util.MessageMask;

import java.awt.Color;

public class LogListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        //event.getGuild().getController().createRole().setName("MarvBot-Mod").complete();

        event.getGuild().getController().createTextChannel("log").setTopic("This is the log-channel for the <@388355915583324160>.").
                complete();

    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {

        event.getGuild().getTextChannelsByName("log", true).get(0).delete().queue();

    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

        MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_VOICE_LISTENER,
                true).get(0),
                event.getMember().getUser(),
                Color.green,
                "joined #" + event.getChannelJoined().getName());

    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {

        MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_VOICE_LISTENER,
                true).get(0),
                event.getMember().getUser(),
                Color.yellow,
                "moved from #" + event.getChannelLeft().getName() + " to #" + event.getChannelJoined().getName());

    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {

        MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_VOICE_LISTENER,
                true).get(0),
                event.getMember().getUser(),
                Color.red,
                "left #" + event.getChannelLeft().getName());

    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_GUILD_LISTENER,
                true).get(0),
                event.getMember().getUser(),
                new Color(20, 90, 10),
                "joined the guild");

    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

        MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_GUILD_LISTENER,
                true).get(0),
                event.getMember().getUser(),
                new Color(90, 10, 10),
                "left the guild");

    }
}