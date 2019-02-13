package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.Color;
import java.time.*;
import java.util.Timer;
import java.util.TimerTask;


public class CmdPoke implements Command {

    private EmbedBuilder done = new EmbedBuilder().setColor(Color.green).setTitle("Done!");


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length > 1 && args[0].startsWith("@") && !event.getMessage().getMentionedMembers().get(0).getUser().isBot()) {

            String content = event.getMessage().getContentDisplay();
            content = content.replaceFirst(Config.PREFIX , "");
            content = content.replaceFirst(Config.CMD_POKE, "");
            content = content.replaceFirst("@", "");
            content = content.replace(event.getMessage().getMentionedMembers().get(0).getEffectiveName(), "");
            String finalContent = content;

            event.getMessage().getMentionedMembers().get(0).getUser().openPrivateChannel().queue((channel) ->
                    channel.sendMessage(
                    new EmbedBuilder().setColor(Color.blue).setAuthor(
                            event.getMessage().getAuthor().getName() + " from #" + event.getGuild().getName(),
                            null, event.getMessage().getAuthor().
                            getEffectiveAvatarUrl()).setTitle("pokes you:").setDescription(
                            finalContent
                    ).setTimestamp(Instant.now()).setFooter("ID: " + event.getMessage().getAuthor().getId(), null).build()
            ).queue());

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    event.getMessage().delete().queue();
                }
            }, 500);


            net.dv8tion.jda.core.entities.Message msg = event.getTextChannel().sendMessage(
                    done.build()
            ).complete();

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    msg.delete().queue();
                }
            }, 5000);

        } else {

            MessageMask.msg(tc, user, Color.RED,Config.ERROR_THUMBNAIL, "Invalid arguments!\n\n\n" +
                    String.format("Usage:\n\n\\%s %s <User> <Message>\n", Config.PREFIX, Config.CMD_POKE));

        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_POKE.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
