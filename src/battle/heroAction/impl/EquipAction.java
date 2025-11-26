package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.menu.BattleMenu;
import battle.enums.EquipChoice;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import market.model.item.Armor;
import market.model.item.Item;
import market.model.item.ItemType;
import market.model.item.Weapon;

import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the equip action during {@link Hero} turn
 */
public class EquipAction implements HeroActionStrategy {

    @Override
    public void execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ioUtils) {

        BattleMenu menu = context.getBattleMenu();
        EquipChoice choice = menu.chooseEquipAction(hero);

        switch (choice) {
            case WEAPON:
                equipWeaponFromInventory(hero, menu, ioUtils);
                break;
            case ARMOR:
                equipArmorFromInventory(hero, menu, ioUtils);
                break;
            case CANCEL:
            default:
                ioUtils.printlnFail(MessageUtils.CANCELED);
                break;
        }
    }

    private void equipWeaponFromInventory(Hero hero, BattleMenu menu, IOUtils ioUtils) {
        List<Weapon> weapons = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Weapon) {
                weapons.add((Weapon) item);
            }
        }

        if (weapons.isEmpty()) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_TO_EQUIP, ItemType.WEAPON.name()));
            return;
        }

        Weapon chosen = menu.chooseWeaponToEquip(hero, weapons);
        if (chosen == null) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.WEAPON.name()));
            return;
        }

        hero.equipWeapon(chosen);
        ioUtils.printlnTitle(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), chosen.getName()));
    }

    private void equipArmorFromInventory(Hero hero, BattleMenu menu, IOUtils ioUtils) {
        List<Armor> armors = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Armor) {
                armors.add((Armor) item);
            }
        }

        if (armors.isEmpty()) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_TO_EQUIP, ItemType.ARMOR.name()));
            return;
        }

        Armor chosen = menu.chooseArmorToEquip(hero, armors);
        if (chosen == null) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.ARMOR.name()));
            return;
        }

        hero.equipArmor(chosen);
        ioUtils.printlnTitle(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), chosen.getName()));
    }
}
