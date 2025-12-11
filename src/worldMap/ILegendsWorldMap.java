package worldMap;

import hero.Hero;
import worldMap.enums.Direction;

import java.util.List;

public interface ILegendsWorldMap extends IWorldMap {
    boolean moveHero(Hero hero, Direction direction);

    boolean teleportHero(Hero hero, Hero targetHero);

    void recallHero(Hero hero);

    void printMap();

    int getHeroLane(Hero hero);

    int[] getHeroPosition(Hero hero);

    List<Hero> getHeroes();
}
