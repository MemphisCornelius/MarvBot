package battle_of_discordia;

import core.ServerSettingsHandler;
import util.Config;
import util.DataBase;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class Resets {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();
    
    public static void resetGame() {

        String sql = "SELECT datetimes FROM resets WHERE reset = ?";
        String insert = "INSERT INTO resets(reset, datetimes, pid) VALUES (?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst0 = con.prepareStatement(sql);
             PreparedStatement pst1 = con.prepareStatement(insert);
             Statement st = con.createStatement()) {

            pst0.setString(1, "game");
            ResultSet rs = pst0.executeQuery();
            if (!rs.next()) {
                Map.create();
                pst1.setString(1, "game");
                pst1.setString(2, LocalDateTime.now().plusMonths(6).format(Config.formatter));
                pst1.setString(3, "0");
                pst1.executeUpdate();
            } else if(Duration.between(LocalDateTime.parse(rs.getString(1), Config.formatter), LocalDateTime.now()).toMillis() >= 0) {

                String[] truncate = {"inventory", "map", "resets"};

                for (String s : truncate) {
                    st.executeUpdate("TRUNCATE TABLE " + s);
                }

                st.executeUpdate("UPDATE player SET points = 0, hp = 100, cx = " + new Random().nextInt(50) + ", cy = " + new Random().nextInt(50) + " WHERE pid != 0");

                DataBase.mapInsert();
                resetGame();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
