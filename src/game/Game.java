package game;

/**
 * Entry point for the game logic
 * Defines the main game lifecycle and player methods
 */

public interface Game {

    /**
     * Starts the main game loop
     */
    void start();

    /**
     * Stops the game loop and terminates the current session
     */
    void stop();
}
