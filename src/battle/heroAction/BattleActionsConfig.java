package battle.heroAction;

import battle.enums.HeroActionType;
import battle.heroAction.impl.EquipAction;
import battle.heroAction.impl.UsePotionAction;
import battle.heroAction.impl.lov.*;
import battle.heroAction.impl.mh.MHAttackAction;
import battle.heroAction.impl.mh.MhCastSpellAction;
import java.util.HashMap;
import java.util.Map;

import game.GameType;
import utils.IOUtils;
import worldMap.ILegendsWorldMap;
import worldMap.IWorldMap;

/**
 * Utility class for mapping between {@link HeroActionType}
 * and their corresponding {@link HeroActionStrategy} implementations
 */
public final class BattleActionsConfig {

    private BattleActionsConfig() {}

    public static Map<HeroActionType, HeroActionStrategy> createActions(GameType gameType,
                                                                        IWorldMap worldMap,
                                                                        IOUtils ioUtils) {
        Map<HeroActionType, HeroActionStrategy> actions = new HashMap<>();

        switch (gameType){
            case MONSTERS_AND_HEROES:
                return createActionsForMonsterAndHeroes(actions);
            case LEGENDS_OF_VALOR:
                return createActionsForLegendsOfValor(actions, worldMap, ioUtils);
            default:
                return actions;
        }
    }

    private static Map<HeroActionType, HeroActionStrategy> createActionsForMonsterAndHeroes(
            Map<HeroActionType, HeroActionStrategy> actions) {
        actions.put(HeroActionType.ATTACK, new MHAttackAction());
        actions.put(HeroActionType.CAST_SPELL, new MhCastSpellAction());
        actions.put(HeroActionType.USE_POTION, new UsePotionAction());
        actions.put(HeroActionType.EQUIP, new EquipAction());
        return actions;
    }

    private static Map<HeroActionType, HeroActionStrategy> createActionsForLegendsOfValor(
            Map<HeroActionType, HeroActionStrategy> actions,
            IWorldMap worldMap,
            IOUtils ioUtils) {

        if (!(worldMap instanceof ILegendsWorldMap)) {
            throw new IllegalArgumentException("Legends of Valor requires ILegendsOfValorMap");
        }


        ILegendsWorldMap map = (ILegendsWorldMap) worldMap;
        actions.put(HeroActionType.ATTACK, new LoVAttackAction(map));
        actions.put(HeroActionType.CAST_SPELL, new LoVCastSpellAction(map));
        actions.put(HeroActionType.USE_POTION, new UsePotionAction());
        actions.put(HeroActionType.EQUIP, new EquipAction());
        actions.put(HeroActionType.MOVE,     new MoveAction(map, ioUtils));
        actions.put(HeroActionType.TELEPORT, new TeleportAction(map, ioUtils));
        actions.put(HeroActionType.RECALL,   new RecallAction(map, ioUtils));
        actions.put(HeroActionType.REMOVE_OBSTACLE,   new RemoveObstacle(map, ioUtils));
        return actions;
    }
}
