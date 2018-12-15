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


        try {

            Connection conn = DriverManager.getConnection(url, usr, pw);
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(lootboxesTable);
            stmt.executeUpdate(userTimeTable);
            stmt.executeUpdate(autoRolesTable);
            stmt.executeUpdate(vcNameTable);
            stmt.executeUpdate(dvcbgIgnoreTable);

            stmt.close();
            conn.close();

        } catch (SQLException e) {

            System.out.println(String.format("[%s] %s", e.getErrorCode(), e.getMessage()));

        }
    }

}
