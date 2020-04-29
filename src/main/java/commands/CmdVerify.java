package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.Time;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

public class CmdVerify implements Command {
    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {
        event.getGuild().getController().addRolesToMember(
                event.getMember(), event.getGuild().getRoleById(CmdSet.configList.get(event.getGuild().getId()).get("verify"))).queue();
        event.getMessage().delete().queueAfter(5, TimeUnit.SECONDS);

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_VERIFY.toUpperCase() + " was executed by "
                + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
