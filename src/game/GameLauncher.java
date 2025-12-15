package game;

import hero.*;
import java.util.List;
import java.util.Scanner;
import market.service.MarketFactory;
import monster.IMonsterFactory;
import monster.MonsterFactory;
import ui.lov.ConsoleLovView;
import ui.launcher.ConsoleLauncherView;
import ui.launcher.LauncherView;
import upload.HeroFileLoader;
import ui.battle.ConsoleBattleView;
import ui.mh.ConsoleMhView;
import utils.ConsoleIOUtils;
import utils.GameConstants;
import utils.IOUtils;
import worldMap.LegendsOfValorWorldMap;
import worldMap.MonstersAndHeroesWorldMap;

/**
 * 
 *  GameLauncher gets user input to get game type and starts the game
 * 
 * 
 */
public class GameLauncher {

    /**
     * Build dependencies and start the game
     */
    public void run() {
        IOUtils ioUtils = new ConsoleIOUtils(new Scanner(System.in));

        List<Hero> availableHeroes = HeroFileLoader.loadAllHeroes();

        LauncherView launcherView = new ConsoleLauncherView(ioUtils);
        int mode = launcherView.promptGameMode();

        if (mode == 1) {
            Party party = launcherView.promptPartySelection(availableHeroes,
                    GameConstants.PARTY_DEFAULT_MIN_SIZE,
                    GameConstants.PARTY_DEFAULT_MAX_SIZE);
            party.setPosition(GameConstants.PARTY_INITIAL_ROW_POSITION, GameConstants.PARTY_INITIAL_COL_POSITION);

            MarketFactory marketFactory = new MarketFactory();
            MonstersAndHeroesWorldMap worldMap = new MonstersAndHeroesWorldMap(GameConstants.WORLD_MAP_SIZE, marketFactory);

            IMonsterFactory monsterFactory = new MonsterFactory();
            ConsoleBattleView battleView = new ConsoleBattleView(ioUtils);
            battle.engine.BattleEngine battleEngine = new battle.engine.BattleEngineImpl(battleView, monsterFactory);

            ConsoleMhView view = new ConsoleMhView(ioUtils);
            Game game = new GameImpl(worldMap, party, battleEngine, view);
            game.start();
            return;
        }

        // Legends of Valor mode
        Party party = launcherView.promptPartySelection(availableHeroes,
                GameConstants.LOV_HEROES_PER_TEAM,
                GameConstants.LOV_HEROES_PER_TEAM);

        MarketFactory marketFactory = new MarketFactory();
        LegendsOfValorWorldMap worldMap = new LegendsOfValorWorldMap(marketFactory);

        // Place heroes: one per lane (0..2)
        for (int lane = 0; lane < Math.min(party.getHeroes().size(), LegendsOfValorWorldMap.LANE_COLUMNS.length); lane++) {
            worldMap.placeHeroAtNexus(party.getHeroes().get(lane), lane);
        }

        ConsoleLovView view = new ConsoleLovView(ioUtils, worldMap);
        IMonsterFactory monsterFactory = new MonsterFactory();
        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(worldMap, party, monsterFactory, view);
        game.start();
    }
}
