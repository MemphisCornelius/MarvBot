package battle_of_discordia;

import core.ServerSettingsHandler;

import java.sql.*;

public class Item {

    private int id;
    private String name;
    private String rarity;
    private double dmg;
    private double heal;
    private double dmgabs;

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public Item(int id, String name, String rarity, double dmg, double heal, double dmgabs) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.dmg = dmg;
        this.heal = heal;
        this.dmgabs = dmgabs;

        String sql = "INSERT INTO items VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            pst.setString(2, name);
            pst.setString(3, rarity);
            pst.setDouble(4, dmg);
            pst.setDouble(5, heal);
            pst.setDouble(6, dmgabs);

            pst.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Item(int id) {

        String sql = "SELECT * FROM items WHERE iid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                this.id = rs.getInt(1);
                this.name = rs.getString(2);
                this.rarity = rs.getString(3);
                this.dmg = rs.getDouble(4);
                this.heal = rs.getDouble(5);
                this.dmgabs = rs.getDouble(6);

            }else {
                throw new IllegalArgumentException("There is no item with this id! [" + id + "]");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Item(String name) {

        String sql = "SELECT * FROM items WHERE name = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                this.id = rs.getInt(1);
                this.name = rs.getString(2);
                this.rarity = rs.getString(3);
                this.dmg = rs.getDouble(4);
                this.heal = rs.getDouble(5);
                this.dmgabs = rs.getDouble(6);

            }else {
                throw new IllegalArgumentException("There is no item with this name! [" + name + "]");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Item(char rarity) {

        String sql = "SELECT * FROM items WHERE rarity = ? AND iid > 10 ORDER BY RAND() LIMIT 1";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, String.valueOf(rarity).toLowerCase());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {

                this.id = rs.getInt(1);
                this.name = rs.getString(2);
                this.rarity = rs.getString(3);
                this.dmg = rs.getDouble(4);
                this.heal = rs.getDouble(5);
                this.dmgabs = rs.getDouble(6);

            }else {
                throw new IllegalArgumentException("There is no item with this rarity! [" + rarity + "]");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRarity() {
        return rarity;
    }

    public double getDmg() {
        return dmg;
    }

    public double getHeal() {
        return heal;
    }

    public double getDmgabs() {
        return dmgabs;
    }


}
