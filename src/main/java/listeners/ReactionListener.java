package listeners;

import battle_of_discordia.Inventory;
import battle_of_discordia.Item;
import battle_of_discordia.Player;
import battle_of_discordia.util.Direction;
import commands.CmdBattleOfDiscordia;
import core.ServerSettingsHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.ColorByRarity;
import util.Config;
import util.MessageMask;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

public class ReactionListener extends ListenerAdapter {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    private void editEmbed(GuildMessageReactionAddEvent event, Color color, String description) {

        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> {
            message.clearReactions().queue();
            MessageEmbed oldEmbed = message.getEmbeds().get(0);
            EmbedBuilder embed = new EmbedBuilder().setColor(color).
                    setAuthor(oldEmbed.getAuthor().getName(), null, oldEmbed.getAuthor().getIconUrl()).
                    setFooter(oldEmbed.getFooter().getText(), oldEmbed.getAuthor().getIconUrl()).
                    setTimestamp(Instant.now()).
                    setImage("attachment://" + event.getMember().getUser().getId() + ".png").
                    setTitle("MOVE!").
                    setDescription(description);
            message.editMessage(embed.build()).queue();

        });
    }

    private void getLKey(GuildMessageReactionAddEvent event, Player p, int mapvalue) {

        boolean bool = false;
        Random r = new Random();
        Inventory inv = p.getInv();

        int rn = r.nextInt(10);

        switch (mapvalue) {
            case 0:
                if (rn <= 4)
                    bool = true;
                break;
            case 1:
                if (rn <= 3)
                    bool = true;
                break;
            case 2:
                if (rn <= 2)
                    bool = true;
                break;
            case 3:
                if (rn <= 5)
                    bool = true;
                break;
        }

        if (bool) {

            rn = r.nextInt(100) + 1;

            String key;

            if (rn <= 50) {
                inv.add(new Item(6), 1);
                key = "common";
            } else if (rn <= 70) {
                inv.add(new Item(7), 1);
                key = "uncommon";
            } else if (rn <= 85) {
                inv.add(new Item(8), 1);
                key = "rare";
            } else if (rn <= 95) {
                inv.add(new Item(9), 1);
                key = "epic";
            } else {
                inv.add(new Item(10), 1);
                key = "legendary";
            }

            MessageMask.msg(event.getChannel(), event.getUser(), ColorByRarity.getColorByRarity(rn), "You have found a " + key + " key.");

        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        if (!event.getMember().getUser().isBot()) {
            if (CmdBattleOfDiscordia.attackReactionMessage.containsKey(event.getMessageId())) {

                Player p = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(0));

                if (event.getMember().getUser().getId().equals(p.getId())) {
                    Item i = new Item(Integer.valueOf(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(1)));
                    Player a = null;
                    switch (event.getReactionEmote().getName()) {

                        case "1⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(2));
                            break;
                        case "2⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(3));
                            break;
                        case "3⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(4));
                            break;
                        case "4⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(5));
                            break;
                        case "5⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(6));
                            break;
                        case "6⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(7));
                            break;
                        case "7⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(8));
                            break;
                        case "8⃣":
                            a = new Player(CmdBattleOfDiscordia.attackReactionMessage.get(event.getMessageId()).get(9));
                            break;
                        case "❌":
                            CmdBattleOfDiscordia.attackReactionMessage.remove(event.getMessageId());
                            event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message ->
                                    message.clearReactions().queue());
                            break;
                    }

                    if (a != null) {

                        double beforeA = a.getHp();
                        double beforeP = p.getHp();
                        p.attack(a, i);

                        MessageMask.msg(event.getChannel(), event.getUser(), new Color(85, 26, 139),
                                "You made " + (beforeA - a.getHp()) + "hp damage to " + a.getName() +
                                        " and healed yourself with " + (p.getHp() - beforeP) + "hp.");

                        CmdBattleOfDiscordia.attackReactionMessage.remove(event.getMessageId());
                        event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message ->
                                message.clearReactions().queue());

                        String update = "UPDATE resets SET datetimes = ? WHERE pid = ? AND reset = 'attack'";

                        try (Connection con = DriverManager.getConnection(url, usr, pw);
                             PreparedStatement pst = con.prepareStatement(update)) {

                            pst.setString(1, LocalDateTime.now().plusMinutes(120).format(Config.formatter));
                            pst.setString(2, p.getId());

                            pst.executeUpdate();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (CmdBattleOfDiscordia.moveReactionMessage.containsKey(event.getMessageId())) {

                Player p = CmdBattleOfDiscordia.moveReactionMessage.get(event.getMessageId());
                Direction dir = null;

                if (event.getMember().getUser().getId().equals(p.getId())) {

                    switch (event.getReactionEmote().getName()) {
                        case "⬅":
                            dir = Direction.LEFT;
                            break;
                        case "⬆":
                            dir = Direction.UP;
                            break;
                        case "⬇":
                            dir = Direction.DOWN;
                            break;
                        case "➡":
                            dir = Direction.RIGHT;
                            break;
                        case "❌":
                            CmdBattleOfDiscordia.moveReactionMessage.remove(event.getMessageId());
                            event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message ->
                                    message.clearReactions().queue());
                            break;
                    }

                    try {
                        if (dir != null) {
                            p.move(dir);

                            User user = event.getUser();

                            File file = new File("bod/" + user.getId() + ".png");
                            BufferedImage bf = CmdBattleOfDiscordia.createMapImageForPlayer(Player.getAround(new Player(event.getUser().getId()).getCoordinate(), 2));

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
                                    .setDescription("You moved " + dir.getDir() + "!")
                                    .setImage("attachment://" + user.getId() + ".png")
                                    .setTimestamp(Instant.now())
                                    .setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl());
                            message.setEmbed(embed.build());

                            event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(msg ->
                                    msg.delete().queue()
                            );

                            channel.sendMessage(message.build()).addFile(file, user.getId() + ".png").queue();

                            String sql = "SELECT value FROM map WHERE pid = ?";
                            String update = "UPDATE resets SET datetimes = ? WHERE pid = ? AND reset = 'move'";

                            try (Connection con = DriverManager.getConnection(url, usr, pw);
                                 PreparedStatement pst0 = con.prepareStatement(sql);
                                 PreparedStatement pst1 = con.prepareStatement(update)) {

                                pst0.setString(1, event.getUser().getId());
                                pst1.setString(2, event.getUser().getId());

                                ResultSet rs = pst0.executeQuery();
                                if (rs.next()) {
                                    switch (rs.getInt(1)) {
                                        case 0:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(45).format(Config.formatter));
                                            getLKey(event, p, rs.getInt(1));
                                            break;
                                        case 1:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(30).format(Config.formatter));
                                            getLKey(event, p, rs.getInt(1));
                                            break;
                                        case 2:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(15).format(Config.formatter));
                                            getLKey(event, p, rs.getInt(1));
                                            break;
                                        case 3:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(85).format(Config.formatter));
                                            getLKey(event, p, rs.getInt(1));
                                            break;
                                    }
                                    pst1.executeUpdate();
                                }

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (IndexOutOfBoundsException e) {
                        editEmbed(event, Color.RED, e.getMessage());
                    }
                }
            }
        }
    }
}
