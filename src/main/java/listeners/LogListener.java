package listeners;

import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Config;
import util.MessageMask;

import java.awt.Color;

public class LogListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                queue();

    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {

        event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER, true).get(0).delete().queue();

    }

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {

        try {
            MessageMask.log(event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER,
                    true).get(0),
                    event.getMember().getUser(),
                    Color.green,
                    "VCID: " + event.getChannelJoined().getId(),
                    "joined #" + event.getChannelJoined().getName());
        } catch (IndexOutOfBoundsException e) {
            event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                    queue();
        }
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        try {
            MessageMask.log(event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER,
                    true).get(0),
                    event.getMember().getUser(),
                    Color.yellow,
                    "VCID: " + event.getChannelJoined().getId(),
                    "moved from #" + event.getChannelLeft().getName() + " to #" + event.getChannelJoined().getName());
        } catch (IndexOutOfBoundsException e) {
            event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                    queue();
        }
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        try {
            MessageMask.log(event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER,
                    true).get(0),
                    event.getMember().getUser(),
                    Color.red,
                    "VCID: " + event.getChannelLeft().getId(),
                    "left #" + event.getChannelLeft().getName());
        } catch (IndexOutOfBoundsException e) {
            event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                    queue();
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        try {
            MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER,
                    true).get(0),
                    event.getMember().getUser(),
                    new Color(20, 90, 10),
                    "joined the guild");
        } catch (IndexOutOfBoundsException e) {
            event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                    queue();
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {

        try {

            MessageMask.msg(event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER,
                    true).get(0),
                    event.getMember().getUser(),
                    new Color(90, 10, 10),
                    "left the guild");
        } catch (IndexOutOfBoundsException e) {
            event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                    queue();
        }
    }
}