package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.helper.LoVRangeUtils;
import battle.heroAction.impl.BaseCastSpellAction;
import hero.Hero;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cast spell action implementation for the Legends of Valor
 */
public class LoVCastSpellAction extends BaseCastSpellAction {

    @Override
    protected List<Monster> getAvailableMonsters(Hero hero,
                                                 List<Monster> monsters,
                                                 BattleContext context) {
        if (monsters == null || monsters.isEmpty()) {
            return monsters;
        }

        return monsters.stream()
                .filter(m -> m.getHp() > 0)
                .filter(m -> LoVRangeUtils.isWithinRangeToAttack(hero, m))
                .collect(Collectors.toList());
    }

    @Override
    protected void handleNoAvailableMonsters(Hero hero,
                                             List<Monster> monsters,
                                             BattleContext context,
                                             IOUtils ioUtils) {
        if (monsters == null || monsters.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_CAST_SPELL);
        } else {
            ioUtils.printlnWarning(MessageUtils.NO_ENEMIES_IN_RANGE);
        }
    }
}
