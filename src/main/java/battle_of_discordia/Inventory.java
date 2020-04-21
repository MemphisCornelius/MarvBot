package battle_of_discordia;

import core.ServerSettingsHandler;
import util.TableBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Inventory {

    private String pid;
    private HashMap<Integer, Integer> content;

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();

    Inventory(String pid) {

        this.pid = pid;

        String querry = "SELECT number, iid FROM inventory WHERE pid = ?";

        this.content = new HashMap<>();

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(querry)) {

            pst.setString(1, pid);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                do {
                    this.content.put(rs.getInt(2), rs.getInt(1));
                } while (rs.next());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasItem(Item item) {

        return (content.containsKey(item.getId())) && (content.get(item.getId()) > 0);

    }

    int getNumberOfItem(Item item) {

        if(hasItem(item)) {
            int i = content.get(item.getId());
            return i;
        }else {
            return 0;
        }
    }

    public void add(Item item, int number) {

        String insert = "INSERT INTO inventory (number, pid, iid) VALUES (?, ?, ?)";
        String update = "UPDATE inventory SET number = ? WHERE pid = ? AND iid = ?";
        String querry = "SELECT number FROM inventory WHERE pid = ? AND iid = ?";

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
                content.put(item.getId(), number);
            } else {
                number += rs.getInt(1);
                pst2.setInt(1, number);
                pst2.setString(2, pid);
                pst2.setInt(3, item.getId());
                pst2.executeUpdate();
                content.replace(item.getId(), number);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(Item item, int number) {

        String update = "UPDATE inventory SET number = ? WHERE pid = ? AND iid = ?";
        String remove = "DELETE FROM inventory WHERE number = 0";


        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst0 = con.prepareStatement(update);
             PreparedStatement pst1 = con.prepareStatement(remove)) {

            pst0.setInt(1, (getNumberOfItem(item) - number));
            pst0.setString(2, pid);
            pst0.setInt(3, item.getId());
            pst0.executeUpdate();

            pst1.executeUpdate();

            content.replace(item.getId(), number);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getIInformation() {

        List<String[][]> rtrn = new ArrayList<>();

        if (content.isEmpty()) {
            return new String[] {"```The inventory is empty.```"};
        }

        String[][] infos;
        ArrayList<String[]> infoslist = new ArrayList<>();

        String item = "SELECT * FROM items WHERE iid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(item)) {

            for (int i : content.keySet()) {

                pst.setInt(1, i);
                ResultSet rs = pst.executeQuery();
                rs.next();

                String[] info = {String.valueOf(rs.getInt(1)),
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
            return new String[] {"```The inventory is empty.```"};
        }

        TableBuilder tb = new TableBuilder().
                codeblock(true).frame(true).autoAdjust(true).
                setVerticalOutline('|').setHorizontalOutline('-').
                setCrossDelimiter('+').setHeaderCrossDelimiter('+').
                setHeaderDelimiter('=').setRowDelimiter('-').setColumnDelimiter('|').
                setHeaders("ItemID", "ItemName", "Quantity", "Rarity", "Heal", "Damage", "Damage absorption");



        for(int i=0;i<infos.length;i+=10){
            rtrn.add(Arrays.copyOfRange(infos, i, Math.min(infos.length,i+10)));
        }

        String[] strings = new String[rtrn.size()];

        for (int i = 0; i < strings.length; i++) {
            strings[i] = tb.setValues(rtrn.get(i)).build();
        }

        return strings;
    }
}
