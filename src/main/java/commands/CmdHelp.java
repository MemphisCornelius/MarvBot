package commands;

import core.CommandHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Time;
import util.Config;
import util.MessageMask;

import java.util.Map;
import java.util.TreeMap;

public class CmdHelp implements Command {



    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (args.length > 0) {

            switch (args[0].toLowerCase()) {

                case Config.CMD_ABOUT:
                    MessageMask.help(tc, user, String.format("Shows all people helping to create the bot.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_ABOUT));
                    break;

                case Config.CMD_AUTOCHAN:
                    MessageMask.help(tc, user, String.format("Setup autochannels\n\n" +
                                    "**Usage:**\n" +
                                    ":white_small_square: `%s%s set <Chan ID>` - Set voice chan as auto channel\n" +
                                    ":white_small_square: `%s%s unset <Chan ID>` - Unset voice chan as auto chan\n" +
                                    ":white_small_square: `%s%s list` - Display all registered auto chans\n",
                            Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN));
                    break;

                case Config.CMD_AUTOROLE:
                    MessageMask.help(tc, user, String.format("Setup autoroles\n\n" +
                            "**Usage:**\n" +
                            ":white_small_square: `%s%s set <Role>` - Set role as autorole.\n" +
                            ":white_small_square: `%s%s unset <Role>` - Unset role as autorole.\n" +
                            ":white_small_square: `%s%s list` - Display all registered autoroles.",
                            Config.PREFIX, Config.CMD_AUTOROLE, Config.PREFIX, Config.CMD_AUTOROLE, Config.PREFIX, Config.CMD_AUTOROLE));
                    break;

                case Config.CMD_BUGREPORT:
                    MessageMask.help(tc, user, String.format("Create a bugreport.\n" +
                            "Priority levels: 1 - trivial; 2 - minor; 3 - major; 4 - critical\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <Priority level [1-4]> <Report>", Config.PREFIX, Config.CMD_BUGREPORT));
                    break;

                case Config.CMD_COINFLIP:
                    MessageMask.help(tc, user, String.format("Flips a coin.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_COINFLIP));
                    break;

                case Config.CMD_DEBUG:
                    MessageMask.help(tc, user,"Get special information.");
                    break;

                case Config.CMD_DVCBGIGNORE:
                    MessageMask.help(tc, user,String.format("Add/remove voicechannels ignored by DVCbG\n\n" +
                                    "**USAGE:**\n" +
                                    ":white_small_square:  `%s%s set <Chan ID>`  -  Set DVCbG-ignore channel\n" +
                                    ":white_small_square:  `%s%s unset <Chan ID>`  -  Unset DVCbG-ignore channel\n" +
                                    ":white_small_square:  `%s%s list`  -  Display all DVCbG-ignore channels\n",
                            Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE));
                    break;

                case Config.CMD_initializeDVCbG:
                    MessageMask.help(tc, user, String.format("Initialize voicechannels for DVCbG\n" +
                            "Owner only!"));

                case Config.CMD_GITHUBISSUE:
                    MessageMask.help(tc, user, String.format("Create a GitHub ticket.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <Label number> <Ticket description>\n\n" +
                            "LabelNumbers:\n1: bug, 2: duplicate, 3: enhancement, 4: help wanted, 5: invalid, 6: question", Config.PREFIX, Config.CMD_GITHUBISSUE));
                    break;

                case Config.CMD_HELP:
                    MessageMask.help(tc, user, String.format("Shows all commands.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_HELP));
                    break;

                case Config.CMD_INVENTORY:
                    MessageMask.help(tc, user, String.format("Lists your inventory.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_INVENTORY));
                    break;

                case Config.CMD_LOOTBOX:
                    MessageMask.help(tc, user, String.format("Get a lootbox (You can only get one per day).\nThese lootboxes don't affect anything at the moment.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_LOOTBOX));
                    break;

                case Config.CMD_PING:
                    MessageMask.help(tc, user, String.format("Shows the ping from this bot.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_PING));
                    break;

                case Config.CMD_POKE:
                    MessageMask.help(tc, user, String.format("Send a user a private message over the bot. (Like poking somebody on Teamspeak)\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <User> <Message>", Config.PREFIX, Config.CMD_POKE));
                    break;

                case Config.CMD_SHUTDOWN:
                    MessageMask.help(tc, user, String.format("Restart the bot. This command is owner only!\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_SHUTDOWN));
                    break;

                case Config.CMD_VERSION:
                    MessageMask.help(tc, user, String.format("Shows JDA and/or JAVA version.\n\n" +
                            "**Usage**\n" +
                            ":white_small_square: `%s%s` <[jda/JDA] / [java/JAVA]>", Config.PREFIX, Config.CMD_VERSION));
                    break;

                case Config.CMD_ZNAKE:
                    MessageMask.help(tc, user, String.format("Play the game `%s`! \n\n**Rules:** Collect the two gold nuggets (€) and escape through the door (#) to win the game!\n" +
                            "But watch out for the snakes (S)! If thy bite you, you will lose! You can move your player (P) with reacting to the arrows under the game message!\n" +
                            "Note: The first five moves the snakes won't move.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.CMD_ZNAKE.toUpperCase(), Config.PREFIX, Config.CMD_HELP));
                    break;

                default:
                    MessageMask.help(tc, user, String.format("There is no command with this name. Please enter a valid name. For this try `%s%s`", Config.PREFIX, Config.CMD_HELP));

            }

        } else {

            StringBuilder COMMANDS = new StringBuilder();
            Map<String, Command> commandsSorted = new TreeMap<>(CommandHandler.commands);

            for (String key : commandsSorted.keySet()) {
                COMMANDS.append(":white_small_square:").append(key).append("\n");
            }

            MessageMask.help(tc, user,
                    "[Click here to invite the bot to your server]", Config.INVITELINK, "\n**Commands:**\n" + COMMANDS.toString() + "\nType `help <command>` to get more Information.");

        }
        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_HELP.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }

}


