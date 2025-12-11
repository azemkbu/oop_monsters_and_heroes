package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import battle.menu.BattleMenu;
import hero.Hero;
import market.model.item.Weapon;
import monster.Monster;
import utils.GameConstants;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.List;

import static utils.GameConstants.HERO_ATTACK_MULTIPLIER;

/**
 * Base implementation of the attack action during {@link Hero} turn
 */
public abstract class BaseAttackAction implements HeroActionStrategy {

    @Override
    public void execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ioUtils) {

        BattleMenu menu = context.getBattleMenu();

        List<Monster> availableMonsters = getAvailableMonsters(hero, monsters, context);

        if (availableMonsters == null || availableMonsters.isEmpty()) {
            handleNoAvailableMonsters(hero, monsters, context, ioUtils);
            return;
        }

        Monster monster = menu.chooseMonsterTarget(hero, availableMonsters);

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

    protected List<Monster> getAvailableMonsters(Hero hero,
                                                 List<Monster> monsters,
                                                 BattleContext context) {
        return monsters;
    }


    protected void handleNoAvailableMonsters(Hero hero,
                                             List<Monster> monsters,
                                             BattleContext context,
                                             IOUtils ioUtils) {
        ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_ATTACK);
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
