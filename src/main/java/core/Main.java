package core;

import commands.*;
import listeners.*;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import util.Config;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ServerSettingsHandler.initializeSettings();

        List<GatewayIntent> gatewayIntents = new ArrayList<>();
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGE_REACTIONS);
        gatewayIntents.add(GatewayIntent.DIRECT_MESSAGES);
        gatewayIntents.add(GatewayIntent.GUILD_BANS);
        gatewayIntents.add(GatewayIntent.GUILD_EMOJIS);
        gatewayIntents.add(GatewayIntent.GUILD_INVITES);
        gatewayIntents.add(GatewayIntent.GUILD_MEMBERS);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        gatewayIntents.add(GatewayIntent.GUILD_MESSAGES);
        gatewayIntents.add(GatewayIntent.GUILD_PRESENCES);
        gatewayIntents.add(GatewayIntent.GUILD_VOICE_STATES);

        addCommands();

        try {
            JDABuilder.create(ServerSettingsHandler.getToken(), gatewayIntents)
                    .setAutoReconnect(true)
                    .setActivity(Activity.of(Activity.ActivityType.LISTENING, Config.GAME))
                    .addEventListeners(new ReadyListener(),
                            new MessageListener(),
                            new AutochannelHandler(),
                            new DVCbGHandler(),
                            new AutoroleHandler(),
                            new ReactionListener(),
                            new LogListener(),
                            new InvitesListener())
                    .build()
                    .awaitReady();
        } catch (InterruptedException | LoginException e) {
            e.printStackTrace();
        }
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
        CommandHandler.commands.put(Config.CMD_AUTOROLE, new CmdAutorole());
        CommandHandler.commands.put(Config.CMD_initializeDVCbG, new CmdDVCbGInitialize());
        CommandHandler.commands.put(Config.CMD_DVCBGIGNORE, new CmdDVCbGIgnore());
        CommandHandler.commands.put(Config.CMD_BATTLEOFDISCORDIA, new CmdBattleOfDiscordia());
        CommandHandler.commands.put(Config.CMD_BAN, new CmdBan());
        CommandHandler.commands.put(Config.CMD_KICK, new CmdKick());
        CommandHandler.commands.put(Config.CMD_SET, new CmdSet());
        CommandHandler.commands.put(Config.CMD_VERIFY, new CmdVerify());
        CommandHandler.commands.put(Config.CMD_INVITES, new CmdInvites());

    }
}