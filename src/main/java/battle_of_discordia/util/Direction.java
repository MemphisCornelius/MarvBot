package battle_of_discordia.util;

public enum Direction {
    LEFT("left"), RIGHT("right"), UP("up"), DOWN("down");

    private final String dir;

    Direction(String dir) {
        this.dir = dir;
    }

    public String dir() {
        return dir;
    }

}
