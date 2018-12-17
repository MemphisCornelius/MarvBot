package battle_of_discordia;

import core.ServerSettingsHandler;
import util.TableBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Inventory {

    private String pid;
    private HashMap<Item, Integer> content;

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    public Inventory(String pid) {

        this.pid = pid;

        String querry = "SELECT number, iid FROM inventory WHERE pid = ?";

        content = new HashMap<>();

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(querry)) {

            pst.setString(1, pid);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                do {
                    content.put(new Item(rs.getInt(2)), rs.getInt(1));
                } while (rs.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean hasItem(Item item) {

        return (content.containsKey(item) && (content.get(item) != 0));

    }

    public int getNuberOfItem(Item item) {

        if(hasItem(item)) {
            return content.get(item);
        }else {
            return 0;
        }
    }

    public void add(Item item, int number) {

        String insert = "INSERT INTO inventory (number, pid, iid) VALUES (?, ?, ?)";
        String update = "UPDATE inventory SET number = ? WHERE pid = ? AND iid = ?";
        String querry = "SELECT number FROM inventory WHERE pid = ? AND iid = ?";

        content = new HashMap<>();

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(insert);
             PreparedStatement pst1 = con.prepareStatement(querry);
             PreparedStatement pst2 = con.prepareStatement(update)) {

            pst1.setString(1, pid);
            pst1.setInt(2, item.getId());

            ResultSet rs = pst1.executeQuery();

            if (!rs.next()) {
                pst.setInt(1, number);
                pst.setString(2, pid);
                pst.setInt(3, item.getId());
                pst.executeUpdate();
                content.put(item, number);
            } else {
                number += rs.getInt(1);
                pst2.setInt(1, number);
                pst2.setString(2, pid);
                pst2.setInt(3, item.getId());
                pst2.executeUpdate();
                content.replace(item, number);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(Item item, int number) {

        String update = "UPDATE inventory SET number = ? WHERE pid = ? AND iid = ?";


        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(update)) {

            pst.setInt(1, number);
            pst.setString(2, pid);
            pst.setInt(3, item.getId());

            content.replace(item, number);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getIInformation() {

        if (content.isEmpty()) {
            return "```The inventory is empty.```";
        }

        String infos[][];
        ArrayList<String[]> infoslist = new ArrayList<>();

        String item = "SELECT * FROM items WHERE iid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(item)) {

            for (Item i : content.keySet()) {

                pst.setInt(1, i.getId());
                ResultSet rs = pst.executeQuery();
                rs.next();

                String info[] = {String.valueOf(rs.getInt(1)),
                        rs.getString(2),
                        String.valueOf(content.get(i)),
                        rs.getString(3),
                        String.valueOf(rs.getDouble(5)),
                        String.valueOf(rs.getDouble(4)),
                        String.valueOf(rs.getDouble(6))
                };

                infoslist.add(info);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        if (!infoslist.isEmpty()) {

            infos = new String[infoslist.size()][7];

            for (int i = 0; i <= infoslist.size() - 1; i++) {
                infos[i] = infoslist.get(i);
            }

        } else {
            return "The inventory is empty.";
        }

        TableBuilder tb = new TableBuilder();
        tb.codeblock(true).frame(true).autoAdjust(true).
                setVerticalOutline('|').setHorizontalOutline('-').
                setCrossDelimiter('+').setHeaderCrossDelimiter('+').
                setHeaderDelimiter('=').setRowDelimiter('-').setColumnDelimiter('|').
                setHeaders("ItemID", "ItemName", "Number", "Rarity", "Heal", "Damage", "Number absorption").setValues(infos);

        return tb.build();

    }
}
