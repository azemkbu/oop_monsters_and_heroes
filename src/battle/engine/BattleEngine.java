package battle.engine;


import hero.Party;
import worldMap.IWorldMap;

/**
 * Runs turn-based battles between a party of {@link hero.Hero} and a group of {@link monster.Monster}
 */
public interface BattleEngine {

    /**
     * Executes a full battle for the given party, including all hero and
     * monster turns
     *
     * @param party the party of heroes participating in the battle
     * @return {@code true} if the heroes win, {@code false} if they are defeated
     */
    boolean runBattle(Party party, IWorldMap iWorldMap);
}
