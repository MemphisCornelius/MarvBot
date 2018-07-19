package listeners;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import core.CommandHandler;
import util.Config;
import util.MessageMask;

import java.awt.*;
import java.io.IOException;
import java.text.ParseException;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getMessage().getContentDisplay().startsWith(Config.PREFIX) && !event.getMessage().getAuthor().getId().equals(event.getJDA().getSelfUser().getId())) {
            try {
                CommandHandler.handleCommand(CommandHandler.parser.parse(event.getMessage().getContentDisplay(), event));
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
    }
}