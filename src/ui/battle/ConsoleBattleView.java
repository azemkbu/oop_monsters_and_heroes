package ui.battle;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import market.model.item.*;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Console battle view implementation using {@link IOUtils}.
 */
public final class ConsoleBattleView implements BattleView {
    private final IOUtils io;

    public ConsoleBattleView(IOUtils io) {
        this.io = io;
    }

    @Override
    public void showBattleStatus(List<Hero> heroes, List<Monster> monsters) {
        io.printlnTitle(MessageUtils.DEFAULT_LINE_HEADER);
        io.printlnHeader(MessageUtils.BATTLE_INFO_HEADER);
        io.printlnTitle("Heroes:");
        for (Hero h : heroes) {
            io.printlnTitle("  - " + h);
        }
        io.printlnTitle("Monsters:");
        for (Monster m : monsters) {
            io.printlnTitle("  - " + m);
        }
        io.printlnHeader(MessageUtils.DEFAULT_LINE_HEADER);
    }

    @Override
    public HeroActionType promptHeroAction(Hero hero, List<Monster> monsters) {
        io.printlnSuccess(String.format(MessageUtils.CURRENT_TURN, hero.getName()));
        List<HeroActionType> options = Arrays.asList(HeroActionType.values());
        for (int i = 0; i < options.size(); i++) {
            HeroActionType action = options.get(i);
            io.printlnTitle(String.format("%d) %s", i + 1, action.getLabel()));
        }
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(1, options.size());
        return options.get(choice - 1);
    }

    @Override
    public Monster promptMonsterTarget(Hero hero, List<Monster> monsters) {
        if (monsters == null || monsters.isEmpty()) {
            return null;
        }
        io.printPrompt(MessageUtils.CHOOSE_MONSTER_TO_TARGET_MESSAGE);
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Level %d, HP %d)",
                    i + 1,
                    m.getName(),
                    m.getLevel(),
                    m.getHp()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, monsters.size());
        if (choice == 0) return null;
        return monsters.get(choice - 1);
    }

    @Override
    public Spell promptSpellToCast(Hero hero, List<Spell> spells) {
        if (spells == null || spells.isEmpty()) return null;
        io.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.SPELL));
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Damage %d, ManaCost %d, Level %d)",
                    i + 1,
                    s.getName(),
                    s.getDamage(),
                    s.getManaCost(),
                    s.getLevel()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, spells.size());
        if (choice == 0) return null;
        return spells.get(choice - 1);
    }

    @Override
    public Potion promptPotionToUse(Hero hero, List<Potion> potions) {
        if (potions == null || potions.isEmpty()) return null;
        io.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.POTION));
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Type %s, Effect %.0f, Level %d)",
                    i + 1,
                    p.getName(),
                    p.getStatType(),
                    p.getEffectAmount(),
                    p.getLevel()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, potions.size());
        if (choice == 0) return null;
        return potions.get(choice - 1);
    }

    @Override
    public EquipChoice promptEquipChoice(Hero hero) {
        List<EquipChoice> options = Arrays.asList(EquipChoice.values());
        for (int i = 0; i < options.size(); i++) {
            EquipChoice choice = options.get(i);
            io.printlnTitle(String.format("%d) %s", i + 1, choice.getLabel()));
        }
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choiceIndex = io.readIntInRange(1, options.size());
        return options.get(choiceIndex - 1);
    }

    @Override
    public Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons) {
        if (weapons == null || weapons.isEmpty()) return null;
        io.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.WEAPON));
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Damage %d, Level %d)",
                    i + 1,
                    w.getName(),
                    w.getDamage(),
                    w.getLevel()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, weapons.size());
        if (choice == 0) return null;
        return weapons.get(choice - 1);
    }

    @Override
    public Armor promptArmorToEquip(Hero hero, List<Armor> armors) {
        if (armors == null || armors.isEmpty()) return null;
        io.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.ARMOR));
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            io.printlnTitle(String.format(
                    "  [%d] %s (Reduction %d, Level %d)",
                    i + 1,
                    a.getName(),
                    a.getDamageReduction(),
                    a.getLevel()
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, armors.size());
        if (choice == 0) return null;
        return armors.get(choice - 1);
    }

    @Override
    public int promptHandsForWeapon(Hero hero, Weapon weapon) {
        if (weapon.getHandsRequired() == 2) return 2;

        while (true) {
            io.printlnHeader(
                    hero.getName() + " is using " + weapon.getName()
                            + " (one-handed).\n"
                            + "Choose how to wield it:\n"
                            + "  [1] One hand (normal damage)\n"
                            + "  [2] Two hands (increased weapon damage)"
            );
            io.printPrompt(MessageUtils.ENTER_CHOICE);
            Integer input = io.readInteger();
            if (input != null && (input == 1 || input == 2)) {
                return input;
            }
            io.printlnFail(MessageUtils.UNKNOWN_COMMAND);
        }
    }

    @Override
    public void showSuccess(String msg) {
        io.printlnSuccess(msg);
    }

    @Override
    public void showWarning(String msg) {
        io.printlnWarning(msg);
    }

    @Override
    public void showFail(String msg) {
        io.printlnFail(msg);
    }
}


