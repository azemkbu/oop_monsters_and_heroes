package combat;

import hero.Hero;
import market.model.item.Weapon;
import monster.Monster;

/**
 * Utility class for calculating effective attack ranges for heroes and monsters.
 * Supports dynamic range calculation based on base range + weapon bonuses.
 */
public final class RangeCalculator {
    
    private RangeCalculator() {}
    
    /**
     * Gets the effective attack range for a hero.
     * Effective range = base range + weapon range bonus (if equipped)
     * 
     * @param hero the hero
     * @return effective attack range in tiles
     */
    public static int getEffectiveRange(Hero hero) {
        int base = hero.getBaseAttackRange();
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null) {
            base += weapon.getRangeBonus();
        }
        return base;
    }
    
    /**
     * Gets the effective attack range for a monster.
     * Monsters don't have weapon bonuses, so just return base range.
     * 
     * @param monster the monster
     * @return effective attack range in tiles
     */
    public static int getEffectiveRange(Monster monster) {
        return monster.getBaseAttackRange();
    }
    
    /**
     * Checks if two positions are within a given range.
     * Uses Chebyshev distance (max of absolute differences).
     * 
     * @param row1 first position row
     * @param col1 first position column
     * @param row2 second position row
     * @param col2 second position column
     * @param range attack range in tiles
     * @return true if positions are within range
     */
    public static boolean isWithinRange(int row1, int col1, int row2, int col2, int range) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return Math.max(rowDiff, colDiff) <= range;
    }
}

