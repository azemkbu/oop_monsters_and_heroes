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
import worldMap.LegendsOfValorWorldMap;
import worldMap.MonstersAndHeroesWorldMap;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
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

        ioUtils.printlnHeader("Choose game mode:");
        ioUtils.printlnTitle("  1) Monsters and Heroes");
        ioUtils.printlnTitle("  2) Legends of Valor");
        ioUtils.printPrompt("Enter choice (1-2): ");
        int mode = ioUtils.readIntInRange(1, 2);

        if (mode == 1) {
            Party party = chooseParty(availableHeroes, ioUtils,
                    GameConstants.PARTY_DEFAULT_MIN_SIZE,
                    GameConstants.PARTY_DEFAULT_MAX_SIZE);
            party.setPosition(GameConstants.PARTY_INITIAL_ROW_POSITION, GameConstants.PARTY_INITIAL_COL_POSITION);

            MarketFactory marketFactory = new MarketFactory();
            MonstersAndHeroesWorldMap worldMap = new MonstersAndHeroesWorldMap(GameConstants.WORLD_MAP_SIZE, marketFactory, ioUtils);

            BattleMenu battleMenu = new BattleMenuImpl(ioUtils);
            MonsterFactory monsterFactory = new MonsterFactory();
            BattleEngine battleEngine = new BattleEngineImpl(battleMenu, ioUtils, monsterFactory);

            Game game = new GameImpl(worldMap, party, battleEngine, ioUtils);
            game.start();
            return;
        }

        // Legends of Valor mode
        Party party = chooseParty(availableHeroes, ioUtils,
                GameConstants.LOV_HEROES_PER_TEAM,
                GameConstants.LOV_HEROES_PER_TEAM);

        MarketFactory marketFactory = new MarketFactory();
        LegendsOfValorWorldMap worldMap = new LegendsOfValorWorldMap(marketFactory, ioUtils);

        // Place heroes: one per lane (0..2)
        for (int lane = 0; lane < Math.min(party.getHeroes().size(), LegendsOfValorWorldMap.LANE_COLUMNS.length); lane++) {
            worldMap.placeHeroAtNexus(party.getHeroes().get(lane), lane);
        }

        BattleMenu battleMenu = new BattleMenuImpl(ioUtils);
        MonsterFactory monsterFactory = new MonsterFactory();

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(worldMap, party, battleMenu, monsterFactory, ioUtils);
        game.start();
    }

    private Party chooseParty(List<Hero> availableHeroes,
                              IOUtils ioUtils,
                              int minSize,
                              int maxSize) {

        ioUtils.printlnTitle(MessageUtils.LIST_OF_HEROES_HEADER);

        for (int i = 0; i < availableHeroes.size(); i++) {
            Hero hero = availableHeroes.get(i);
            ioUtils.printlnTitle(String.format(" [%d] %s%n", i + 1, hero.toString()));
        }

        if (minSize == maxSize) {
            ioUtils.printlnTitle("Party size for this mode is fixed at " + minSize + ".");
        } else {
            ioUtils.printPrompt(String.format(MessageUtils.CHOOSE_YOUR_PARTY_MESSAGE, minSize, maxSize));
        }

        int partySize = (minSize == maxSize)
                ? minSize
                : ioUtils.readIntInRange(minSize, maxSize);

        Party party = new Party();
        Set<Hero> chosen = new HashSet<>();

        for (int i = 0; i < partySize; i++) {
            while (true) {
                ioUtils.printPrompt(String.format(MessageUtils.SELECT_HERO_BY_NUMBER, i + 1));
                int choice = ioUtils.readIntInRange(1, availableHeroes.size());
                Hero hero = availableHeroes.get(choice - 1);
                if (chosen.contains(hero)) {
                    ioUtils.printlnFail("You already selected that hero. Choose a different one.");
                    continue;
                }
                chosen.add(hero);
                party.addHero(hero);
                break;
            }
        }

        return party;
    }
}
