package worldMap;

import hero.Hero;
import java.util.List;
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
}
