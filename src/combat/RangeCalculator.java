package combat;

import hero.Hero;
import market.model.item.Weapon;
import monster.Monster;

/**
 * Utility class for calculating effective attack ranges.
 * Considers base range from entity type and weapon bonuses.
 */
public final class RangeCalculator {

    private RangeCalculator() {}

    /**
     * Calculates a hero's effective attack range.
     * Effective range = base range + weapon range bonus (if any).
     * 
     * @param hero the hero
     * @return the effective attack range
     */
    public static int getEffectiveRange(Hero hero) {
        if (hero == null) return 1;
        int base = hero.getBaseAttackRange();
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null) {
            base += weapon.getRangeBonus();
        }
        return base;
    }

    /**
     * Calculates a monster's effective attack range.
     * Monsters don't have weapons, so this is just their base range.
     * 
     * @param monster the monster
     * @return the effective attack range
     */
    public static int getEffectiveRange(Monster monster) {
        if (monster == null) return 1;
        return monster.getBaseAttackRange();
    }

    /**
     * Checks if two positions are within a given range.
     * Uses Chebyshev distance (8-directional movement).
     * 
     * @param row1 first position row
     * @param col1 first position column
     * @param row2 second position row
     * @param col2 second position column
     * @param range maximum allowed distance
     * @return true if within range
     */
    public static boolean isWithinRange(int row1, int col1, int row2, int col2, int range) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        // Chebyshev distance: max of row/col differences
        return Math.max(rowDiff, colDiff) <= range;
    }
}

