package utils;

/**
 * Interface that defines methods for console input and output used throughout the game
 */
public interface IOUtils {

    String readLine();

    Integer readInteger();

    int readIntInRange(int min, int max);

    void printlnSuccess(String message);

    void printlnFail(String message);

    void printlnWarning(String message);

    void printPrompt(String message);

    void printlnTitle(String message);

    void printlnHeader(String message);

    /**
     * Clears the console screen.
     */
    void clearScreen();
}
