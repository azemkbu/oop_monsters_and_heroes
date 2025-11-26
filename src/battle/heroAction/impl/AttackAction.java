package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.menu.BattleMenu;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import market.model.item.Weapon;
import monster.Monster;
import utils.MessageUtils;
import utils.GameConstants;
import utils.IOUtils;

import java.util.List;

import static utils.GameConstants.HERO_ATTACK_MULTIPLIER;

/**
 * Implementation of the attack action during {@link Hero} turn
 */
public class AttackAction implements HeroActionStrategy {

    @Override
    public void execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ioUtils) {

        BattleMenu menu = context.getBattleMenu();

        if (monsters.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_ATTACK);
            return;
        }

        Monster monster = menu.chooseMonsterTarget(hero, monsters);

        if (monster == null) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTER_SELECTED);
            return;
        }

        Weapon weapon = hero.getEquippedWeapon();

        int damage = getCalculatedDamage(hero, menu, ioUtils);

        if (monster.dodgesAttack()) {
            ioUtils.printlnWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, hero.getName(), monster.getName()));
            return;
        }


        int dealtDamage = monster.takeDamage(damage);
        ioUtils.printlnWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK, hero.getName(), monster.getName(), dealtDamage));

        if (weapon != null) {
            weapon.consumeUse();

            if (!weapon.isUsable()) {
                ioUtils.printlnFail(String.format(MessageUtils.ITEM_CAN_NO_LONGER_BE_USED, weapon.getName()));
            } else {
                ioUtils.printlnWarning(String.format(
                        MessageUtils.REMAINING_ITEM_USES,
                        weapon.getName(),
                        weapon.getUsesRemaining()
                ));
            }
        }

        if (!monster.isAlive()) {
            ioUtils.printlnWarning(String.format(MessageUtils.CHARACTER_DEFEATED, monster.getName()));
        }
    }

    public int getCalculatedDamage(Hero hero, BattleMenu menu, IOUtils ioUtils) {
        int weaponDamage = 0;

        Weapon weapon = hero.getEquippedWeapon();

        if (weapon != null) {
            weaponDamage = weapon.getDamage();

            if (weapon.getHandsRequired() == 1) {
                int hands = menu.chooseHandsForWeapon(hero, weapon);
                if (hands == 2) {
                    weaponDamage = (int) Math.round(
                            weaponDamage * GameConstants.ONE_HANDED_WEAPON_BONUS_MULTIPLIER
                    );
                }
            } else {
                ioUtils.printlnSuccess(String.format(MessageUtils.TWO_HANDS_ITEM_MESSAGE, weapon.getName()));
            }
        }

        double raw = (hero.getStrength() + weaponDamage) * HERO_ATTACK_MULTIPLIER;
        return (int) Math.round(raw);
    }
}
