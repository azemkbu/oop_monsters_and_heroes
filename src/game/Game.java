package game;

import worldMap.enums.Direction;


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

    /**
     * Handles quitting the game (View should own the I/O).
     */
    void handleQuit();

    /**
     * Handles moving the party in the given direction on the world map
     *
     * @param direction direction in which the party should attempt to move
     */
    void handleMove(Direction direction);

    /**
     * Handles entering the market if the party is standing on a market tile
     */
    void handleEnterMarket();

    /**
     * Shows information about all heroes in the current party
     */
    void showPartyInfo();
}
