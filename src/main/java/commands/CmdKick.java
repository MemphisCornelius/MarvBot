package commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class CmdKick implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        if (event.getMember().hasPermission(Permission.ADMINISTRATOR) || event.getMember().hasPermission(Permission.KICK_MEMBERS) ||
                event.getMember().getRoles().contains(
                        event.getGuild().getRoleById(
                                CmdSet.configList.get(event.getGuild().getId()).get("moderation")))) {

            TextChannel tc = event.getTextChannel();
            User user = event.getAuthor();

            try {
                Member kick = event.getMessage().getMentionedMembers().get(0);

                String content = event.getMessage().getContentDisplay();
                content = content.replaceFirst(Config.PREFIX, "");
                content = content.replaceFirst(Config.CMD_KICK, "");
                content = content.replaceFirst("@", "");
                content = content.replace(event.getMessage().getMentionedMembers().get(0).getEffectiveName(), "");
                String finalContent = content.trim();

                if (!finalContent.isEmpty()) {

                    event.getGuild().kick(kick, content).queue();

                    try {
                        MessageMask.log((CmdSet.configList.containsKey(event.getGuild().getId()) && CmdSet.configList.get(event.getGuild().getId()).containsKey("modlog") ?
                                        event.getGuild().getTextChannelById(CmdSet.configList.get(event.getGuild().getId()).get("modlog")) :
                                        event.getGuild().getTextChannelsByName(Config.CHANNEL_LOG_LISTENER, true).get(0)),
                                kick.getUser(), Color.WHITE, "by " + user.getName(),
                                "was kicked for \n ```" + content + "```");
                    } catch (IndexOutOfBoundsException e) {
                        event.getGuild().createTextChannel(Config.CHANNEL_LOG_LISTENER).setTopic("This is the log-channel for the <@388355915583324160>.").
                                queue();
                    }

                } else {
                    MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                }

            } catch (IndexOutOfBoundsException e) {
                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
            } catch (HierarchyException ex) {
                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You cant kick this user because of insufficient permissons!");
            }
        } else {
            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.RED, Config.ERROR_THUMBNAIL,
                    "ERROR!\nYou do nat have the permissons to do this");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_KICK.toUpperCase() + " was executed by "
                + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return String.format("***USAGE***" +
                "\n:white_small_square: `%s%s <user> <reason>` - Kicks the member from the server.", Config.PREFIX, Config.CMD_KICK);
    }
}
