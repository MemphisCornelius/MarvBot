package commands;

import battle_of_discordia.Inventory;
import battle_of_discordia.Item;
import battle_of_discordia.Player;
import core.ServerSettingsHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import util.ColorByRarity;
import util.Config;
import util.MessageMask;
import util.Time;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

public class CmdBattleOfDiscordia implements Command {

    private Player p;
    public static HashMap<String, Player> moveReactionMessage = new HashMap<>();
    public static HashMap<String, List<String>> attackReactionMessage = new HashMap<>();

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public static BufferedImage createMapImageForPlayer(Point[] points) {

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

                switch (values[x][y]) {
                    case 0:
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 1:
                        g2d.setColor(Color.YELLOW);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 2:
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 3:
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    default:
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);
                        break;
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

    private static BufferedImage createMapImage() {
        int[][] values = new int[50][50];
        boolean[][] player = new boolean[50][50];

        String sql = "SELECT value, pid FROM map WHERE x = ? AND y = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 0; i < 50; i++) {
                for (int k = 0; k < 50; k++) {

                    pst.setInt(1, i);
                    pst.setInt(2, k);
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
        int IMAGE_WIDTH = 2000;
        int IMAGE_HIGHT = 2000;

        BufferedImage bufferedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HIGHT);

        for (int x = 0; x < values.length; x++) {
            for (int y = 0; y < values[x].length; y++) {

                switch (values[x][y]) {
                    case 0:
                        g2d.setColor(Color.BLUE);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 1:
                        g2d.setColor(Color.YELLOW);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 2:
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    case 3:
                        g2d.setColor(Color.DARK_GRAY);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);

                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE - 1, PIXEL_SCALE - 1);
                        g2d.drawRect(y * PIXEL_SCALE + 1, x * PIXEL_SCALE + 1, PIXEL_SCALE - 3, PIXEL_SCALE - 3);

                        break;
                    default:
                        g2d.setColor(Color.BLACK);
                        g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);
                        break;
                }

                if (player[x][y]) {
                    g2d.setColor(Color.RED);
                    g2d.fillRect(y * PIXEL_SCALE + 11, x * PIXEL_SCALE + 11, 18, 18);
                }
            }
        }

        return bufferedImage;
    }

    private String getLootbox(Inventory inv, int rn) {
        String lootbox;

        if (rn <= 50) {
            inv.add(new Item(1), 1);
            lootbox = "common";
        } else if (rn <= 70) {
            inv.add(new Item(2), 1);
            lootbox = "uncommon";
        } else if (rn <= 85) {
            inv.add(new Item(3), 1);
            lootbox = "rare";
        } else if (rn <= 95) {
            inv.add(new Item(4), 1);
            lootbox = "epic";
        } else {
            inv.add(new Item(5), 1);
            lootbox = "legendary";
        }

        return lootbox;
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

                        try (Connection con = DriverManager.getConnection(url, usr, pw);
                             PreparedStatement pst0 = con.prepareStatement(sql);
                             PreparedStatement pst1 = con.prepareStatement(insert)) {

                            pst0.setString(1, p.getId());
                            ResultSet rs = pst0.executeQuery();

                            if (rs.next()) {
                                dur = Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now());
                                allowedMoving = (dur.toMillis()) >= 0;
                            } else {
                                pst1.setString(1, p.getId());
                                pst1.executeUpdate();
                                allowedMoving = true;
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                            allowedMoving = false;
                        }

                        if (allowedMoving) {

                            BufferedImage bf = createMapImageForPlayer(Player.getAround(p.getCoordinate(), 2));

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
                                        try {
                                            message1.clearReactions().queue();
                                            moveReactionMessage.remove(message1.getId());
                                        } catch (ErrorResponseException e) {}
                                    }
                                }, 60000);

                            });
                        } else {
                            MessageMask.msg(tc, user, Color.RED, String.format("Wait %s minutes to move again!", Math.abs(dur.toMinutes())));
                        }
                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to move!");
                    }

                    break;

                case "information":
                case "info":
                    if (Player.hasEntry(user.getId())) {

                        p = new Player(user.getId());

                        event.getTextChannel().sendMessage("**" + p.getName() + "'s information:**\n" + p.getPInformation()).queue();

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to see its information!");
                    }
                    break;

                case "inventory":
                case "inv":
                    if (Player.hasEntry(user.getId())) {

                        p = new Player(user.getId());

                        String[] invT = p.getInv().getIInformation();

                        for (String str : invT) {

                            event.getTextChannel().sendMessage("**" + p.getName() + "'s inventory:**\n" + str).queue();
                        }


                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to see its inventory!");
                    }
                    break;

                case "attack":

                    if (args.length >= 2) {

                        if (Player.hasEntry(user.getId())) {
                            p = new Player(user.getId());

                            boolean allowedAttacking = false;
                            Duration dur = Duration.ofDays(999999);

                            String sql = "SELECT datetimes FROM  resets WHERE pid = ? AND reset = 'attack'";
                            String insert = "INSERT INTO resets(reset, pid) VALUES ('attack', ?)";

                            try (Connection con = DriverManager.getConnection(url, usr, pw);
                                 PreparedStatement pst0 = con.prepareStatement(sql);
                                 PreparedStatement pst1 = con.prepareStatement(insert)) {

                                pst0.setString(1, p.getId());
                                ResultSet rs = pst0.executeQuery();

                                if (rs.next()) {
                                    dur = Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now());
                                    allowedAttacking = (dur.toMillis() >= 0);
                                } else {
                                    pst1.setString(1, p.getId());
                                    pst1.executeUpdate();
                                    allowedAttacking = true;
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            if (allowedAttacking) {

                                if (p.getInv().hasItem(new Item(Integer.valueOf(args[1])))) {

                                    StringBuilder sb = new StringBuilder();

                                    List<String> playersaround = p.getPlayersAround(1);

                                    if (playersaround != null && playersaround.size() > 0) {
                                        try {
                                            int n = playersaround.size();

                                            sb.append(":one: ").append(event.getJDA().getUserById(playersaround.get(0)).getAsMention()).append("\n");
                                            if (n >= 2)
                                                sb.append(":two: ").append(event.getJDA().getUserById(playersaround.get(1)).getAsMention()).append("\n");
                                            if (n >= 3)
                                                sb.append(":three: ").append(event.getJDA().getUserById(playersaround.get(2)).getAsMention()).append("\n");
                                            if (n >= 4)
                                                sb.append(":four: ").append(event.getJDA().getUserById(playersaround.get(3)).getAsMention()).append("\n");
                                            if (n >= 5)
                                                sb.append(":five: ").append(event.getJDA().getUserById(playersaround.get(4)).getAsMention()).append("\n");
                                            if (n >= 6)
                                                sb.append(":six: ").append(event.getJDA().getUserById(playersaround.get(5)).getAsMention()).append("\n");
                                            if (n >= 7)
                                                sb.append(":seven: ").append(event.getJDA().getUserById(playersaround.get(6)).getAsMention()).append("\n");
                                            if (n >= 8)
                                                sb.append(":eight: ").append(event.getJDA().getUserById(playersaround.get(7)).getAsMention()).append("\n");

                                        } catch (IndexOutOfBoundsException e) {
                                            e.printStackTrace();
                                        }

                                        tc.sendMessage(new EmbedBuilder().
                                                setColor(new Color(85, 26, 139)).
                                                setTimestamp(Instant.now()).
                                                setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()).
                                                setDescription("Select with a reation the player you want to attack:\n\n" + sb.toString()).
                                                setFooter("ID: " + user.getId(), null).build()
                                        ).queue(
                                                msg -> {

                                                    int i = playersaround.size();

                                                    if (i >= 1)
                                                        msg.addReaction("1⃣").queue();
                                                    if (i >= 2)
                                                        msg.addReaction("2⃣").queue();
                                                    if (i >= 3)
                                                        msg.addReaction("3⃣").queue();
                                                    if (i >= 4)
                                                        msg.addReaction("4⃣").queue();
                                                    if (i >= 5)
                                                        msg.addReaction("5⃣").queue();
                                                    if (i >= 6)
                                                        msg.addReaction("6⃣").queue();
                                                    if (i >= 7)
                                                        msg.addReaction("7⃣").queue();
                                                    if (i >= 8)
                                                        msg.addReaction("8⃣").queue();

                                                    msg.addReaction("❌").queue();

                                                    List<String> ls = new ArrayList<>();
                                                    ls.add(p.getId());
                                                    ls.add(args[1]);
                                                    ls.addAll(playersaround);

                                                    attackReactionMessage.put(msg.getId(), ls);

                                                    new Timer().schedule(new TimerTask() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                msg.clearReactions().queue();
                                                                attackReactionMessage.remove(msg.getId());
                                                            } catch (ErrorResponseException e) {}
                                                        }
                                                    }, 60000);
                                                }
                                        );

                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You have no players nearby to attack.");
                                    }


                                } else {
                                    MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have this item to attack someone!");
                                }
                            } else {
                                MessageMask.msg(tc, user, Color.RED, String.format("Wait %s minutes to attack again!", Math.abs(dur.toMinutes())));
                            }

                        } else {
                            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to attack someone!");
                        }
                    } else {
                        MessageMask.msg(tc, user, Color.RED, help());
                    }

                    break;
                case "use":
                    if (args.length >= 2) {
                        if (Player.hasEntry(user.getId())) {
                            Player p = new Player(user.getId());
                            double hpBefore = p.getHp();

                            try {
                                Item i = new Item(Integer.valueOf(args[1]));
                                if (p.getInv().hasItem(i)) {
                                    p.use(i);
                                }

                                MessageMask.msg(event.getTextChannel(), event.getAuthor(), new Color(85, 26, 139),
                                        "You healed yourself with " + (p.getHp() - hpBefore) + "hp.");

                            } catch (IllegalArgumentException e) {
                                MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, e.getMessage());
                            }
                        } else {
                            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a player to use an item.");
                        }
                    } else {
                        help();
                    }

                    break;
                case "leaderboard":
                case "lb":
                    MessageMask.msg(tc, user, Color.CYAN, Player.leaderBoard());
                    break;
                case "lootbox":
                    int rn = new Random().nextInt(100) + 1;

                    if (Player.hasEntry(user.getId())) {
                        Player p = new Player(user.getId());
                        Inventory inv = p.getInv();

                        String dateTimeQuery = "SELECT datetimes FROM resets WHERE pid = ? AND reset = 'lootbox'";
                        String insert = "INSERT INTO resets(reset, datetimes, pid) VALUES ('lootbox', ?, ?)";
                        String update = "UPDATE resets SET datetimes = ? WHERE reset = 'lootbox' AND pid = ?";

                        try (Connection con = DriverManager.getConnection(url, usr, pw);
                             PreparedStatement pst0 = con.prepareStatement(dateTimeQuery);
                             PreparedStatement pst1 = con.prepareStatement(insert);
                             PreparedStatement pst2 = con.prepareStatement(update)) {

                            pst0.setString(1, p.getId());
                            ResultSet rs = pst0.executeQuery();

                            if (rs.next()) {
                                Duration dur = Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now());
                                if (dur.toMillis() >= 0) {

                                    pst2.setString(1, LocalDateTime.now().plusHours(4).format(Config.formatter));
                                    pst2.setString(2, p.getId());
                                    pst2.executeUpdate();

                                    MessageMask.msg(tc, user, ColorByRarity.getColorByRarity(rn), String.format("You have obtained a(n) %s lootbox", getLootbox(inv, rn)));


                                } else {
                                    MessageMask.msg(tc, user, Color.RED, String.format("Wait %s houres to get your next lootbox!", Math.abs(dur.toHours())));
                                }
                            } else {
                                pst1.setString(1, LocalDateTime.now().plusHours(4).format(Config.formatter));
                                pst1.setString(2, p.getId());
                                pst1.executeUpdate();

                                MessageMask.msg(tc, user, ColorByRarity.getColorByRarity(rn), String.format("You have obtained a `%s lootbox`", getLootbox(inv, rn)));

                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                                String.format("You first have to register a player with `%s%s register <playername>`", Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA));
                    }
                    break;
                case "open":
                    if (args.length >= 2) {
                        if (Player.hasEntry(user.getId())) {
                            Player p = new Player(user.getId());
                            Inventory inv = p.getInv();

                            Item item = null;
                            Item i;
                            Item k;
                            switch (args[1]) {
                                case "common":
                                case "c":
                                    i = new Item(1);
                                    k = new Item(6);
                                    if (inv.hasItem(i) && inv.hasItem(k)) {
                                        item = new Item('c');
                                        inv.add(item, 1);
                                        inv.remove(i, 1);
                                        inv.remove(k, 1);
                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a common lootbox/key! ");
                                    }
                                    break;
                                case "uncommon":
                                case "u":
                                    i = new Item(2);
                                    k = new Item(7);
                                    if (inv.hasItem(i) && inv.hasItem(k)) {
                                        item = new Item('u');
                                        inv.add(item, 1);
                                        inv.remove(i, 1);
                                        inv.remove(k, 1);
                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have an uncommon lootbox/key! ");
                                    }
                                    break;
                                case "rare":
                                case "r":
                                    i = new Item(3);
                                    k = new Item(8);
                                    if (inv.hasItem(i) && inv.hasItem(k)) {
                                        item = new Item('r');
                                        inv.add(item, 1);
                                        inv.remove(i, 1);
                                        inv.remove(k, 1);
                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a rare lootbox/key! ");
                                    }
                                    break;
                                case "epic":
                                case "e":
                                    i = new Item(4);
                                    k = new Item(9);
                                    if (inv.hasItem(i) && inv.hasItem(k)) {
                                        item = new Item('e');
                                        inv.add(item, 1);
                                        inv.remove(i, 1);
                                        inv.remove(k, 1);
                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have an epic lootbox/key! ");
                                    }
                                    break;
                                case "legendary":
                                case "l":
                                    i = new Item(5);
                                    k = new Item(10);
                                    if (inv.hasItem(i) && inv.hasItem(k)) {
                                        item = new Item('l');
                                        inv.add(item, 1);
                                        inv.remove(i, 1);
                                        inv.remove(k, 1);
                                    } else {
                                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "You do not have a legendary lootbox/key! ");
                                    }
                                    break;
                                default:
                                    MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL, "There is no lootboxtype with this rarity!");
                                    break;
                            }

                            if (item != null) {
                                MessageMask.msg(tc, user, new Color(148, 161, 250), "You obtained a " + item.getName());
                            }

                        } else {
                            MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                                    String.format("You first have to register a player with `%s%s register <playername>`", Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA));
                        }
                    } else {
                        MessageMask.msg(tc, user, Color.RED, Config.ERROR_THUMBNAIL,
                                "You have to name a lootboxrarity which you want to open.");
                    }

                    break;

                case "map":
                    BufferedImage bf = createMapImage();

                    File file = new File("bod/map.png");

                    try {
                        ImageIO.write(bf, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    MessageChannel channel = event.getChannel();
                    MessageBuilder message = new MessageBuilder();
                    EmbedBuilder embed = new EmbedBuilder();

                    embed.setColor(Color.orange)
                            .setAuthor(user.getName(), null, user.getEffectiveAvatarUrl())
                            .setTitle("map")
                            .setImage("attachment://map.png")
                            .setTimestamp(Instant.now())
                            .setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl());
                    message.setEmbed(embed.build());

                    channel.sendMessage(message.build()).addFile(file, "map.png").queue();
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
        return String.format("**USAGE:**" +
                        "\n:white_small_square: %s%s register <playername>" +
                        "\n:white_small_square: %s%s delete" +
                        "\n:white_small_square: %s%s rename <playername>" +
                        "\n:white_small_square: %s%s move" +
                        "\n:white_small_square: %s%s information" +
                        "\n:white_small_square: %s%s inventory" +
                        "\n:white_small_square: %s%s attack <itemID>" +
                        "\n:white_small_square: %s%s use <itemID>" +
                        "\n:white_small_square: %s%s leaderboard" +
                        "\n:white_small_square: %s%s lootbox" +
                        "\n:white_small_square: %s%s open <lootbox type>" +
                        "\n\n for mor information use %s%s %s",

                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_HELP, Config.CMD_BATTLEOFDISCORDIA
        );
    }
}
