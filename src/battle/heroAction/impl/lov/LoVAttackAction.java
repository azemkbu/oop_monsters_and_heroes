package battle.heroAction.impl.lov;

import battle.heroAction.BattleContext;
import battle.heroAction.helper.LoVRangeUtils;
import battle.heroAction.impl.BaseAttackAction;
import battle.menu.BattleMenu;
import hero.Hero;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;

import java.util.List;
import java.util.stream.Collectors;

import static utils.GameConstants.HERO_ATTACK_MULTIPLIER;

/**
 * Attack action implementation for the Legends of Valor game
 */
public class LoVAttackAction extends BaseAttackAction {

    private final ILegendsWorldMap worldMap;

    public LoVAttackAction(ILegendsWorldMap worldMap) {
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
                .filter(monster -> LoVRangeUtils.isWithinRangeToAttack(hero, monster))
                .collect(Collectors.toList());
    }

    @Override
    public int getCalculatedDamage(Hero hero, BattleMenu menu, IOUtils ioUtils) {
        // Apply query-based terrain bonuses (stateless multipliers from the current tile).
        int weaponDamage = 0;

        if (hero.getEquippedWeapon() != null) {
            weaponDamage = hero.getEquippedWeapon().getDamage();

            if (hero.getEquippedWeapon().getHandsRequired() == 1) {
                int hands = menu.chooseHandsForWeapon(hero, hero.getEquippedWeapon());
                if (hands == 2) {
                    weaponDamage = (int) Math.round(
                            weaponDamage * utils.GameConstants.ONE_HANDED_WEAPON_BONUS_MULTIPLIER
                    );
                }
            } else {
                ioUtils.printlnSuccess(String.format(
                        utils.MessageUtils.TWO_HANDS_ITEM_MESSAGE,
                        hero.getEquippedWeapon().getName()
                ));
            }
        }

        double strengthMultiplier = 1.0;
        if (worldMap != null) {
            Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
            if (tile != null) {
                strengthMultiplier = tile.getStrengthMultiplier();
            }
        }

        int effectiveStrength = (int) Math.round(hero.getStrength() * strengthMultiplier);
        double raw = (effectiveStrength + weaponDamage) * HERO_ATTACK_MULTIPLIER;
        return (int) Math.round(raw);
    }

    @Override
    protected void handleNoAvailableMonsters(Hero hero,
                                             List<Monster> monsters,
                                             BattleContext context,
                                             IOUtils ioUtils) {
        if (monsters == null || monsters.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_ATTACK);
        } else {
            ioUtils.printlnWarning(MessageUtils.NO_ENEMIES_IN_RANGE);
        }
    }
}
