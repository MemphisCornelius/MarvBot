package util;

import core.ServerSettingsHandler;

import java.time.format.DateTimeFormatter;

public class Config {

    private Config() {}

    //USERS
    public static final String OWNERID = "261918744765399044";
    public static final String DESINGER = "182223737884639232";
    public static final String ARTURID = "216970882923364352";
    public static final String RAULID = "307627856710860800";
    public static final String SIMONID = "426410086588743680";

    //PREFIX
    public static final String PREFIX = ServerSettingsHandler.getPrefix();

    //INVITELINK
    public static final  String INVITELINK = ServerSettingsHandler.getInvitelink();

    //GAME
    public static final String GAME = ServerSettingsHandler.getGame();

    //LOG LISTENER CHANNEL
    public static final String CHANNEL_LOG_LISTENER = ServerSettingsHandler.getLogChannelName();

    //COMMANDS
    public static final String CMD_PING = "ping";
    public static final String CMD_COINFLIP = "coinflip";
    public static final String CMD_POKE = "poke";
    public static final String CMD_HELP = "help";
    public static final String CMD_AUTOCHAN = "autochan";
    public static final String CMD_ZNAKE = "znake";
    public static final String CMD_TEST = "test";
    public static final String CMD_ABOUT = "about";
    public static final String CMD_BUGREPORT = "bugreport";
    public static final String CMD_DEBUG = "debug";
    public static final String CMD_SHUTDOWN = "shutdown";
    public static final String CMD_VERSION = "version";
    public static final String CMD_GITHUBISSUE = "ghticket";
    public static final String CMD_AUTOROLE = "autorole";
    public static final String CMD_initializeDVCbG = "dvcbg_initialize";
    public static final String CMD_DVCBGIGNORE = "dvcbg_ignore";
    public static final String CMD_BATTLEOFDISCORDIA = "bod";
    public static final String CMD_GUILDSTREAM = "gs";

    //UTILS
    public static final String ERROR_THUMBNAIL = "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss-SSS");

}
