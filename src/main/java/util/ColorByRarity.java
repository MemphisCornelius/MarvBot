package util;

import java.awt.*;

public class ColorByRarity {

    public static Color getColorByRarity(int rn) {

        if (rn <= 50) {
            return new Color(126, 126, 126);
        } else if (rn <= 70) {
            return new Color(23, 162, 63);
        } else if (rn <= 85) {
            return new Color(31, 73, 191);
        } else if (rn <= 95) {
            return new Color(186, 0, 161);
        } else {
            return new Color(228, 189, 36);
        }
    }

}
