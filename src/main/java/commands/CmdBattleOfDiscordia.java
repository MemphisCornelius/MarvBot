package commands;

import battle_of_discordia.Player;
import core.ServerSettingsHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CmdBattleOfDiscordia implements Command {

    Player p;
    public static HashMap<String, Player> moveReactionMessage = new HashMap<>();

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public static BufferedImage createMapImage(Point[] points) {

        int[][] values = new int[(int) Math.sqrt(points.length)][(int) Math.sqrt(points.length)];
        boolean[][] player = new boolean[(int) Math.sqrt(points.length)][(int) Math.sqrt(points.length)];

        String sql = "SELECT value, pid FROM map WHERE x = ? AND y = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {
            int a = 0;

            for (int i = 0; i < 5; i++) {
                for (int k = 0; k < 5; k++) {

                    Point p = points[a];
                    a++;

                    pst.setInt(1, p.x);
                    pst.setInt(2, p.y);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        values[i][k] = rs.getInt(1);
                        player[i][k] = (rs.getString(2) != null);
                    } else {
                        values[i][k] = -1;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        int PIXEL_SCALE = 40;
        int IMAGE_WIDTH = 200;
        int IMAGE_HIGHT = 200;

        BufferedImage bufferedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HIGHT);

        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {

                if (values[x][y] == 0) {
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                    g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                } else if (values[x][y] == 1) {
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                    g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                } else if (values[x][y] == 2) {
                    g2d.setColor(Color.GREEN);
                    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                    g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                } else if (values[x][y] == 3) {
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                    g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);
                }

                if (player[x][y]) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(y * PIXEL_SCALE + 11, x * PIXEL_SCALE + 11, 18, 18);
                }
            }
        }

        g2d.setColor(Color.WHITE);
        g2d.fillRect(91, 91, 18, 18);

        return bufferedImage;
    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length >= 1) {

            switch (args[0]) {

                case "register":
                    if (!Player.hasEntry(user.getId())) {
                        if (args.length >= 2) {
                            StringBuilder _name = new StringBuilder();

                            for (String s : args) {
                                _name.append(s);
                            }

                            String name = _name.toString();
                            name = name.replace(args[0], "");

                            try {
                                p = new Player(user.getId(), name.trim());
                                MessageMask.msg(tc, user, new Color(20, 90, 10), String.format("Welcome `%s` to the game!", p.getName()));

                            } catch (IllegalArgumentException e) {

                                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, e.getMessage());
                            }

                        } else {
                            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You have to give your player a name!");
                        }
                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You have already a player!");
                    }
                    break;

                case "delete":
                    if (Player.hasEntry(user.getId())) {

                        p = new Player(user.getId());
                        p.delete();
                        MessageMask.msg(tc, user, new Color(90, 10, 10), String.format("You left the game `%s`", p.getName()));

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to delete!");
                    }
                    break;

                case "rename":
                    if (Player.hasEntry(user.getId())) {
                        StringBuilder _name = new StringBuilder();

                        for (String s : args) {
                            _name.append(s);
                        }

                        String name = _name.toString();
                        name = name.replace(args[0], "");

                        try {

                            p = new Player(user.getId());
                            p.setName(name.trim());

                            MessageMask.msg(tc, user, Color.GREEN, "Succcessfully renamed to `" + p.getName() + "`");

                        } catch (IllegalArgumentException e) {
                            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, e.getMessage());
                        }

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to rename!");
                    }
                    break;

                case "move":
                    if (Player.hasEntry(user.getId())) {
                        p = new Player(user.getId());

                        boolean allowedMoving;
                        Duration dur = Duration.ofDays(9999);

                        String sql = "SELECT datetimes FROM  resets WHERE pid = ? AND reset = 'move'";
                        String insert = "INSERT INTO resets(reset, pid) VALUES ('move', ?)";

                        try(Connection con = DriverManager.getConnection(url, usr, pw);
                            PreparedStatement pst0 = con.prepareStatement(sql);
                            PreparedStatement pst1 = con.prepareStatement(insert)) {

                            pst0.setString(1, p.getId());
                            ResultSet rs = pst0.executeQuery();

                            if (rs.next()) {
                                dur = Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now());
                                allowedMoving = (dur.toMillis()) >= 0;
                            }else {
                                pst1.setString(1, p.getId());
                                pst1.executeUpdate();
                                allowedMoving = true;
                            }

                        }catch (SQLException e) {
                            e.printStackTrace();
                            allowedMoving = false;
                        }

                        if (allowedMoving) {

                            BufferedImage bf = createMapImage(Player.getAround(p.getCoordinate(), 2));

                            File file = new File("bod/" + user.getId() + ".png");

                            try {
                                ImageIO.write(bf, "png", file);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            MessageChannel channel = event.getChannel();
                            MessageBuilder message = new MessageBuilder();
                            EmbedBuilder embed = new EmbedBuilder();

                            embed.setColor(Color.orange)
                                    .setAuthor(p.getName(), null, user.getEffectiveAvatarUrl())
                                    .setTitle("MOVE!")
                                    .setDescription("Press one of the arrows to move in this direction!")
                                    .setImage("attachment://" + user.getId() + ".png")
                                    .setTimestamp(Instant.now())
                                    .setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl());
                            message.setEmbed(embed.build());

                            channel.sendMessage(message.build()).addFile(file, user.getId() + ".png").queue(message1 -> {
                                message1.addReaction("⬅").queue();
                                message1.addReaction("⬆").queue();
                                message1.addReaction("⬇").queue();
                                message1.addReaction("➡").queue();
                                message1.addReaction("❌").queue();

                                moveReactionMessage.put(message1.getId(), p);

                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        message1.clearReactions().queue();
                                        moveReactionMessage.remove(message1.getId());
                                    }
                                }, 60000);

                            });
                        }else {
                            MessageMask.msg(tc, user, Color.RED, String.format("Wait %s minutes to move again!", Math.abs(dur.toMinutes())));
                        }
                    }else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to move!");
                    }

                    break;

                case "information":
                case "info":
                    if (Player.hasEntry(user.getId())) {

                        p = new Player(user.getId());

                        event.getTextChannel().sendMessage("**" + p.getName() + "'s information:**\n" + p.getPInformation()).queue();

                        //MessageMask.msg(tc, user, Color.CYAN,"**" + p.getName() + " information** \n\n" +  p.getPInformation());

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to see its information!");
                    }
                    break;

                case "inventory":
                case "inv":
                    if (Player.hasEntry(user.getId())) {

                        p = new Player(user.getId());

                        event.getTextChannel().sendMessage("**" + p.getName() + "'s inventory:**\n" + p.getInv().getIInformation()).queue();

                        //MessageMask.msg(tc, user, Color.CYAN,"**" + p.getName() + " inventory** \n\n" +  p.getInv().getIInformation());

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to see its inventory!");
                    }
                    break;

                case "attack":
                    break;
                case "use":
                    break;
                case "leaderboard":
                case "lb":
                    MessageMask.msg(tc, user, Color.CYAN, Player.leaderBoard());
                    break;
                default:
                    MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
                    break;
            }

        } else {
            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, help());
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_BATTLEOFDISCORDIA.toUpperCase() + " was executed by "
                + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return "Imagine a fancy errormsg here pls";
    }
}
