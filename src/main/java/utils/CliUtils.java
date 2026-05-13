package utils;

public class CliUtils {

    public static final String RESET = "\033[0m";
    public static final String CYAN = "\033[0;36m";
    public static final String BLUE = "\033[0;34m";
    public static final String GREEN = "\033[0;32m";
    public static final String RED = "\033[0;31m";
    public static final String BOLD = "\033[1m";
    public static final String CLEAR_SCREEN = "\033[H\033[2J";

    private CliUtils() {} // Previene l'istanziazione

    public static String center(String text, int width) {
        if (text == null) return "";
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - text.length() - padding));
    }
}
