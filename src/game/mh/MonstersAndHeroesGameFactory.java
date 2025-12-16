package game.mh;

import battle.engine.BattleEngine;
import battle.engine.BattleEngineImpl;
import battle.menu.BattleMenu;
import battle.menu.BattleMenuImpl;
import game.Game;
import game.GameFactory;
import game.PartyFactoryUtil;
import hero.Hero;
import hero.Party;
import market.service.MarketFactory;
import monster.MonsterFactory;
import utils.GameConstants;
import utils.IOUtils;
import worldMap.MonstersAndHeroesWorldMap;

import java.util.List;

public class MonstersAndHeroesGameFactory implements GameFactory {

    @Override
    public Game createGame(IOUtils ioUtils, List<Hero> availableHeroes) {
        Party party = PartyFactoryUtil.chooseParty(
                availableHeroes, ioUtils,
                GameConstants.PARTY_DEFAULT_MIN_SIZE,
                GameConstants.PARTY_DEFAULT_MAX_SIZE
        );
        party.setPosition(GameConstants.PARTY_INITIAL_ROW_POSITION, GameConstants.PARTY_INITIAL_COL_POSITION);

        MarketFactory marketFactory = new MarketFactory();
        MonstersAndHeroesWorldMap worldMap =
                new MonstersAndHeroesWorldMap(GameConstants.WORLD_MAP_SIZE, marketFactory, ioUtils);

        BattleMenu battleMenu = new BattleMenuImpl(ioUtils);
        MonsterFactory monsterFactory = new MonsterFactory();
        BattleEngine battleEngine = new BattleEngineImpl(battleMenu, ioUtils, monsterFactory);

        return new MonstersAndHeroesCommandGameImpl(worldMap, party, battleEngine, ioUtils);
    }
}
