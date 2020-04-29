package commands;

import core.CommandHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

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
            String helpMsg;

            switch (args[0].toLowerCase()) {

                case Config.CMD_ABOUT:
                    helpMsg = String.format("Shows all people helping to create the bot.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_ABOUT);
                    break;

                case Config.CMD_AUTOCHAN:
                    helpMsg = String.format("Setup autochannels\n\n" +
                                    "**Usage:**\n" +
                                    ":white_small_square: `%s%s set <Chan ID>` - Set voice chan as auto channel\n" +
                                    ":white_small_square: `%s%s unset <Chan ID>` - Unset voice chan as auto chan\n" +
                                    ":white_small_square:  `%s%s name <Channel ID> <name>`  -  Set name of autochannel\n" +
                                    ":white_small_square:  `%s%s unname <Channel ID>`  -  Unset name of autochannel\n" +
                                    ":white_small_square: `%s%s list` - Display all registered auto chans\n",
                            Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN, Config.PREFIX, Config.CMD_AUTOCHAN);
                    break;

                case Config.CMD_BAN:
                    String.format("Ban a user.\n\n" +
                            "***USAGE***" +
                            "\n:white_small_square: `%s%s <user> <reason>` - Bans the user from the server", Config.PREFIX, Config.CMD_BAN);

                case Config.CMD_BATTLEOFDISCORDIA:
                    helpMsg = String.format("Welcome to the game \"Battle of Discordia\". " +
                            "First, you have to register you a player account with which you will play this game. " +
                            "This player will then spawn randomly within the 50 x 50 units large map. " +
                            "There you can move one unit at once. " +
                            "But you have to wait until your player has moved and this can take some time if you move on a poor ground. " +
                            "Once moving your Player has been finished, there is a chance of finding a key which can open lootboxes. " +
                            "In these lootboxes, which you can get every four hours, you will find weapons, shields and healing spells. " +
                            "With these you can damage other players, block attacks or heal yourself (Tip: Do not try to heal yourself over 120hp, it will not work). " +
                            "But watch out you can use any item only once! " +
                            "To be able to attack another player, you have to be standing within a one-unit range of him. " +
                            "So, you have to move and find other players to attack. " +
                            "If you attack another player, he will automatically equip his best shield to block your attack. " +
                            "For each hit you will receive the same amount of points as you did damage. " +
                            "If you kill a player he will lose a third of his inventory, lose all his points and you will get 20 points extra. " +
                            "But remember you can only attack every two hours.");
                    MessageMask.help(tc, user, helpMsg);

                    helpMsg = String.format("**USAGE:**" +
                                    "\n:white_small_square: `%s%s register <playername>` - Register yourself for this game." +
                                    "\n:white_small_square: `%s%s delete` - Delete your registered account." +
                                    "\n:white_small_square: `%s%s rename <playername>` - Rename your account if you are unhappy with your current one." +
                                    "\n:white_small_square: `%s%s move` - Shows you the map and gives you the opportunity to move." +
                                    "\n:white_small_square: `%s%s information` - See all the stats of your player." +
                                    "\n:white_small_square: `%s%s inventory` - Opens your inventory." +
                                    "\n:white_small_square: `%s%s attack <itemID>` - Gives you the opportunity to attack a nearby player with the selected item." +
                                    "\n:white_small_square: `%s%s use <itemID>` - Use the selected item to heal you." +
                                    "\n:white_small_square: `%s%s leaderboard` - Shows you the leaderboard." +
                                    "\n:white_small_square: `%s%s lootbox` - Get a lootbox." +
                                    "\n:white_small_square: `%s%s open <lootbox type>` - Open a lootbox.",

                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA, Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA,
                            Config.PREFIX, Config.CMD_BATTLEOFDISCORDIA);

                    break;

                case Config.CMD_AUTOROLE:
                    helpMsg = String.format("Setup autoroles\n\n" +
                                    "**Usage:**\n" +
                                    ":white_small_square: `%s%s set <Role>` - Set role as autorole.\n" +
                                    ":white_small_square: `%s%s unset <Role>` - Unset role as autorole.\n" +
                                    ":white_small_square: `%s%s list` - Display all registered autoroles.",
                            Config.PREFIX, Config.CMD_AUTOROLE, Config.PREFIX, Config.CMD_AUTOROLE, Config.PREFIX, Config.CMD_AUTOROLE);
                    break;

                case Config.CMD_BUGREPORT:
                    helpMsg = String.format("Create a bugreport.\n" +
                            "Priority levels: 1 - trivial; 2 - minor; 3 - major; 4 - critical\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <Priority level [1-4]> <Report>", Config.PREFIX, Config.CMD_BUGREPORT);
                    break;

                case Config.CMD_COINFLIP:
                    helpMsg = String.format("Flips a coin.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_COINFLIP);
                    break;

                case Config.CMD_DEBUG:
                    helpMsg = "Get special information.";
                    break;

                case Config.CMD_DVCBGIGNORE:
                    helpMsg = String.format("Add/remove voicechannels ignored by DVCbG\n\n" +
                                    "**USAGE:**\n" +
                                    ":white_small_square:  `%s%s set <Chan ID>`  -  Set DVCbG-ignore channel\n" +
                                    ":white_small_square:  `%s%s unset <Chan ID>`  -  Unset DVCbG-ignore channel\n" +
                                    ":white_small_square:  `%s%s list`  -  Display all DVCbG-ignore channels\n",
                            Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE, Config.PREFIX, Config.CMD_DVCBGIGNORE);
                    break;

                case Config.CMD_initializeDVCbG:
                    helpMsg = String.format("Initialize voicechannels for DVCbG\n" +
                            "Botowner only!");
                    break;

                case Config.CMD_GITHUBISSUE:
                    helpMsg = String.format("Create a GitHub ticket.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <Label number> <Ticket description>\n\n" +
                            "LabelNumbers:\n1: bug, 2: duplicate, 3: enhancement, 4: help wanted, 5: invalid, 6: question", Config.PREFIX, Config.CMD_GITHUBISSUE);
                    break;

                case Config.CMD_HELP:
                    helpMsg = String.format("Shows all commands.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_HELP);
                    break;

                case Config.CMD_KICK:
                    helpMsg = String.format("Kick a user\n\n" +
                            "***USAGE***" +
                            "\n:white_small_square: `%s%s <user> <reason>` - Kicks the member from the server.", Config.PREFIX, Config.CMD_KICK);
                    break;

                case Config.CMD_PING:
                    helpMsg = String.format("Shows the ping from this bot.\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_PING);
                    break;

                case Config.CMD_POKE:
                    helpMsg = String.format("Send a user a private message over the bot. (Like poking somebody on Teamspeak)\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s` <User> <Message>", Config.PREFIX, Config.CMD_POKE);
                    break;

                case Config.CMD_SET:
                    helpMsg = String.format("***USAGE***\n" +
                                    ":white_small_square: `%s%s verify <role>` - Set verify role.\n" +
                                    ":white_small_square: `%s%s moderation <role>` - Set moderation role.\n" +
                                    ":white_small_square: `%s%s modlog <textChanneliId>` - Set modlog channel.\n" +
                                    ":white_small_square: `%s%s autorole <role>` - Set autorole.\n" +
                                    ":white_small_square: `%s%s list` - List all configs.\n" +
                                    ":white_small_square: `%s%s remove <type>` - Removes the config setting.",
                            Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET, Config.PREFIX, Config.CMD_SET);
                    break;

                case Config.CMD_SHUTDOWN:
                    helpMsg = String.format("Restart the bot. This command is owner only!\n\n" +
                            "**Usage**:\n :white_small_square: `%s%s`", Config.PREFIX, Config.CMD_SHUTDOWN);
                    break;

                case Config.CMD_VERSION:
                    helpMsg = String.format("Shows JDA and/or JAVA version.\n\n" +
                            "**Usage**\n" +
                            ":white_small_square: `%s%s` <[jda/JDA] / [java/JAVA]>", Config.PREFIX, Config.CMD_VERSION);
                    break;

                default:
                    helpMsg = String.format("There is no command with this name. Please enter a valid name. For this try `%s%s`", Config.PREFIX, Config.CMD_HELP);

            }

            MessageMask.help(tc, user, helpMsg);

        } else {

            StringBuilder COMMANDS = new StringBuilder();
            Map<String, Command> commandsSorted = new TreeMap<>(CommandHandler.commands);

            for (String key : commandsSorted.keySet()) {
                COMMANDS.append(":white_small_square: ").append(key).append("\n");
            }

            String com = COMMANDS.toString().replace(":white_small_square: " + Config.CMD_VERIFY + "\n", "");

            MessageMask.help(tc, user,
                    "[Click here to invite the bot to your server]", Config.INVITELINK, "\n**Commands:**\n" + com + "\nType `help <command>` to get more Information.");

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


