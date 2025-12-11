package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.helper.LoVRangeUtils;
import battle.heroAction.impl.BaseCastSpellAction;
import hero.Hero;
import market.model.item.Spell;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cast spell action implementation for the Legends of Valor
 */
public class LoVCastSpellAction extends BaseCastSpellAction {

    private final ILegendsWorldMap worldMap;

    public LoVCastSpellAction(ILegendsWorldMap worldMap) {
        this.worldMap = worldMap;
    }

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
    protected int calculateFinalDamage(Hero hero, Monster monster, Spell spell) {
        double dexMultiplier = 1.0;
        if (worldMap != null) {
            Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
            if (tile != null) {
                dexMultiplier = tile.getDexterityMultiplier();
            }
        }

        double baseDamage = spell.getDamage();
        double dexterity = hero.getDexterity() * dexMultiplier;
        double spellDamage = baseDamage
                + (dexterity / utils.GameConstants.HERO_SPELL_DEX_DIVISOR) * baseDamage;

        double effectiveDamage = spellDamage - monster.getDefense();
        return (int) Math.max(0, Math.round(effectiveDamage));
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
