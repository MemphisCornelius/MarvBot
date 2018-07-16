package commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class CmdRestart implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (event.getAuthor().getId().equals(Config.OWNERID)){

           System.exit(0);

        }else {
            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                    "You don't have permissons to do that!");
        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println(" [COMMAND] " + Time.getTime() + Config.CMD_RESTART.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
