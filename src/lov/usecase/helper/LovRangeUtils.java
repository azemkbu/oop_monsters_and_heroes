package lov.usecase.helper;

import combat.RangeCalculator;
import hero.Hero;
import monster.Monster;

/**
 * Range helper for Legends of Valor.
 * Uses RangeCalculator for dynamic range based on hero class and weapon.
 */
public final class LovRangeUtils {
    private LovRangeUtils() {}

    /**
     * Checks if a hero can attack a monster based on the hero's effective range.
     * Effective range = base class range + weapon range bonus.
     */
    public static boolean isWithinRangeToAttack(Hero hero, Monster monster) {
        if (hero == null || monster == null) return false;
        int range = RangeCalculator.getEffectiveRange(hero);
        return RangeCalculator.isWithinRange(
            hero.getRow(), hero.getCol(),
            monster.getRow(), monster.getCol(),
            range
        );
    }
}
