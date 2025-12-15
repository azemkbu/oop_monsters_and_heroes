package lov.usecase.helper;

import hero.Hero;
import monster.Monster;

/**
 * Range helper for Legends of Valor.
 * Defines adjacency-based range (same tile or 8-neighborhood).
 */
public final class LovRangeUtils {
    private LovRangeUtils() {}

    public static boolean isWithinRangeToAttack(Hero hero, Monster monster) {
        if (hero == null || monster == null) return false;
        int rowDiff = Math.abs(hero.getRow() - monster.getRow());
        int colDiff = Math.abs(hero.getCol() - monster.getCol());
        return rowDiff <= 1 && colDiff <= 1;
    }
}


