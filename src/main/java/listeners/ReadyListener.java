package listeners;

import core.ServerSettingsHandler;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import util.Time;

import java.sql.*;

public class ReadyListener extends ListenerAdapter {

    private static void createDbTable() {

        String url = ServerSettingsHandler.getDBURL();
        String user = ServerSettingsHandler.getDBUS();
        String password = ServerSettingsHandler.getDBPW();

        String lootboxes = "CREATE CACHED TABLE IF NOT EXISTS lootboxes" +
                "(uid VARCHAR(20) not NULL, " +
                " n_c INTEGER, " +
                " n_u INTEGER, " +
                " n_r INTEGER, " +
                " n_e INTEGER, " +
                " n_l INTEGER, " +
                " PRIMARY KEY (uid))";

        String userTime = "CREATE CACHED TABLE IF NOT EXISTS usertime" +
                "(uid VARCHAR(20) not NULL, " +
                " datetime VARCHAR(23)," +
                " PRIMARY KEY (uid))";

        try {

            Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(lootboxes);
            stmt.executeUpdate(userTime);

            stmt.close();
            conn.close();

        } catch (SQLException e) {

            System.out.println(String.format("[%s] %s", e.getErrorCode(), e.getMessage()));

        }
    }

    @Override
    public void onReady(ReadyEvent event) {

        commands.CmdAutochannel.load(event.getJDA());
        commands.CmdAutorole.load();
        core.DVCbGHandler.load();
        createDbTable();

        System.out.println("[INFO] " + Time.getTime() + " The bot is ready!");
    }
}