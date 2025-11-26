package utils;

import java.util.Scanner;

import static utils.ConsoleColors.*;

/**
 * Concrete implementation of {@link IOUtils} interface
 */
public class ConsoleIOUtils implements IOUtils {

    private final Scanner scanner;

    public ConsoleIOUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    private void println(String message) {
        System.out.println(message);
    }

    private void print(String message) {
        System.out.print(message);
    }

    @Override
    public String readLine() {
        return scanner.nextLine();
    }

    @Override
    public Integer readInteger() {
        return scanner.nextInt();
    }

    @Override
    public int readIntInRange(int min, int max) {
        while (true) {
            String line = readLine();
            if (line == null) {
                printPrompt(String.format("Please enter a number between %d and %d: ", min, max));
                continue;
            }

            try {
                int value = Integer.parseInt(line.trim());
                if (value < min || value > max) {
                    printPrompt(String.format("Please enter a number between %d and %d: ", min, max));
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                printPrompt("Please enter a valid number: ");
            }
        }
    }

    @Override
    public void printlnSuccess(String message) {
        println(GREEN + message + RESET);
    }

    @Override
    public void printlnFail(String message) {
        println(BRIGHT_RED + message + RESET);
    }

    @Override
    public void printlnWarning(String message) {
        println(GOLD + message + RESET);
    }

    @Override
    public void printPrompt(String message) {
        print(BOLD + message + RESET);
    }

    @Override
    public void printlnTitle(String message) {
        println(BOLD + BRIGHT_CYAN2 + message + RESET);
    }

    @Override
    public void printlnHeader(String message) {
        println(BOLD + ORANGE + message + RESET);
    }
}
