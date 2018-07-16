package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Timer;
import java.util.TimerTask;

public class CmdBugreport implements Command {

    private EmbedBuilder done = new EmbedBuilder().setColor(Color.green).setTitle("Done!");

    private static boolean isInteger(String s) {
        return isInteger(s, 10);
    }

    private static boolean isInteger(String s, int radix) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (i == 0 && s.charAt(i) == '-') {
                if (s.length() == 1) return false;
                else continue;
            }
            if (Character.digit(s.charAt(i), radix) < 0) return false;
        }
        return true;
    }

    private Color getColorByDegree(String i) {
        switch (i) {
            case "1":
                return Color.green;
            case "2":
                return Color.yellow;
            case "3":
                return Color.orange;
            case "4":
                return Color.red;
            default:
                return Color.black;
        }
    }

    private String getStringByDegree(String s) {
        switch (s) {
            case "1":
                return "trivial";
            case "2":
                return "minor";
            case "3":
                return "major";
            case "4":
                return "critical";
            default:
                return "undefined";
        }
    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length > 1 && isInteger(args[0])) {

            int priorityLevel = Integer.parseInt(args[0]);

            if (priorityLevel >= 1 && priorityLevel <= 4) {

                String content = event.getMessage().getContentDisplay();
                content = content.replaceFirst(Config.PREFIX, "");
                content = content.replaceFirst(Config.CMD_BUGREPORT, "");
                content = content.replaceFirst(args[0], "");
                String finalContent = content;

                event.getJDA().getUserById(Config.OWNERID).openPrivateChannel().queue((channel) -> {
                    channel.sendMessage(
                            new EmbedBuilder().setColor(getColorByDegree(args[0])).setAuthor(event.getMessage().getAuthor().getName() + " from #" + event.getGuild().getId(), null, event.getMessage().getAuthor().
                                    getEffectiveAvatarUrl()).setTitle(String.format("found following %s bug:", getStringByDegree(args[0]))).
                                    setDescription(finalContent).
                                    setTimestamp(OffsetDateTime.of(LocalDate.now(), LocalTime.now(), ZoneOffset.UTC)).
                                    setFooter("ID: " + event.getMessage().getAuthor().getId(), null).build()
                    ).queue();
                });

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
                MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833", "Invalid arguments!\n" +
                        "Priority levels: 1 - trivial; 2 - minor; 3 - major; 4 - critical\n\n" +
                        String.format("**Usage**: `%s%s` <Priority level [1-4]> <Report>", Config.PREFIX, Config.CMD_BUGREPORT));
            }
        } else {
            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833", "Invalid arguments!\n" +
                    "Priority levels: 1 - trivial; 2 - minor; 3 - major; 4 - critical\n\n" +
                    String.format("**Usage**: `%s%s` <Priority level [1-4]> <Report>", Config.PREFIX, Config.CMD_BUGREPORT));
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println(" [COMMAND] " + Time.getTime() + Config.CMD_BUGREPORT.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
