package battle_of_discordia;

import battle_of_discordia.util.SimplexNoiseGenerator;
import core.ServerSettingsHandler;

import java.sql.*;

public class Map {

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public static void create() {

        double[][] array = new SimplexNoiseGenerator(1, 2f, 0.050f).createWorld(50, 50);
        array = convert(array);
        save(array);

    }

    private static double[][] convert(double[][] array) {

        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[x].length; y++) {

                if (array[x][y] <= -0.5) {
                    array[x][y] = 0;
                } else if (array[x][y] <= 0) {
                    array[x][y] = 1;
                } else if (array[x][y] <= 0.5) {
                    array[x][y] = 2;
                } else {
                    array[x][y] = 3;
                }
            }
        }

        return array;

    }

    private static void save(double[][] array) {

        try (Connection con = DriverManager.getConnection(url, usr, pw)) {

            try (Statement st = con.createStatement()) {

                con.setAutoCommit(false);



                for (int x = 0; x < 50; x++) {
                    for (int y = 0; y < 50; y++) {

                        st.addBatch("UPDATE map "
                                + "SET value = " + array[x][y]
                                + " WHERE x = " + x + " AND y = " + y);
                    }
                }

                st.executeBatch();
                con.commit();

            } catch (SQLException ex) {
                try {

                    con.rollback();
                } catch (SQLException ex2) {

                    ex2.printStackTrace();
                }

                ex.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
