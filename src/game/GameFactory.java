package game;

import hero.Hero;
import java.util.List;
import utils.IOUtils;

/*
*
* Interface for Game Factory
* Allows easy creation of new Game Factories
* 
*/

public interface GameFactory {
    Game createGame(IOUtils ioUtils, List<Hero> availableHeroes);
}
