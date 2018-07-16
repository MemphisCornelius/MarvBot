package commands;

import listeners.VoicechatListener;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class CmdDebug implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length > 0) {

            switch (args[0]) {

                case "DVCbG":
                    MessageMask.msg(tc, user, Color.gray, VoicechatListener.getVoiceChannelName().toString());
                    break;
                default:
                    break;
            }
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println(" [COMMAND] " + Time.getTime() + Config.CMD_DEBUG.toUpperCase() + " was executed by " + event.getMessage().getAuthor());

    }

    @Override
    public String help() {
        return null;
    }
}
