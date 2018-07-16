package commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Config;
import util.Emojis;
import util.MessageMask;
import util.Time;

import java.awt.Point;
import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;


public class CmdZnake implements Command {

    private Point playerPosition = new Point((int) (Math.random() * 19), (int) (Math.random() * 10));
    private Point snakePosition = new Point((int) (Math.random() * 19), (int) (Math.random() * 10));
    private Point goldPosition1 = new Point((int) (Math.random() * 19), (int) (Math.random() * 10));
    private Point goldPosition2 = new Point((int) (Math.random() * 19), (int) (Math.random() * 10));
    private Point doorPosition = new Point((int) (Math.random() * 19), (int) (Math.random() * 10));
    private boolean notPoor = false;
    private boolean rich = false;
    private boolean win = false;
    private boolean loose = false;
    private int i = 0;


    private String draw() {

        String cont = "";

        for (int y = 0; y < 10; y++) {

            for (int x = 0; x < 18; x++) {

                Point p = new Point(x, y);
                if (playerPosition.equals(p)) {
                    cont = cont.replaceFirst("", Emojis.player);
                } else if (snakePosition.equals(p)) {
                    cont = cont.replaceFirst("", Emojis.snake);
                } else if (goldPosition1.equals(p) || goldPosition2.equals(p)) {
                    cont = cont.replaceFirst("", Emojis.moneybag);
                } else if (doorPosition.equals(p)) {
                    cont = cont.replaceFirst("", Emojis.door);
                } else {
                    cont = cont.replaceFirst("", Emojis.square);
                }
            }
            cont = cont.replaceFirst("", "\n");
        }
        return cont;
    }

    private void getReactionForMove(Message msg) {
        User author = msg.getAuthor();

        //System.out.println(msg.getReactions().get(0));
        //System.out.println(msg.getReactions().get(1));
        //System.out.println(msg.getReactions().get(2));
        //System.out.println(msg.getReactions().get(3));

    }

    private void movePlayer(String c) {
        switch (c) {
            case "w":
                playerPosition.y = Math.max(0, playerPosition.y - 1);
                break;
            case "a":
                playerPosition.x = Math.max(0, playerPosition.x - 1);
                break;
            case "s":
                playerPosition.y = Math.max(0, playerPosition.y + 1);
                break;
            case "d":
                playerPosition.x = Math.max(0, playerPosition.x + 1);
                break;
        }
    }

    private void moveSnake(Point p) {
        if (playerPosition.x < p.x) {
            p.x--;
        } else if (playerPosition.x > p.x) {
            p.x++;
        } else if (playerPosition.y < p.y) {
            p.y--;
        } else if (playerPosition.y > p.y) {
            p.y++;
        }
    }

    private void check(TextChannel tc, User user) {
        if (notPoor && rich && doorPosition.equals(playerPosition)) {
            MessageMask.msg(tc, user, Color.green, "You won!");
            win = true;
        }
        if (playerPosition.equals(snakePosition)) {
            MessageMask.msg(tc, user, Color.green, "ZZZZZ The snake bit you. You lost!");
            loose = true;
        }
        if (playerPosition.equals(goldPosition1)) {
            rich = true;
            goldPosition1.setLocation(-1, -1);
        }

        if (playerPosition.equals(goldPosition2)) {
            notPoor = true;
            goldPosition2.setLocation(-1, -1);
        }
    }

    /*private String move(User user, Message msg) {
        user.
    }
*/


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();
        User player = user;

        Message msg = event.getTextChannel().sendMessage(new EmbedBuilder().
                setColor(Color.GREEN).
                setAuthor("ZNAKE").
                setDescription(draw()).
                setTimestamp(OffsetDateTime.of(LocalDate.now(), LocalTime.now(), ZoneOffset.UTC)).
                setFooter("@" + user.getName() + " is playing!", user.getEffectiveAvatarUrl()).
                build()
        ).complete();

        msg.addReaction(Emojis.arrow_left).queue();
        msg.addReaction(Emojis.arrow_up).queue();
        msg.addReaction(Emojis.arrow_down).queue();
        msg.addReaction(Emojis.arrow_right).queue();

        System.out.println(msg.getReactions().toString());

        while (!win || !loose) {

            switch (i) {
                case 0:
                    check(tc, user);
                    //movePlayer();
                    getReactionForMove(msg);
                    i++;
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    msg.editMessage(new EmbedBuilder().
                            setColor(Color.GREEN).
                            setAuthor("ZNAKE").
                            setDescription(draw()).
                            setTimestamp(OffsetDateTime.of(LocalDate.now(), LocalTime.now(), ZoneOffset.UTC)).
                            setFooter("@" + user.getName() + "is playing!", user.getEffectiveAvatarUrl()).
                            build()).queue();
                    check(tc, user);
                    getReactionForMove(msg);
                    //movePlayer();
                    i++;
                    break;
                default:
                    msg.editMessage(new EmbedBuilder().
                            setColor(Color.GREEN).
                            setAuthor("ZNAKE").
                            setDescription(draw()).
                            setTimestamp(OffsetDateTime.of(LocalDate.now(), LocalTime.now(), ZoneOffset.UTC)).
                            setFooter("@" + user.getName() + "is playing!", user.getEffectiveAvatarUrl()).
                            build()).queue();
                    check(tc, user);
                    getReactionForMove(msg);
                    //movePlayer();
                    moveSnake(snakePosition);
            }
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println(" [COMMAND] " + Time.getTime() + Config.CMD_ZNAKE.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
