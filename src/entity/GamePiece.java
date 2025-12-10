package entity;

/**
 * Common interface for all game pieces (entities that can stand on tiles).
 * Both Hero and Monster implement this interface for unified position tracking.
 * 
 * ==================== DESIGN RATIONALE ====================
 * 
 * PROBLEM:
 * - Hero and Monster had no common abstraction
 * - WorldMap needed separate Maps and methods for each type
 * - Violated DRY principle and made code harder to maintain
 * 
 * SOLUTION:
 * - Introduce GamePiece interface for common behaviors
 * - Both Hero and Monster implement this interface
 * - WorldMap can manage all pieces uniformly
 * 
 * DESIGN PRINCIPLES APPLIED:
 * - Interface Segregation: Only essential methods defined
 * - Dependency Inversion: WorldMap depends on abstraction, not concrete classes
 * - Open/Closed: New piece types can be added without modifying existing code
 * - Polymorphism: Unified handling of all game pieces
 * 
 * ===========================================================
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
}

