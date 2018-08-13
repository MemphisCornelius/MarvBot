package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    private Time() {}

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("[dd.MM.yyyy - HH:mm:ss] ");
        return dateFormat.format(new Date());
    }
}