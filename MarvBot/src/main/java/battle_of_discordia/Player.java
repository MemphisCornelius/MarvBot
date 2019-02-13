package battle_of_discordia;

import battle_of_discordia.util.Direction;
import core.ServerSettingsHandler;
import util.TableBuilder;

import java.awt.Point;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Player {

    private final String id;
    private String name;
    private int points;
    private double hp;
    private Point coordinate;
    private Inventory inv;

    private static String url = ServerSettingsHandler.getDBURL();
    private static String usr = ServerSettingsHandler.getDBUS();
    private static String pw = ServerSettingsHandler.getDBPW();


    public Player(String id, String name) {

        if (!(name.length() > 0 && name.length() < 13)) {
            throw new IllegalArgumentException("The name have to be between one and twelve characters long.");
        } else {

            Random r = new Random();

            this.id = id;
            this.name = name;
            points = 0;
            hp = 100;
            coordinate = new Point(r.nextInt(50), r.nextInt(50));
            inv = new Inventory(id);

            String select = "SELECT * FROM player WHERE name = ?";
            String insert = "INSERT INTO player VALUES (?, ?, ?, ?, ?, ?)";
            String map = "UPDATE map SET pid = ? WHERE x = ? AND y = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst0 = con.prepareStatement(select);
                 PreparedStatement pst1 = con.prepareStatement(insert);
                 PreparedStatement pst2 = con.prepareStatement(map)) {

                pst0.setString(1, name);
                ResultSet rs = pst0.executeQuery();

                if (rs.next()) {
                    delete();
                    throw new IllegalArgumentException("The name `" + name + "` is already taken.");
                }

                pst1.setString(1, id);
                pst1.setString(2, name);
                pst1.setInt(3, 0);
                pst1.setDouble(4, hp);
                pst1.setInt(5, coordinate.x);
                pst1.setInt(6, coordinate.y);
                pst1.executeUpdate();

                pst2.setString(1, id);
                pst2.setInt(2, coordinate.x);
                pst2.setInt(3, coordinate.y);

                pst2.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Player(String id) {

        this.id = id;
        inv = new Inventory(id);

        String sql = "SELECT * FROM player WHERE pid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            rs.next();

            name = rs.getString(2);
            points = rs.getInt(3);
            hp = rs.getDouble(4);
            coordinate = new Point(rs.getInt(5), rs.getInt(6));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasEntry(String id) {

        String sql = "SELECT * FROM player WHERE pid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    public static Point[] getAround(Point p, int around) {

        List<Point> points = new ArrayList<>();

        for (int x = (p.x - around); x <= (p.x + around); x++) {
            for (int y = (p.y - around); y <= (p.y + around); y++) {
                points.add(new Point(x, y));
            }
        }

        Point[] pointsaround = new Point[points.size()];

        for (int i = 0; i < points.size(); i++) {
            pointsaround[i] = points.get(i);
        }

        return pointsaround;
    }

    public List<String> getPlayersAround(int arround) {

        Point[] points = getAround(coordinate, arround);
        ArrayList<String> players = new ArrayList<>();

        String sql = "SELECT pid FROM player WHERE cx = ? AND cy = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(sql)) {

            for (int i = 0; i < 9; i++) {

                pst.setInt(1, points[i].x);
                pst.setInt(2, points[i].y);

                ResultSet rs0 = pst.executeQuery();

                if (rs0.next()) {
                    if (rs0.getString(1) != null) {
                        players.add(rs0.getString(1));
                    }
                }
            }

            players.remove(id);

            return players;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String leaderBoard() {

        String[][] lb;
        ArrayList<String[]> lblist = new ArrayList<>();

        String points = "SELECT name, points FROM player WHERE pid != 0 ORDER BY points DESC LIMIT 10";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst = con.prepareStatement(points)) {

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                String[] str = {
                        rs.getString(1),
                        rs.getString(2)
                };

                lblist.add(str);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!lblist.isEmpty()) {

            lb = new String[lblist.size()][2];

            for (int i = 0; i <= lblist.size() - 1; i++) {
                lb[i] = lblist.get(i);
            }
        } else {
            return "```No leaderboard available```";
        }


        TableBuilder tb = new TableBuilder()
                .codeblock(true).frame(true).autoAdjust(true).
                        setVerticalOutline('|').setHorizontalOutline('-').
                        setCrossDelimiter('+').setHeaderCrossDelimiter('+').
                        setHeaderDelimiter('=').setRowDelimiter('-').setColumnDelimiter('|').
                        setHeaders("name", "points").setValues(lb);

        return tb.build();

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getHp() {
        return hp;
    }

    public Inventory getInv() {
        return inv;
    }

    public Point getCoordinate() {
        return coordinate;
    }

    public void setName(String name) {
        if (!(name.length() > 0 && name.length() < 13)) {
            throw new IllegalArgumentException("The name has to be between one and twelve characters long.");
        } else {

            String select = "SELECT * FROM player WHERE name = ?";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst0 = con.prepareStatement(select)) {

                pst0.setString(1, name);
                ResultSet rs = pst0.executeQuery();

                if (rs.next()) {
                    throw new IllegalArgumentException("The name `" + name + "` is already taken.");
                } else {
                    this.name = name;
                    save();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void move(Direction dir) throws IndexOutOfBoundsException {

        switch (dir) {
            case LEFT:
                if (coordinate.y != 0) {
                    coordinate.move(coordinate.x, coordinate.y - 1);
                } else {
                    throw new IndexOutOfBoundsException("You have reached the end of the world.");
                }
                break;
            case RIGHT:
                if (coordinate.y != 49) {
                    coordinate.move(coordinate.x, coordinate.y + 1);
                } else {
                    throw new IndexOutOfBoundsException("You have reached the end of the world.");
                }
                break;
            case DOWN:
                if (coordinate.x != 49) {
                    coordinate.move(coordinate.x + 1, coordinate.y);
                } else {
                    throw new IndexOutOfBoundsException("You have reached the end of the world.");
                }
                break;
            case UP:
                if (coordinate.x != 0) {
                    coordinate.move(coordinate.x - 1, coordinate.y);
                } else {
                    throw new IndexOutOfBoundsException("You have reached the end of the world.");
                }
                break;
        }
        save();
    }

    public void use(Item item) {
        int rn = new Random().nextInt(100) + 1;

        if ((this.hp += item.getHeal()) >= 120) {
            this.hp = 120;
        } else {
            this.hp += item.getHeal();
        }

        if (rn < 50)
            inv.remove(item, 1);


        save();
    }

    public void attack(Player p, Item item) {

        if (inv.hasItem(item)) {

            double dmgabs = 0;
            Item defense = null;

            String dmgabsQ = "SELECT MAX(DISTINCT i.dmgabs), inv.iid " +
                    "FROM items i, inventory inv " +
                    "WHERE inv.pid = ? AND inv.iid = i.iid AND i.dmgabs > 0";

            try (Connection con = DriverManager.getConnection(url, usr, pw);
                 PreparedStatement pst2 = con.prepareStatement(dmgabsQ)) {

                pst2.setString(1, p.getId());
                ResultSet rs2 = pst2.executeQuery();

                if (rs2.next()) {

                    int i = rs2.getInt(2);
                    if (i != 0)
                        defense = new Item(i);

                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!getPlayersAround(1).contains(p.getId())) {
                throw new IllegalArgumentException("You cannot attack a player who is not nearby!");
            } else {

                if (defense != null) {
                    dmgabs = defense.getDmgabs();
                    p.use(defense);
                }

                if ((p.hp - ((1 - dmgabs) * item.getDmg())) > 0) {

                    p.hp = p.hp - ((1 - dmgabs) * item.getDmg());

                } else {
                    p.die(this);
                }

                use(item);

                this.points += ((1 - dmgabs) * item.getDmg());

                save();
                p.save();
            }
        } else {
            throw new IllegalArgumentException("You have to own the weapon you want to use!");
        }
    }

    private void die(Player p) {            //gets killed by p

        String count = "SELECT COUNT(*) FROM inventory WHERE pid = ?";
        String random = "SELECT iid FROM inventory WHERE pid = ? ORDER BY RAND() LIMIT ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst0 = con.prepareStatement(count);
             PreparedStatement pst1 = con.prepareStatement(random)) {

            pst0.setString(1, id);
            pst1.setString(1, id);

            ResultSet rs0 = pst0.executeQuery();
            rs0.next();
            int i = rs0.getInt(1) / 3;

            pst1.setInt(2, i);
            ResultSet rs1 = pst1.executeQuery();
            while (rs1.next()) {
                Item item = new Item(rs1.getInt(1));
                inv.remove(item, p.getInv().getNumberOfItem(item));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Random r = new Random();
        coordinate.move(r.nextInt(50), r.nextInt(50));
        hp = 100;

        points -= 20;
        save();
        p.points += 20;
        p.save();

    }

    public String getPInformation() {

        String[][] infos = {{id, name, String.valueOf(points), String.valueOf(hp), String.valueOf(coordinate.y), String.valueOf(coordinate.x)}};

        TableBuilder tb = new TableBuilder();
        tb.codeblock(true).frame(true).autoAdjust(true).
                setVerticalOutline('|').setHorizontalOutline('-').
                setCrossDelimiter('+').setHeaderCrossDelimiter('+').
                setHeaderDelimiter('=').setRowDelimiter('-').setColumnDelimiter('|').
                setHeaders("id", "name", "points", "hp", "x", "y").setValues(infos);

        return tb.build();

    }

    private void save() {

        String sql = "UPDATE player SET name = ?, hp = ?, cx = ?, cy = ?, points = ? WHERE pid = ?";
        String map0 = "UPDATE map SET pid = null WHERE pid = ?";
        String map1 = "UPDATE map SET pid = ? WHERE x = ? AND y = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst0 = con.prepareStatement(sql);
             PreparedStatement pst1 = con.prepareStatement(map0);
             PreparedStatement pst2 = con.prepareStatement(map1)) {

            pst0.setString(1, name);
            pst0.setDouble(2, hp);
            pst0.setInt(3, coordinate.x);
            pst0.setInt(4, coordinate.y);
            pst0.setDouble(5, points);
            pst0.setString(6, id);

            pst0.executeUpdate();

            pst1.setString(1, id);

            pst1.executeUpdate();

            pst2.setString(1, id);
            pst2.setInt(2, coordinate.x);
            pst2.setInt(3, coordinate.y);

            pst2.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {

        String deletei = "DELETE FROM inventory WHERE pid = ?";
        String map = "UPDATE map SET pid = null WHERE pid = ?";
        String deletep = "DELETE FROM player WHERE pid = ?";
        String deleter = "DELETE FROM resets WHERE pid = ?";

        try (Connection con = DriverManager.getConnection(url, usr, pw);
             PreparedStatement pst0 = con.prepareStatement(deletei);
             PreparedStatement pst3 = con.prepareStatement(deletep);
             PreparedStatement pst1 = con.prepareStatement(map);
             PreparedStatement pst2 = con.prepareStatement(deleter)) {

            pst0.setString(1, id);
            pst0.executeUpdate();

            pst1.setString(1, id);
            pst1.executeUpdate();

            pst2.setString(1, id);
            pst2.executeUpdate();

            pst3.setString(1, id);
            pst3.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
