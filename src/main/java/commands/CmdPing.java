package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.Color;

public class CmdPing implements Command {

    private Color getColorByPing(long ping) {
        if (ping < 100)
            return Color.green;
        if (ping < 400)
            return Color.yellow;
        if (ping < 700)
            return Color.orange;
        if (ping < 1000)
            return Color.magenta;
        return Color.red;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        long ping = event.getJDA().getPing();

        MessageMask.msg(event.getTextChannel(), event.getAuthor(), getColorByPing(ping), String.format(":ping_pong:   **Pong!**\n\nThe ping is `%s` ms.",
                ping));

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_PING.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
