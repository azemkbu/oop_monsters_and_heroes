package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import java.util.List;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;

/**
 * Recall action for Legends of Valor
 */
public class RecallAction implements HeroActionStrategy {

    private final ILegendsWorldMap worldMap;
    private final IOUtils io;

    public RecallAction(ILegendsWorldMap worldMap, IOUtils io) {
        this.worldMap = worldMap;
        this.io = io;
    }

    @Override
    public boolean execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ignored) {

        int lane = worldMap.getHeroLane(hero);
        if (lane == -1) {
            io.printlnFail(MessageUtils.MSG_NO_LANE);
            execute(hero, monsters,context,ignored);
            return false;
        }

        io.printlnSuccess(String.format(MessageUtils.MSG_RECALLING_FORMAT, hero.getName()));

        worldMap.recallHero(hero);

        int[] pos = worldMap.getHeroPosition(hero);
        int row = (pos != null ? pos[0] : -1);
        int col = (pos != null ? pos[1] : -1);

        io.printlnSuccess(String.format(MessageUtils.MSG_RECALL_SUCCESS,
                hero.getName(), lane, row, col));

        worldMap.printMap();
        return true;
    }
}
