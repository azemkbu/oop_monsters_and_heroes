package utils;
/**
 * Utility class containing ANSI codes for coloring console output
 */
public final class ConsoleColors {
    public static final String RESET  = "\u001B[0m";
    public static final String BOLD   = "\u001B[1m";

    // Text colors
    public static final String BLACK   = "\u001B[30m";
    public static final String RED     = "\u001B[31m";
    public static final String GREEN   = "\u001B[32m";
    public static final String YELLOW  = "\u001B[33m";
    public static final String BLUE    = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN    = "\u001B[36m";
    public static final String WHITE   = "\u001B[37m";

    public static final String BRIGHT_RED    = "\u001B[91m";

    // Background colors
    public static final String BG_BLACK   = "\u001B[40m";
    public static final String BG_RED     = "\u001B[41m";
    public static final String BG_GREEN   = "\u001B[42m";
    public static final String BG_YELLOW  = "\u001B[43m";
    public static final String BG_BLUE    = "\u001B[44m";
    public static final String BG_MAGENTA = "\u001B[45m";
    public static final String BG_CYAN    = "\u001B[46m";
    public static final String BG_WHITE   = "\u001B[47m";

    // Extended colors
    public static final String ORANGE       = "\u001B[38;5;208m";
    public static final String GOLD         = "\u001B[38;5;220m";
    public static final String BRIGHT_CYAN2 = "\u001B[38;5;45m";

    private ConsoleColors() {}
}
