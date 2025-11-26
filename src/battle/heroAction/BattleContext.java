package battle.heroAction;

import battle.menu.BattleMenu;

/**
 * Provides shared context to {@link HeroActionStrategy}
 */
public class BattleContext {

    private final BattleMenu battleMenu;

    public BattleContext(BattleMenu battleMenu) {
        this.battleMenu = battleMenu;
    }

    public BattleMenu getBattleMenu() {
        return battleMenu;
    }
}
