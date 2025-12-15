package worldMap;

import hero.Party;
import worldMap.enums.Direction;

/**
 * Interface defining the contract for world maps in the game.
 * Different game modes can have different map implementations.
 */
public interface IWorldMap {

    /**
     * Gets the size of the map (assumes square grid)
     * @return the size of the map
     */
    int getSize();

    /**
     * Gets the tile at the specified position
     * @param row the row index
     * @param col the column index
     * @return the Tile at the specified position
     */
    Tile getTile(int row, int col);

    /**
     * Checks if the given coordinates are within map bounds
     * @param row the row index
     * @param col the column index
     * @return true if within bounds, false otherwise
     */
    boolean checkBounds(int row, int col);

    /**
     * Checks if the specified position is accessible
     * @param row the row index
     * @param col the column index
     * @return true if the position can be entered, false otherwise
     */
    boolean isAccessible(int row, int col);

    /**
     * Attempts to move the party in the given direction
     * @param party the party to move
     * @param direction the direction to move
     * @return true if the move was successful, false otherwise
     */
    boolean moveParty(Party party, Direction direction);

    /**
     * Gets the tile the party is currently standing on
     * @param party the party
     * @return the current Tile
     */
    Tile getPartyTile(Party party);
}

