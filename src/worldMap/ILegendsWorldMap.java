package worldMap;

import hero.Hero;
import java.util.List;
import monster.Monster;
import worldMap.enums.Direction;

/*
*
*  Extension of world map for legends of valor
*
*
*/

public interface ILegendsWorldMap extends IWorldMap {
    boolean moveHero(Hero hero, Direction direction);

    boolean teleportHero(Hero hero, Hero targetHero);

    void recallHero(Hero hero);

    void printMap();

    int getHeroLane(Hero hero);

    int[] getHeroPosition(Hero hero);

    List<Hero> getHeroes();

    /**
     * Gets all monsters currently tracked on the LOV map.
     */
    List<Monster> getMonsters();

    /**
     * Gets the position of a monster.
     * @return int[]{row, col} or null if not found
     */
    int[] getMonsterPosition(Monster monster);
}
