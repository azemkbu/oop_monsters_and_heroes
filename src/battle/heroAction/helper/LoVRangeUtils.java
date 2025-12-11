package battle.heroAction.helper;

import hero.Hero;
import monster.Monster;

public final class LoVRangeUtils {
    private LoVRangeUtils() {
    }

    public static boolean isWithinRangeToAttack(Hero hero,
                                                Monster monster) {

        int monsterRow = monster.getRow();
        int monsterCol = monster.getCol();

        int heroRow = hero.getRow();
        int heroCol = hero.getCol();

        int dx = Math.abs(heroRow - monsterRow);
        int dy = Math.abs(heroCol - monsterCol);

        return dx <= 1 && dy <= 1;
    }
}
