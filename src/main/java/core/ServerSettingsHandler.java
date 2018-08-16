package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class ServerSettingsHandler {

    private ServerSettingsHandler() {}

    private static HashMap<String, String> settings = new HashMap<>();

    static void initializeSettings() {

        ArrayList<String> lines = new ArrayList<>();

        try {
            FileReader fr = new FileReader("SERVER_SETTINGS/SETTINGS.txt");
            Scanner sc = new Scanner(fr);

            while (sc.hasNext()) {
                String line = sc.nextLine();
                lines.add(line);
            }



        } catch (FileNotFoundException e) {
            System.err.println("Please add a file 'SETTINGS.txt' in 'SERVER_SETTINGS'!");
        }

        for (int i = 0; i < lines.size(); i++) {
            String[] split = lines.get(i).split(" ");
            String key;
            String value;

            if (split[0].endsWith(":")) {
                key = split[0].toUpperCase().substring(0, split[0].length() - 1);

                if (split[1].startsWith("\"") && split[1].endsWith("\"")) {
                    value = split[1].replaceFirst("\"", "");
                    value = value.substring(0, value.length() - 1);

                    settings.put(key, value);

                }else {
                    System.err.println("Please check the spelling at the right side of line: " + (i + 1));
                }

            }else {
                System.err.println("Please check the spelling st the left side of line: " + (i + 1));
            }
        }
    }

    static String getToken() {
        return settings.get("TOKEN");
    }
    static String getGHLogin() {
        return settings.get("GITHUB_LOGIN");
    }
    static String getGHOA() {
        return  settings.get("GITHUB_OA");
    }
    static String getGHPW() {
        return settings.get("GITHUB_PW");
    }
}
