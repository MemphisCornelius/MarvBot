package core;

import commands.*;
import listeners.*;
import net.dv8tion.jda.core.*;
import net.dv8tion.jda.core.entities.Game;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import util.Config;

import javax.security.auth.login.LoginException;
import java.io.IOException;


public class Main {

    private static JDABuilder builder;
    private static GitHub gitHub;
    private static GHRepository repo;

    public static GHRepository getRepo() {
        return repo;
    }

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
            gitHub = GitHub.connectUsingPassword(ServerSettingsHandler.getGHLogin(), ServerSettingsHandler.getGHPW());
            repo = gitHub.getRepository("MemphisCornelius/MarvBot");
        } catch (LoginException | IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //LISTENERS
    private static void addListeners() {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new LogListener());
        builder.addEventListener(new MessageListener());
        builder.addEventListener(new AutochannelHandler());
        builder.addEventListener(new AutoroleHandler());
        builder.addEventListener(new DVCbGHandler());
    }

    //COMMANDS
    private static void addCommands() {
        CommandHandler.commands.put(Config.CMD_PING, new CmdPing());
        CommandHandler.commands.put(Config.CMD_LOOTBOX, new CmdLootbox());
        CommandHandler.commands.put(Config.CMD_COINFLIP, new CmdCoinflip());
        CommandHandler.commands.put(Config.CMD_POKE, new CmdPoke());
        CommandHandler.commands.put(Config.CMD_HELP, new CmdHelp());
        CommandHandler.commands.put(Config.CMD_AUTOCHAN, new CmdAutochannel());
      //CommandHandler.commands.put(Config.CMD_ZNAKE, new CmdZnake());
        CommandHandler.commands.put(Config.CMD_ABOUT, new CmdAbout());
      //CommandHandler.commands.put(Config.CMD_BUGREPORT, new CmdBugreport());
        CommandHandler.commands.put(Config.CMD_DEBUG, new CmdDebug());
        CommandHandler.commands.put(Config.CMD_SHUTDOWN, new CmdShutdown());
        CommandHandler.commands.put(Config.CMD_VERSION, new CmdVersion());
        CommandHandler.commands.put(Config.CMD_GITHUBISSUE, new CmdGithubIssue());
        CommandHandler.commands.put(Config.CMD_AUTOROLE, new CmdAutorole());
        CommandHandler.commands.put(Config.CMD_INVENTORY, new CmdInventory());
    }
}