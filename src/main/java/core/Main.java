package core;

import commands.*;
import listeners.LogListener;
import listeners.MessageListener;
import listeners.ReactionListener;
import listeners.ReadyListener;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import util.Config;

import javax.security.auth.login.LoginException;

public class Main {

    private static JDABuilder builder;

    public static void main(String[] args) {

        ServerSettingsHandler.initializeSettings();

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(ServerSettingsHandler.getToken());
        builder.setAutoReconnect(true);
        builder.setActivity(Activity.of(Activity.ActivityType.LISTENING, Config.GAME));

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
        builder.addEventListeners(new ReadyListener());
        builder.addEventListeners(new MessageListener());
        builder.addEventListeners(new AutoroleHandler());
        builder.addEventListeners(new DVCbGHandler());
        builder.addEventListeners(new AutochannelHandler());
        builder.addEventListeners(new ReactionListener());
        builder.addEventListeners(new LogListener());
    }

    //COMMANDS
    private static void addCommands() {
        CommandHandler.commands.put(Config.CMD_PING, new CmdPing());
        CommandHandler.commands.put(Config.CMD_COINFLIP, new CmdCoinflip());
        CommandHandler.commands.put(Config.CMD_POKE, new CmdPoke());
        CommandHandler.commands.put(Config.CMD_HELP, new CmdHelp());
        CommandHandler.commands.put(Config.CMD_AUTOCHAN, new CmdAutochannel());
        CommandHandler.commands.put(Config.CMD_ABOUT, new CmdAbout());
        CommandHandler.commands.put(Config.CMD_DEBUG, new CmdDebug());
        CommandHandler.commands.put(Config.CMD_SHUTDOWN, new CmdShutdown());
        CommandHandler.commands.put(Config.CMD_VERSION, new CmdVersion());
        //CommandHandler.commands.put(Config.CMD_GITHUBISSUE, new CmdGithubIssue());
        //CommandHandler.commands.put(Config.CMD_AUTOROLE, new CmdAutorole());
        CommandHandler.commands.put(Config.CMD_initializeDVCbG, new CmdDVCbGInitialize());
        CommandHandler.commands.put(Config.CMD_DVCBGIGNORE, new CmdDVCbGIgnore());
        CommandHandler.commands.put(Config.CMD_BATTLEOFDISCORDIA, new CmdBattleOfDiscordia());
        CommandHandler.commands.put(Config.CMD_BAN, new CmdBan());
        CommandHandler.commands.put(Config.CMD_KICK, new CmdKick());
        CommandHandler.commands.put(Config.CMD_SET, new CmdSet());
        CommandHandler.commands.put(Config.CMD_VERIFY, new CmdVerify());

    }
}