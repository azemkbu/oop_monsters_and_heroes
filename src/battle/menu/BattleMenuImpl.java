package battle.menu;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import hero.Party;
import market.model.item.*;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.Arrays;
import java.util.List;

import static utils.ConsoleColors.GREEN;

/**
 * Concrete implementation of {@link battle.menu.BattleMenu} interface
 */

public class BattleMenuImpl implements BattleMenu {

    private final IOUtils ioUtils;
    private final Party party; // Optional: for displaying hero indices in LoV

    public BattleMenuImpl(IOUtils ioUtils) {
        this(ioUtils, null);
    }

    public BattleMenuImpl(IOUtils ioUtils, Party party) {
        this.ioUtils = ioUtils;
        this.party = party;
    }

    @Override
    public HeroActionType chooseActionForHero(Hero hero, List<Monster> monsters) {
        // Display hero turn with index if party is available (Legends of Valor)
        String heroLabel = hero.getName();
        if (party != null) {
            int heroIndex = party.getHeroes().indexOf(hero);
            if (heroIndex >= 0) {
                heroLabel = "[H" + (heroIndex + 1) + "] " + hero.getName();
            }
        }
        ioUtils.printlnSuccess(String.format("=== %s's turn ===", heroLabel));

        List<HeroActionType> options = Arrays.asList(HeroActionType.values());

        for (int i = 0; i < options.size(); i++) {
            HeroActionType action = options.get(i);
            ioUtils.printlnTitle(String.format("%d) %s", i + 1, action.getLabel()));
        }

        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(1, options.size());
        return options.get(choice - 1);
    }


    @Override
    public Monster chooseMonsterTarget(Hero hero, List<Monster> monsters) {
        ioUtils.printPrompt(MessageUtils.CHOOSE_MONSTER_TO_TARGET_MESSAGE);
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Level %d, HP %d)",
                    i + 1,
                    m.getName(),
                    m.getLevel(),
                    m.getHp()
            ));
        }
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = ioUtils.readIntInRange(1, monsters.size());
        return monsters.get(choice - 1);
    }

    @Override
    public EquipChoice chooseEquipAction(Hero hero) {
        ioUtils.printlnTitle(MessageUtils.ENTER_CHOICE);

        List<EquipChoice> options = Arrays.asList(EquipChoice.values());

        for (int i = 0; i < options.size(); i++) {
            EquipChoice choice = options.get(i);
            ioUtils.printlnTitle(String.format("%d) %s", i + 1, choice.getLabel()));
        }

        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);
        int choiceIndex = ioUtils.readIntInRange(1, options.size());

        return options.get(choiceIndex - 1);
    }



    @Override
    public Weapon chooseWeaponToEquip(Hero hero, List<Weapon> weapons) {
        ioUtils.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.WEAPON));
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Damage %d, Level %d)",
                    i + 1,
                    w.getName(),
                    w.getDamage(),
                    w.getLevel()
            ));
        }

        ioUtils.printlnTitle(MessageUtils.CANCEL_LINE);
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(0, weapons.size());
        if (choice == 0) {
            return null;
        }
        return weapons.get(choice - 1);
    }

    @Override
    public Armor chooseArmorToEquip(Hero hero, List<Armor> armors) {
        ioUtils.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.ARMOR));
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Reduction %d, Level %d)",
                    i + 1,
                    a.getName(),
                    a.getDamageReduction(),
                    a.getLevel()
            ));
        }
        ioUtils.printlnTitle(MessageUtils.CANCEL_LINE);
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(0, armors.size());
        if (choice == 0) {
            return null;
        }
        return armors.get(choice - 1);
    }

    @Override
    public Spell chooseSpellToCast(Hero hero, List<Spell> spells) {
        ioUtils.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.SPELL));
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Damage %d, ManaCost %d, Level %d)",
                    i + 1,
                    s.getName(),
                    s.getDamage(),
                    s.getManaCost(),
                    s.getLevel()
            ));
        }
        ioUtils.printlnTitle(MessageUtils.CANCEL_LINE);
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(0, spells.size());
        if (choice == 0) {
            return null;
        }
        return spells.get(choice - 1);
    }

    @Override
    public Potion choosePotionToUse(Hero hero, List<Potion> potions) {
        ioUtils.printlnHeader(String.format(MessageUtils.CHOOSE_ITEM_TO_USE_BY_TYPE, ItemType.POTION));
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            ioUtils.printlnTitle(String.format(
                    "  [%d] %s (Type %s, Effect %.0s, Level %d)",
                    i + 1,
                    p.getName(),
                    p.getStatType(),
                    p.getEffectAmount(),
                    p.getLevel()
            ));
        }
        ioUtils.printlnTitle(MessageUtils.CANCEL_LINE);
        ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

        int choice = ioUtils.readIntInRange(0, potions.size());
        if (choice == 0) {
            return null;
        }
        return potions.get(choice - 1);
    }

    @Override
    public void showBattleStatus(List<Hero> heroes, List<Monster> monsters) {
        ioUtils.printlnTitle(MessageUtils.DEFAULT_LINE_HEADER);
        ioUtils.printlnHeader(MessageUtils.BATTLE_INFO_HEADER);
        ioUtils.printlnTitle(GREEN + "Heroes:");
        for (Hero h : heroes) {
            ioUtils.printlnTitle("  - " + h);
        }
        ioUtils.printlnTitle(GREEN + "Monsters:");
        for (Monster m : monsters) {
            ioUtils.printlnTitle("  - " + m);
        }
        ioUtils.printlnHeader(MessageUtils.DEFAULT_LINE_HEADER);
    }


    public int chooseHandsForWeapon(Hero hero, Weapon weapon) {
        if (weapon.getHandsRequired() == 2) {
            return 2;
        }

        while (true) {
            ioUtils.printlnHeader(
                    hero.getName() + " is using " + weapon.getName()
                            + " (one-handed).\n"
                            + "Choose how to wield it:\n"
                            + "  [1] One hand (normal damage)\n"
                            + "  [2] Two hands (increased weapon damage)"
            );
            ioUtils.printPrompt(MessageUtils.ENTER_CHOICE);

            Integer input = ioUtils.readInteger();

            if (input == 1 || input == 2) {
                return input;
            } else {
                ioUtils.printlnFail(MessageUtils.UNKNOWN_COMMAND);
            }
        }
    }
}
