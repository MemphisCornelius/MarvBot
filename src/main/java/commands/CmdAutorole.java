package commands;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import util.Config;
import util.MessageMask;
import util.Time;

import java.awt.*;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdAutorole implements Command {

    private static HashMap<String, List<String>> autoroles = new HashMap<>();

    public static HashMap<String, List<String>> getAutorole() {
        return autoroles;
    }

    private static void save() {

        File path = new File("SERVER_SETTINGS/");
        if (!path.exists())
            path.mkdir();

        HashMap<String, List<String>> out = new HashMap<>();


        autoroles.forEach((g, r) -> out.put(g, r));

        try {
            FileOutputStream fos = new FileOutputStream("SERVER_SETTINGS/autoroles.dat");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(out);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void load() {
        File file = new File("SERVER_SETTINGS/autoroles.dat");
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                HashMap<String, List<String>> out = (HashMap<String, List<String>>) ois.readObject();
                ois.close();

                out.forEach((g, r) -> autoroles.put(g, r));

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean called(String[] args, MessageReceivedEvent event) {
        return false;
    }

    @Override
    public boolean action(String[] args, MessageReceivedEvent event) throws ParseException, IOException {

        String g = event.getGuild().getId();
        User user = event.getAuthor();
        TextChannel tc = event.getTextChannel();
        String mentionedRole;

        if (!autoroles.containsKey(g)) {
            autoroles.put(g, new ArrayList<>());
        }

        List<String> roles = autoroles.get(g);

        if (args.length > 0) {

            switch (args[0]) {
                case "set":
                case "add":
                    try {
                        mentionedRole = event.getMessage().getMentionedRoles().get(0).getId();
                        if (!roles.contains(mentionedRole)) {
                            roles.add(mentionedRole);
                            autoroles.replace(g, roles);
                            save();
                            MessageMask.msg(tc, user, Color.GREEN, ":white_check_mark: Successfully set role as autorole.");
                        } else {
                            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                    "ERROR!\n\n This role is already a autorole!");
                        }
                    } catch (IndexOutOfBoundsException e) {
                        MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                "ERROR!\n\nYou have to name the role you want to add!");
                    }
                    break;

                case "unset":
                case "delete":
                case "remove":
                    if (autoroles.containsKey(g)) {
                        try {
                            mentionedRole = event.getMessage().getMentionedRoles().get(0).getId();
                            if (roles.contains(mentionedRole)) {
                                roles.remove(mentionedRole);
                                autoroles.replace(g, roles);
                                save();
                                MessageMask.msg(tc, user, Color.GREEN, ":white_check_mark: Successfully unset role as autorole. ");
                            } else {
                                MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                        "ERROR!\n\nThis role isn`t a autorole!");
                            }
                        } catch (IndexOutOfBoundsException e) {
                            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                    "ERROR!\n\nYou have to name the role you want to remove!");
                        }
                    } else {
                        MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                                "ERROR!\n\nThere is no role you can remove!");
                    }

                    break;

                case "show":
                case "list":

                    StringBuilder sb = new StringBuilder();

                    sb.append("All autololes:\n\n");

                    for (String r : roles) {
                        sb.append(":white_small_square: ");
                        sb.append(event.getGuild().getRoleById(r).getAsMention());
                        sb.append("\n");
                    }

                    MessageMask.msg(event.getTextChannel(), event.getAuthor(), Color.BLUE, sb.toString());

                    break;

                default:
                    MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                            "ERROR!\n\nYou have to use one of these keywords: set, add, uset, remove, delete, show list!\n\nFor more information use " +
                                    Config.PREFIX + Config.CMD_HELP + " " + Config.CMD_AUTOROLE);
            }
        }else {
            MessageMask.msg(tc, user, Color.RED, "https://vignette.wikia.nocookie.net/timmypedia/images/1/1f/Red-X-in-circle.png/revision/latest?cb=20160924072833",
                    "ERROR! \n\nInvalid arguments!\nUse `" + Config.PREFIX + Config.CMD_HELP + " " + Config.CMD_AUTOROLE + "` to get more information about it.");
        }

        return false;
    }

    @Override
    public void executed(boolean success, MessageReceivedEvent event) {
        System.out.println("[COMMAND] " + Time.getTime() + Config.CMD_AUTOROLE.toUpperCase() + " was executed by " + event.getMessage().getAuthor());
    }

    @Override
    public String help() {
        return null;
    }
}
