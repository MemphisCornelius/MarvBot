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

        builder = new JDABuilder(AccountType.BOT);

        builder.setToken(ServerSettingsHandler.getToken());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setGame(Game.of(Game.GameType.LISTENING, Config.GAME));

        System.out.println(JDAInfo.VERSION);

        addListeners();
        addCommands();

        try {
            builder.buildBlocking();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    //LISTENERS
    private static void addListeners() {
        builder.addEventListener(new ReadyListener());
        builder.addEventListener(new LogListener());
        builder.addEventListener(new MessageListener());
        builder.addEventListener(new AutochannelHandler());
        builder.addEventListener(new VoicechatListener());
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
        CommandHandler.commands.put(Config.CMD_BUGREPORT, new CmdBugreport());
        CommandHandler.commands.put(Config.CMD_DEBUG, new CmdDebug());
        CommandHandler.commands.put(Config.CMD_RESTART, new CmdRestart());
        CommandHandler.commands.put(Config.CMD_VERSION, new CmdVersion());
    }
}