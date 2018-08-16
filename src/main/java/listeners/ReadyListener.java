package listeners;

import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Time;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        System.out.println("[INFO] " + Time.getTime() + " The bot is ready!");

        commands.CmdAutochannel.load(event.getJDA());
        commands.CmdLootbox.load();
        commands.CmdAutorole.load();
        core.DVCbGHandler.load();
    }
}