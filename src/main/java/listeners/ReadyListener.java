package listeners;

import battle_of_discordia.Resets;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ResumedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.DataBase;
import util.Time;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        LocalDateTime start = LocalDateTime.now();

        File folder = new File("bod");
        folder.mkdir();

        commands.CmdAutochannel.load(event.getJDA());
        commands.CmdSet.load();
        DataBase.createDbTables();
        DataBase.mapInsert();
        DataBase.itemInsert();
        Resets.resetGame();

        long starttime = Duration.between(start, LocalDateTime.now()).toMillis();

        System.out.println("[INFO] " + Time.getTime() + " The bot is ready! [" + starttime + "ms]");
    }

    @Override
    public void onResume(ResumedEvent event) {
        System.out.println("[INFO] " + Time.getTime() + " The bot resumed the connection successfully!");
    }

    @Override
    public void onReconnect(ReconnectedEvent event) {
        System.out.println("[INFO] " + Time.getTime() + " The bot reconnected successfully!");
    }
}