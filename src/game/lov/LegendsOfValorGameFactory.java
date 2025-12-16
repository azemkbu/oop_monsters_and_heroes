package game.lov;

import battle.menu.BattleMenu;
import battle.menu.BattleMenuImpl;
import game.Game;
import game.GameFactory;
import game.PartyFactoryUtil;
import hero.Hero;
import hero.Party;
import java.util.List;
import market.service.MarketFactory;
import monster.MonsterFactory;
import utils.GameConstants;
import utils.IOUtils;
import worldMap.LegendsOfValorWorldMap;


/*
*
* Using GameFactory interface, creates a new legends of valor game.
*   
*
*/

public class LegendsOfValorGameFactory implements GameFactory {

    @Override
    public Game createGame(IOUtils ioUtils, List<Hero> availableHeroes) {
        Party party = PartyFactoryUtil.chooseParty(
                availableHeroes, ioUtils,
                GameConstants.LOV_HEROES_PER_TEAM,
                GameConstants.LOV_HEROES_PER_TEAM
        );

        MarketFactory marketFactory = new MarketFactory();
        LegendsOfValorWorldMap worldMap = new LegendsOfValorWorldMap(marketFactory, ioUtils);

        for (int lane = 0; lane < Math.min(party.getHeroes().size(), LegendsOfValorWorldMap.LANE_COLUMNS.length); lane++) {
            worldMap.placeHeroAtNexus(party.getHeroes().get(lane), lane);
        }

        BattleMenu battleMenu = new BattleMenuImpl(ioUtils, party); // Pass party for hero index display
        MonsterFactory monsterFactory = new MonsterFactory();

        return new LegendsOfValorGameImpl(worldMap, party, battleMenu, monsterFactory, ioUtils);
    }
}
