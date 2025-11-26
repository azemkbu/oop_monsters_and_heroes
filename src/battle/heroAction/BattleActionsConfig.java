package battle.heroAction;

import battle.enums.HeroActionType;
import battle.heroAction.impl.AttackAction;
import battle.heroAction.impl.CastSpellAction;
import battle.heroAction.impl.EquipAction;
import battle.heroAction.impl.UsePotionAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for mapping between {@link HeroActionType}
 * and their corresponding {@link HeroActionStrategy} implementations
 */
public final class BattleActionsConfig {

    public static Map<HeroActionType, HeroActionStrategy> createActions() {
        Map<HeroActionType, HeroActionStrategy> actions = new HashMap<>();
        actions.put(HeroActionType.ATTACK, new AttackAction());
        actions.put(HeroActionType.CAST_SPELL, new CastSpellAction());
        actions.put(HeroActionType.USE_POTION, new UsePotionAction());
        actions.put(HeroActionType.EQUIP, new EquipAction());
        return actions;
    }
}
