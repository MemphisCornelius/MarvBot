package commands;

import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.GitHubConnector;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class CmdVersion implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        switch (args.length) {
            case 0:
                MessageMask.msg(tc, user, Color.BLACK, String.format("JDA: %s\nJAVA: %s", JDAInfo.VERSION, System.getProperty("java.version")));
                break;
            case 1:
                switch (args[0]) {
                    case "JDA":
                    case "jda":
                        MessageMask.help(tc, user, String.format("JDA: %s", JDAInfo.VERSION));
                        break;
                    case "JAVA":
                    case "java":
                        MessageMask.help(tc, user, String.format("JAVA: %s", System.getProperty("java.version")));
                        break;
                }
                break;
            default:
                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                        String.format("Invalid arguments!\n" + "**Usage**\n" + ":white_small_square: `%s%s` <[jda/JDA] / [java/JAVA]>", Config.PREFIX, Config.CMD_VERSION));
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_VERSION.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
