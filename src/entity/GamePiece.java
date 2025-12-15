package entity;

/**
 * Common interface for all game pieces (entities that can stand on tiles).
 * Both Hero and Monster implement this interface for unified position tracking.
 */
public interface GamePiece {

    /**
     * Gets the name of this game piece.
     * @return the piece name
     */
    String getName();

    /**
     * Checks if this piece is still alive/active.
     * @return true if alive, false otherwise
     */
    boolean isAlive();

    /**
     * Gets the current row position on the map.
     * @return the row index
     */
    int getRow();

    /**
     * Gets the current column position on the map.
     * @return the column index
     */
    int getCol();

    /**
     * Sets the position of this piece on the map.
     * @param row the row index
     * @param col the column index
     */
    void setPosition(int row, int col);

    /**
     * Checks if this piece is a hero.
     * @return true if this is a Hero, false otherwise
     */
    default boolean isHero() {
        return false;
    }

    /**
     * Checks if this piece is a monster.
     * @return true if this is a Monster, false otherwise
     */
    default boolean isMonster() {
        return false;
    }

    /**
     * Gets the base attack range of this piece.
     * Default is 1 (melee range). Subclasses can override for different ranges.
     * @return the base attack range in tiles
     */
    default int getBaseAttackRange() {
        return 1;
    }
}

