package game;

import battle.engine.BattleEngine;
import battle.menu.BattleMenu;
import battle.menu.BattleMenuImpl;
import battle.engine.BattleEngineImpl;
import hero.*;
import market.service.MarketFactory;
import monster.MonsterFactory;
import upload.HeroFileLoader;
import utils.MessageUtils;
import utils.GameConstants;
import utils.ConsoleIOUtils;
import utils.IOUtils;
import worldMap.WorldMap;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the game that wires up the game components and starts the game loop
 */
public class GameLauncher {

    /**
     * Build dependencies and start the game
     */
    public void run() {
        IOUtils ioUtils = new ConsoleIOUtils(new Scanner(System.in));

        List<Hero> availableHeroes = HeroFileLoader.loadAllHeroes();
        Party party = chooseParty(availableHeroes, ioUtils);
        party.setPosition(GameConstants.PARTY_INITIAL_ROW_POSITION, GameConstants.PARTY_INITIAL_COL_POSITION);

        MarketFactory marketFactory = new MarketFactory();

        WorldMap worldMap = new WorldMap(GameConstants.WORLD_MAP_SIZE, marketFactory, ioUtils);

        BattleMenu battleMenu = new BattleMenuImpl(ioUtils);

        MonsterFactory monsterFactory = new MonsterFactory();

        BattleEngine battleEngine = new BattleEngineImpl(battleMenu, ioUtils, monsterFactory);

        Game game = new GameImpl(worldMap, party, battleEngine, ioUtils);
        game.start();
    }

    private Party chooseParty(List<Hero> availableHeroes, IOUtils ioUtils) {

        ioUtils.printlnTitle(MessageUtils.LIST_OF_HEROES_HEADER);

        for (int i = 0; i < availableHeroes.size(); i++) {
            Hero hero = availableHeroes.get(i);
            ioUtils.printlnTitle(String.format(" [%d] %s%n", i + 1, hero.toString()));
        }

        ioUtils.printPrompt(String.format(MessageUtils.CHOOSE_YOUR_PARTY_MESSAGE,
                GameConstants.PARTY_DEFAULT_MIN_SIZE,
                GameConstants.PARTY_DEFAULT_MAX_SIZE));

        int partySize = ioUtils.readIntInRange(
                GameConstants.PARTY_DEFAULT_MIN_SIZE,
                GameConstants.PARTY_DEFAULT_MAX_SIZE
        );

        Party party = new Party();

        for (int i = 0; i < partySize; i++) {
            ioUtils.printPrompt(String.format(MessageUtils.SELECT_HERO_BY_NUMBER, i + 1));
            int choice = ioUtils.readIntInRange(GameConstants.PARTY_DEFAULT_MIN_SIZE,  availableHeroes.size());
            Hero chosen = availableHeroes.get(choice - 1);
            party.addHero(chosen);
        }

        return party;
    }
}
