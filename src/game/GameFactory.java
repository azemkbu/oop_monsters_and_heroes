package game;

import hero.Hero;
import utils.IOUtils;

import java.util.List;

public interface GameFactory {
    Game createGame(IOUtils ioUtils, List<Hero> availableHeroes);
}
