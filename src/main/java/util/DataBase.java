package util;

import core.ServerSettingsHandler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DataBase {

    private DataBase(){}

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public static void createDbTables() {

        String lootboxesTable = "CREATE TABLE IF NOT EXISTS lootboxes" +
                "(uid VARCHAR(20) NOT NULL," +
                " n_c INTEGER," +
                " n_u INTEGER," +
                " n_r INTEGER," +
                " n_e INTEGER," +
                " n_l INTEGER," +
                " PRIMARY KEY (uid))";

        String userTimeTable = "CREATE TABLE IF NOT EXISTS usertime" +
                "(uid VARCHAR(20) NOT NULL," +
                " datetime VARCHAR(23)," +
                " PRIMARY KEY (uid))";

        String autoRolesTable = "CREATE TABLE IF NOT EXISTS autoroles" +
                "(rid VARCHAR(18) NOT NULL," +
                " gid VARCHAR(18)," +
                " PRIMARY KEY (rid))";

        String vcNameTable = "CREATE TABLE IF NOT EXISTS vcnames" +
                "(vcid VARCHAR(18) NOT NULL," +
                " vcname VARCHAR(100)," +
                " PRIMARY KEY (vcid))";

        String dvcbgIgnoreTable = "CREATE TABLE IF NOT EXISTS dvcbgignore" +
                "(vcid VARCHAR(18) NOT NULL," +
                " gid VARCHAR(18)," +
                " PRIMARY KEY (vcid))";

        String playerTable = "CREATE TABLE IF NOT EXISTS player" +
                "(pid VARCHAR(20) NOT NULL," +
                " name VARCHAR(12)," +
                " points INTEGER," +
                " hp DOUBLE," +
                " cx INTEGER," +
                " cy INTEGER," +
                " PRIMARY KEY (pid))";

        String itemTable = "CREATE TABLE IF NOT EXISTS items" +
                "(iid INTEGER NOT NULL AUTO_INCREMENT," +
                " name VARCHAR(30)," +
                " rarity VARCHAR(1)," +
                " dmg DOUBLE," +
                " heal DOUBLE," +
                " dmgabs DOUBLE," +
                " PRIMARY KEY (iid))";

        String inventoryTable = "CREATE TABLE IF NOT EXISTS inventory" +
                "(inventry INTEGER NOT NULL AUTO_INCREMENT," +
                " number INTEGER," +
                " pid VARCHAR(20) NOT NULL," +
                " iid INTEGER NOT NULL," +
                " PRIMARY KEY (inventry)," +
                " FOREIGN KEY (pid) REFERENCES player(pid)," +
                " FOREIGN KEY (iid) REFERENCES items(iid))";

        String mapTable = "CREATE TABLE IF NOT EXISTS map" +
                "(x INTEGER NOT NULL," +
                " y INTEGER NOT NULL," +
                " value INTEGER," +
                " pid VARCHAR(20)," +
                " PRIMARY KEY (x, y)," +
                " FOREIGN KEY (pid) REFERENCES player(pid))";

        String resetsTable = "CREATE TABLE IF NOT EXISTS resets" +
                "(entry INTEGER NOT NULL AUTO_INCREMENT," +
                " reset VARCHAR(50)," +
                " datetimes VARCHAR(23)," +
                " pid VARCHAR(20)," +
                " PRIMARY KEY (entry)," +
                " FOREIGN KEY (pid) REFERENCES player(pid))";

        try {

            Connection conn = DriverManager.getConnection(url, usr, pw);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(lootboxesTable);
            stmt.executeUpdate(userTimeTable);
            stmt.executeUpdate(autoRolesTable);
            stmt.executeUpdate(vcNameTable);
            stmt.executeUpdate(dvcbgIgnoreTable);
            stmt.executeUpdate(playerTable);
            stmt.executeUpdate(itemTable);
            stmt.executeUpdate(inventoryTable);
            stmt.executeUpdate(mapTable);
            stmt.executeUpdate(resetsTable);
            stmt.executeUpdate("INSERT IGNORE INTO player VALUES (0, NULL, NULL, NULL, NULL, NULL )");

            stmt.close();
            conn.close();

        } catch (SQLException e) {

            System.out.println(String.format("[%s] %s", e.getErrorCode(), e.getMessage()));

        }
    }

    public static void mapInsert() {

        try (Connection con = DriverManager.getConnection(url, usr, pw)) {

            try (Statement st = con.createStatement()) {

                ResultSet rs = st.executeQuery("SELECT * FROM map WHERE x = 49 AND y = 49");

                if(!rs.next()) {


                    con.setAutoCommit(false);

                    for (int x = 0; x < 50; x++) {
                        for (int y = 0; y < 50; y++) {

                            st.addBatch("INSERT IGNORE INTO map(x, y) "
                                    + "VALUES(" + x + ", " + y + ")");

                        }
                    }

                    st.executeBatch();

                    con.commit();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                try {

                    con.rollback();
                } catch (SQLException ex2) {

                    ex2.printStackTrace();
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void itemInsert() {

        ArrayList<String> lines = new ArrayList<>();

        try {

            FileReader fr = new FileReader("SERVER_SETTINGS/ITEMS.txt");
            Scanner sc = new Scanner(fr);

            while (sc.hasNext()) {
                String line = sc.nextLine();
                lines.add(line);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Please add a file 'ITEMS.txt' in 'SERVER_SETTINGS'!");
        }

        String[][] items = new String[lines.size()][5];

        int i = 0;
        for (String s : lines) {

            if (!s.isEmpty()) {

                String[] line = s.split(",");

                if (line.length == 5) {
                    items[i] = line;
                    i++;
                }
            }
        }

        String querry = "SELECT * FROM items";
        String insert = "INSERT INTO items(name, rarity, dmg, heal, dmgabs) VALUES (?, ?, ?, ?, ?)";

        try(Connection con = DriverManager.getConnection(url, usr, pw)){

            try(PreparedStatement pst0 = con.prepareStatement(querry);
                PreparedStatement pst1 = con.prepareStatement(insert)) {

                if(!pst0.executeQuery().next()) {

                    con.setAutoCommit(false);

                    for (String[] str: items) {

                        if(str[0] != null) {

                            pst1.setString(1, str[0].trim());
                            pst1.setString(2, str[1].trim());
                            pst1.setDouble(3, Double.parseDouble(str[2]));
                            pst1.setDouble(4, Double.parseDouble(str[3]));
                            pst1.setDouble(5, Double.parseDouble(str[4]));

                            pst1.addBatch();
                        }
                    }

                    pst1.executeBatch();
                    con.commit();

                }
            }catch (SQLException ex) {
                ex.printStackTrace();
                try {

                    con.rollback();
                } catch (SQLException ex2) {

                    ex2.printStackTrace();
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
}
