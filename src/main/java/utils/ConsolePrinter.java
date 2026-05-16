package utils;

import java.io.Console;
import java.util.Scanner;
import java.util.logging.*;
import static utils.CliUtils.*;

public final class ConsolePrinter {

    private static final Console SYSTEM_CONSOLE = System.console();
    private static final Scanner INPUT_SCANNER = new Scanner(System.in);
    private static final Logger LOGGER = Logger.getLogger(ConsolePrinter.class.getName());
    private static final int WIDTH = 46;

    static {
        setupLogger();
    }

    private ConsolePrinter() {}

    private static void setupLogger() {
        Logger rootLogger = Logger.getLogger("");
        for (Handler h : rootLogger.getHandlers()) rootLogger.removeHandler(h);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord msg) {
                return msg.getMessage() + System.lineSeparator();
            }
        });
        rootLogger.addHandler(handler);
    }

    public static void clear() {
        if (SYSTEM_CONSOLE != null) {
            SYSTEM_CONSOLE.writer().print("\033[H\033[2J");
            SYSTEM_CONSOLE.flush();
        } else {
            LOGGER.info(() -> System.lineSeparator().repeat(10) + "--- REFRESH ---");
        }
    }

    public static void printHeader(String title) {
        clear();
        String line = "═".repeat(WIDTH - 2);

        logFormatted("%s╔%s╗%s%n", CYAN, line, RESET);
        logFormatted("%s║%s%s%s║%s%n", BLUE, BOLD, center(title.toUpperCase(), WIDTH - 2), BLUE, RESET);
        logFormatted("%s╚%s╝%s%n", CYAN, line, RESET);
    }

    public static void printMenuOption(String key, String label) {
        logFormatted("  %s[%s]%s %s%n", GREEN, key, RESET, label);
    }

    public static void printStatus(String message, boolean isError) {
        String color = isError ? RED : GREEN;
        logFormatted("%n%s >> %s%s%n", color, message, RESET);
    }

    public static void logFormatted(String format, Object... args) {
        String message = String.format(format, args);
        if (SYSTEM_CONSOLE != null) {
            SYSTEM_CONSOLE.writer().print(message);
            SYSTEM_CONSOLE.flush();
        } else {
            LOGGER.info(message.stripTrailing());
        }
    }

    public static String readLine(String prompt) {
        logFormatted("%s%s%s ", BOLD, prompt, RESET);
        return (SYSTEM_CONSOLE != null) ? SYSTEM_CONSOLE.readLine() : INPUT_SCANNER.nextLine();
    }

    public static char[] readPassword(String prompt) {
        logFormatted("%s%s%s ", BOLD, prompt, RESET);
        return (SYSTEM_CONSOLE != null) ? SYSTEM_CONSOLE.readPassword() : INPUT_SCANNER.nextLine().toCharArray();
    }
}