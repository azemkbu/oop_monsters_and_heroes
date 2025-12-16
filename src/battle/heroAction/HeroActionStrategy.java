package battle.heroAction;

import hero.Hero;
import java.util.List;
import monster.Monster;
import utils.IOUtils;

/**
 * Defines strategy for a hero's chosen action during their turn
 */
public interface HeroActionStrategy {

    /**
     * Executes the action for the given hero within the provided battle context
     *
     * @param hero     hero performing the action
     * @param monsters current list of monsters in the battle
     * @param context  shared battle context
     * @param ioUtils  IO abstraction for user interaction
     */
    boolean execute(Hero hero,
                 List<Monster> monsters,
                 BattleContext context,
                 IOUtils ioUtils);
}
