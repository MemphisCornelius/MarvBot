package listeners;

import battle_of_discordia.Player;
import battle_of_discordia.util.Direction;
import commands.CmdBattleOfDiscordia;
import core.ServerSettingsHandler;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Config;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;

public class ReactionMoveListener extends ListenerAdapter {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    private void editEmbed(GuildMessageReactionAddEvent event, Color color, String description) {

        event.getChannel().getMessageById(event.getMessageIdLong()).queue(message -> {
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

    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

        if (!event.getMember().getUser().isBot()) {

            if (CmdBattleOfDiscordia.moveReactionMessage.containsKey(event.getMessageId())) {

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
                            event.getChannel().getMessageById(event.getMessageIdLong()).queue(message ->
                                    message.clearReactions().queue());
                            break;
                    }

                    try {
                        if (dir != null) {
                            p.move(dir);

                            User user = event.getUser();

                            File file = new File("bod/" + user.getId() + ".png");
                            BufferedImage bf = CmdBattleOfDiscordia.createMapImage(Player.getAround(new Player(event.getUser().getId()).getCoordinate(), 2));

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
                                    .setDescription("You moved " + dir.dir() + "!")
                                    .setImage("attachment://" + user.getId() + ".png")
                                    .setTimestamp(Instant.now())
                                    .setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl());
                            message.setEmbed(embed.build());

                            event.getChannel().getMessageById(event.getMessageIdLong()).queue(msg ->
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
                                if(rs.next()) {
                                    switch (rs.getInt(1)) {
                                        case 0:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(45).format(Config.formatter));
                                            break;
                                        case 1:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(30).format(Config.formatter));
                                            break;
                                        case 2:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(15).format(Config.formatter));
                                            break;
                                        case 3:
                                            pst1.setString(1, LocalDateTime.now().plusMinutes(85).format(Config.formatter));
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
