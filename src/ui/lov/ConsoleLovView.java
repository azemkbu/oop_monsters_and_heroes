package ui.lov;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import market.model.Market;
import market.model.item.Armor;
import market.model.item.ItemType;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import market.service.MarketService;
import market.service.MarketServiceImpl;
import market.ui.MarketMenu;
import market.ui.MarketMenuImpl;
import monster.Monster;
import ui.formatter.LegendsMapFormatter;
import ui.formatter.LineKind;
import ui.formatter.RenderedLine;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.ILegendsWorldMap;
import worldMap.enums.Direction;

import java.util.List;

/**
 * Console implementation of {@link LovView}.
 */
public final class ConsoleLovView implements LovView {
    private final IOUtils io;
    private final ILegendsWorldMap worldMap;
    private final LegendsMapFormatter mapFormatter;

    public ConsoleLovView(IOUtils io, ILegendsWorldMap worldMap) {
        this(io, worldMap, new LegendsMapFormatter());
    }

    public ConsoleLovView(IOUtils io,
                          ILegendsWorldMap worldMap,
                          LegendsMapFormatter mapFormatter) {
        this.io = io;
        this.worldMap = worldMap;
        this.mapFormatter = mapFormatter;
    }

    @Override
    public void showStarting() {
        io.printlnHeader("Starting Legends of Valor...");
    }

    @Override
    public void showRoundHeader(int round) {
        io.printlnHeader("===== Round " + round + " =====");
    }

    @Override
    public void refreshDisplay(int round, List<Hero> heroes, List<Monster> monsters) {
        // Clear screen first
        io.clearScreen();

        // Show round header
        showRoundHeader(round);

        // Render map
        renderMap();

        // Show status panels
        showHeroesAndMonstersStatus(heroes, monsters);
    }

    @Override
    public void renderMap() {
        List<RenderedLine> lines = mapFormatter.render(worldMap);
        for (RenderedLine line : lines) {
            if (line.getKind() == LineKind.HEADER) {
                io.printlnHeader(line.getText());
            } else {
                io.printlnTitle(line.getText());
            }
        }
    }

    @Override
    public boolean promptContinueOrQuit() {
        // Keep the LoV UI identical to the original version: no "press enter to continue" prompt.
        // The game continues immediately each round; quitting is handled by outer application / EOF.
        return true;
    }

    @Override
    public void showHeroesAndMonstersStatus(List<Hero> heroes, List<Monster> monsters) {
        io.printlnTitle(MessageUtils.DEFAULT_LINE_HEADER);
        io.printlnHeader("======= STATUS =======");
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

        HeroActionType[] options = HeroActionType.values();
        for (int i = 0; i < options.length; i++) {
            io.printlnTitle(String.format("%d) %s", i + 1, options[i].getLabel()));
        }
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(1, options.length);
        return options[choice - 1];
    }

    @Override
    public Direction promptDirection(String prompt, boolean allowCancel) {
        while (true) {
            io.printlnTitle(prompt);
            io.printlnTitle("  W = Up");
            io.printlnTitle("  S = Down");
            io.printlnTitle("  A = Left");
            io.printlnTitle("  D = Right");
            if (allowCancel) {
                io.printlnTitle("  Q = Cancel");
            }
            io.printPrompt("Enter direction (W/A/S/D" + (allowCancel ? " or Q" : "") + "): ");

            String line = io.readLine();
            if (line == null || line.trim().isEmpty()) {
                io.printlnFail("Invalid input, please use W/A/S/D" + (allowCancel ? " or Q." : "."));
                continue;
            }
            char input = Character.toUpperCase(line.trim().charAt(0));
            switch (input) {
                case 'W':
                    return Direction.UP;
                case 'S':
                    return Direction.DOWN;
                case 'A':
                    return Direction.LEFT;
                case 'D':
                    return Direction.RIGHT;
                case 'Q':
                    if (allowCancel) return null;
                    io.printlnFail("Invalid input, please use W/A/S/D.");
                    break;
                default:
                    io.printlnFail("Invalid input, please use W/A/S/D" + (allowCancel ? " or Q." : "."));
            }
        }
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
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(1, monsters.size());
        return monsters.get(choice - 1);
    }

    @Override
    public Hero promptTeleportTarget(Hero hero, List<Hero> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }
        io.printlnTitle("Choose a hero to teleport near:");
        for (int i = 0; i < candidates.size(); i++) {
            Hero h = candidates.get(i);
            int[] pos = worldMap.getHeroPosition(h);
            io.printlnTitle(String.format(
                    "  [%d] %s  [lane=%d, row=%d, col=%d]",
                    i + 1,
                    h.getName(),
                    worldMap.getHeroLane(h),
                    pos == null ? -1 : pos[0],
                    pos == null ? -1 : pos[1]
            ));
        }
        io.printlnTitle(MessageUtils.CANCEL_LINE);
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, candidates.size());
        if (choice == 0) {
            return null;
        }
        return candidates.get(choice - 1);
    }

    @Override
    public Spell promptSpellToCast(Hero hero, List<Spell> spells) {
        if (spells == null || spells.isEmpty()) {
            return null;
        }
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
        if (potions == null || potions.isEmpty()) {
            return null;
        }
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
        io.printlnTitle(MessageUtils.ENTER_CHOICE);
        EquipChoice[] options = EquipChoice.values();
        for (int i = 0; i < options.length; i++) {
            io.printlnTitle(String.format("%d) %s", i + 1, options[i].getLabel()));
        }
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choiceIndex = io.readIntInRange(1, options.length);
        return options[choiceIndex - 1];
    }

    @Override
    public Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons) {
        if (weapons == null || weapons.isEmpty()) {
            return null;
        }
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
        if (armors == null || armors.isEmpty()) {
            return null;
        }
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
        if (weapon.getHandsRequired() == 2) {
            return 2;
        }

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
    public boolean maybeEnterMarket(Hero hero, Market market) {
        if (market == null) {
            return false;
        }

        // Match the original LOV prompt format exactly.
        io.printPrompt("Enter market for " + hero.getName() + "? (y/n): ");
        String line = io.readLine();
        if (line == null || line.trim().isEmpty()) {
            return false;
        }
        char c = Character.toLowerCase(line.trim().charAt(0));
        if (c != 'y') {
            return false;
        }

        MarketService marketService = new MarketServiceImpl(market);
        MarketMenu marketMenu = new MarketMenuImpl(marketService, io);
        marketMenu.runMarketSession(hero);
        return false;
    }

    @Override
    public void showSuccess(String message) {
        io.printlnSuccess(message);
    }

    @Override
    public void showFail(String message) {
        io.printlnFail(message);
    }

    @Override
    public void showWarning(String message) {
        io.printlnWarning(message);
    }
}


