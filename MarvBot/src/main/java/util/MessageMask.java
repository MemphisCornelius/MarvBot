package util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.*;

public class MessageMask {

    public static void help(TextChannel tc, User user, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(Color.BLACK).
                setAuthor("MarvBot help", null, "https://cdn.discordapp.com/avatars/388355915583324160/048a1b8773c9f946b32a204b68b25c45.png").
                setThumbnail("https://i.pinimg.com/originals/a9/04/71/a9047123313c66cfb13cf4f4c8daee8f.png").
                setDescription(content).
                setTimestamp(Instant.now()).
                setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl()).
                build()
        ).queue();
    }

    public static void help(TextChannel tc, User user, String title, String titleUrl, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(Color.BLACK).
                setAuthor("MarvBot help", null, "https://cdn.discordapp.com/avatars/388355915583324160/048a1b8773c9f946b32a204b68b25c45.png").
                setThumbnail("https://i.pinimg.com/originals/a9/04/71/a9047123313c66cfb13cf4f4c8daee8f.png").
                setTitle(title, titleUrl).
                setDescription(content).
                setTimestamp(Instant.now()).
                setFooter("Requested by @" + user.getName(), user.getEffectiveAvatarUrl()).
                build()
        ).queue();
    }


    public static void msg(TextChannel tc, User user, Color color, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(color).
                setTimestamp(Instant.now()).
                setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()).
                setDescription(content).
                setFooter("ID: " + user.getId(), null).build()
        ).queue();
    }


    public static void msg(TextChannel tc, User user, Color color, String Thumbnail, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(color).
                setTimestamp(Instant.now()).
                setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()).
                setThumbnail(Thumbnail).
                setDescription(content).
                setFooter("ID: " + user.getId(), null).build()
        ).queue();
    }

    public static void msgWithPicture(TextChannel tc, User user, Color color, String pictureUrl, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(color).
                setTimestamp(Instant.now()).
                setAuthor(user.getName(), null, user.getEffectiveAvatarUrl()).
                setImage(pictureUrl).
                setDescription(content).
                setFooter("ID: " + user.getId(), null).build()
        ).queue();
    }

    public static void log(TextChannel tc, User user, Color color, String footer, String content) {
        tc.sendMessage(new EmbedBuilder().
                setColor(color).
                setTimestamp(Instant.now()).
                setAuthor(user.toString(), null, user.getEffectiveAvatarUrl()).
                setDescription(content).
                setFooter(footer, null).build()
        ).queue();
    }
}
