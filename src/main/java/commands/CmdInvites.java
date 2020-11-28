package commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

import static listeners.InvitesListener.memberInvites;

public class CmdInvites implements Command{


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {
        if(memberInvites.containsKey(event.getGuild().getId()) && memberInvites.get(event.getGuild().getId()).containsKey(event.getAuthor().getId()))
            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.getHSBColor(269, 83, 24), "You have **" + memberInvites.get(event.getGuild().getId()).get(event.getAuthor().getId())
                    + "** invites on this server!");
        else
            MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.getHSBColor(269, 83, 24), "You have **0"
                    + "** invites on this server! || or something went wrong ||");
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_INVITES.toUpperCase() + " was executed by "
                + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
