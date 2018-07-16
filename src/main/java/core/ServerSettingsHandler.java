package core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

class ServerSettingsHandler {

    private static String[] readFile(String datName) {

        ArrayList<String> lines = new ArrayList<>();

        try {
            FileReader fr = new FileReader(datName);
            Scanner sc = new Scanner(fr);

            while (sc.hasNext()) {
                String line = sc.nextLine();
                lines.add(line);
            }

            return lines.toArray(new String[lines.size()]);

        } catch (FileNotFoundException e) {
            System.err.println("Please add a file 'TOKEN' in 'SERVER_SETTINGS'!");
            return null;

        }
    }

    static String getToken() {

        String[] line = readFile("SERVER_SETTINGS/TOKEN.txt");

        if (line[0] != null) {
            if (line[0].toUpperCase().startsWith("TOKEN: ")) {

                String[] split = line[0].split(" ");

                if (split[1].startsWith("\"") && split[1].endsWith("\"")) {

                    String token = split[1].replaceFirst("\"", "");
                    token = token.substring(0, token.length() - 1);

                    return token;

                } else {
                    System.err.println("Please enter your token in double quotes (\")");
                    return null;
                }

            } else {
                System.err.println("Please enter your token in the fist line with this pattern: \n" +
                        "TOKEN: \"YOUR-TOKEN-HERE\"");
                return null;
            }
        } else {


            System.err.println("Please enter your token in the fist line with the following pattern: \n" +
                    "\tTOKEN: \"YOUR-TOKEN-HERE\"");
            return null;
        }
    }
}
