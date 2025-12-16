package game;

import game.lov.LegendsOfValorGameFactory;
import game.mh.MonstersAndHeroesGameFactory;
import hero.Hero;
import upload.HeroFileLoader;
import utils.ConsoleIOUtils;
import utils.IOUtils;

import java.util.List;
import java.util.Scanner;

/**
 *
 *  GameLauncher gets user input to get game type and starts the game
 *
 *
 */

public class GameLauncher {

    public void run() {
        IOUtils ioUtils = new ConsoleIOUtils(new Scanner(System.in));
        List<Hero> availableHeroes = HeroFileLoader.loadAllHeroes();

        ioUtils.printlnHeader("Choose game mode:");
        ioUtils.printlnTitle("  1) Monsters and Heroes");
        ioUtils.printlnTitle("  2) Legends of Valor");
        ioUtils.printPrompt("Enter choice (1-2): ");
        int mode = ioUtils.readIntInRange(1, 2);

        GameFactory factory = (mode == 1)
                ? new MonstersAndHeroesGameFactory()
                : new LegendsOfValorGameFactory();

        Game game = factory.createGame(ioUtils, availableHeroes);
        game.start();
    }
}
