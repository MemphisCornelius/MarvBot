package core;

import commands.*;
import listeners.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import util.Config;

import javax.security.auth.login.LoginException;


public class Main {

    private static JDABuilder builder;

    public static void main(String[] args) {

        ServerSettingsHandler.initializeSettings();

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(ServerSettingsHandler.getToken());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setGame(Game.of(Game.GameType.LISTENING, Config.GAME));

        addListeners();
        addCommands();

        try {
            builder.build().awaitReady();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //LISTENERS
    private static void addListeners() {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new LogListener());
        builder.addEventListener(new MessageListener());
        builder.addEventListener(new AutoroleHandler());
        builder.addEventListener(new DVCbGHandler());
        builder.addEventListener(new AutochannelHandler());
        builder.addEventListener(new ReactionMoveListener());
    }

    //COMMANDS
    private static void addCommands() {
        CommandHandler.commands.put(Config.CMD_PING, new CmdPing());
        CommandHandler.commands.put(Config.CMD_COINFLIP, new CmdCoinflip());
        CommandHandler.commands.put(Config.CMD_POKE, new CmdPoke());
        CommandHandler.commands.put(Config.CMD_HELP, new CmdHelp());
        CommandHandler.commands.put(Config.CMD_AUTOCHAN, new CmdAutochannel());
      //CommandHandler.commands.put(Config.CMD_ZNAKE, new CmdZnake());
        CommandHandler.commands.put(Config.CMD_ABOUT, new CmdAbout());
        CommandHandler.commands.put(Config.CMD_DEBUG, new CmdDebug());
        CommandHandler.commands.put(Config.CMD_SHUTDOWN, new CmdShutdown());
        CommandHandler.commands.put(Config.CMD_VERSION, new CmdVersion());
        CommandHandler.commands.put(Config.CMD_GITHUBISSUE, new CmdGithubIssue());
        CommandHandler.commands.put(Config.CMD_AUTOROLE, new CmdAutorole());
        CommandHandler.commands.put(Config.CMD_INVENTORY, new CmdInventory());
        CommandHandler.commands.put(Config.CMD_LOOTBOX, new CmdLootbox());
        CommandHandler.commands.put(Config.CMD_initializeDVCbG, new CmdDVCbGInitialize());
        CommandHandler.commands.put(Config.CMD_DVCBGIGNORE, new CmdDVCbGIgnore());
    }
}