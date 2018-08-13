package commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.*;
import java.time.*;
import java.util.*;

public class CmdLootbox implements Command {



    private static HashMap<String, LocalDateTime> userIDs = new HashMap<>();

    private static void save() {

        File path = new File("SERVER_SETTINGS/");
        if (!path.exists()) {
            path.mkdir();
        }

        HashMap<String, LocalDateTime> out = new HashMap<>();

        userIDs.forEach((id, d) -> out.put(id, d));


        try {
            FileOutputStream fos = new FileOutputStream("SERVER_SETTINGS/userIDs_for_lootboxes_with_date.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void load() {

        File file = new File("SERVER_SETTINGS/userIDs_for_lootboxes_with_date.dat");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, LocalDateTime> out = (HashMap<String, LocalDateTime>) ois.readObject();
                ois.close();

                out.forEach((d, id) -> userIDs.put(d, id));

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean oneOrMoreDaysAfter(LocalDateTime d1, LocalDateTime d2) {
        Duration duration = Duration.between(d1, d2);
        long diff = Math.abs(duration.toMillis());

        return diff >= 86400000/*ms = 1d*/;

    }

    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) {

        int rn = new Random().nextInt(100) + 1;

        TextChannel tc = event.getTextChannel();
        User user = event.getAuthor();

        if (userIDs.containsKey(event.getAuthor().getId()) && oneOrMoreDaysAfter(userIDs.get(event.getAuthor().getId()), LocalDateTime.now()) || !userIDs.containsKey(event.getAuthor().getId())) {

            if (rn <= 50) {
                MessageMask.msg(tc, user, new Color(126, 126,126),"You've obtained a common lootbox!");

                System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + "obtained a common lootbox");

            } else if (rn <= 70) {
                MessageMask.msg(tc, user, new Color(23, 162, 63),"You've obtained a uncommon lootbox!");

                System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + "obtained an uncommon lootbox");

            } else if (rn <= 85) {
                MessageMask.msg(tc, user, new Color(31, 73, 191),"You've obtained a rare lootbox!");

                System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + "obtained a rare lootbox");

            } else if (rn <= 95) {
                MessageMask.msg(tc, user, new Color(186, 0, 161),"You've obtained a epic lootbox!");

                System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + "obtained am epic lootbox");

            } else if (rn <= 100) {
                MessageMask.msg(tc, user, new Color(228, 189, 36),"You've obtained a legendary lootbox!");

                System.out.println("[LOOTBOX] " + Time.getTime() + event.getMessage().getAuthor() + ">obtained a legendary lootbox");
            }


            if (userIDs.containsKey(event.getAuthor().getId())) {
                userIDs.replace(event.getAuthor().getId(), userIDs.get(event.getAuthor().getId()), LocalDateTime.now());
            } else {
                userIDs.put(event.getAuthor().getId(), LocalDateTime.now());
            }
            save();
        } else {
            MessageMask.msg(tc, user, Color.RED,"You can only get one lootbox in 24 hours!");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_LOOTBOX.toUpperCase() + " was executed by " + event.getMessage().getAuthor());

    }

    @Override
    public String help() {
        return null;
    }
}
