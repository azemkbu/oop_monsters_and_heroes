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
 * Uses letter commands (W/A/S/D for movement, K for attack, etc.) instead of number menus.
 */
public final class ConsoleLovView implements LovView {
    private final IOUtils io;
    private final ILegendsWorldMap worldMap;
    private final LegendsMapFormatter mapFormatter;

    // Stores the direction from last WASD input (used when MOVE is returned)
    private Direction lastMoveDirection = null;

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
        io.clearScreen();
        showRoundHeader(round);
        renderMap();
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
    public HeroActionType promptHeroAction(Hero hero, List<Monster> monsters, boolean isOnNexus) {
        // Show hero turn header
        io.printlnSuccess(String.format("═══════ %s's Turn ═══════", hero.getName()));

        // Show command options
        io.printlnTitle(" Movement: [W]Up  [S]Down  [A]Left  [D]Right");
        io.printlnTitle(" Combat:   [K]Attack  [C]Cast Spell  [P]Potion  [E]Equip");
        if (isOnNexus) {
            io.printlnTitle(" Special:  [T]Teleport  [B]Recall  [M]Market  [R]Remove Obstacle");
        } else {
            io.printlnTitle(" Special:  [T]Teleport  [B]Recall  [R]Remove Obstacle");
        }
        io.printlnTitle(" Other:    [I]Info  [Q]Quit");

        while (true) {
            io.printPrompt("Enter command: ");
            String line = io.readLine();
            if (line == null || line.trim().isEmpty()) {
                io.printlnFail("Please enter a command.");
                continue;
            }

            char cmd = Character.toUpperCase(line.trim().charAt(0));
            switch (cmd) {
                // Movement - WASD directly moves
                case 'W':
                    lastMoveDirection = Direction.UP;
                    return HeroActionType.MOVE;
                case 'A':
                    lastMoveDirection = Direction.LEFT;
                    return HeroActionType.MOVE;
                case 'S':
                    lastMoveDirection = Direction.DOWN;
                    return HeroActionType.MOVE;
                case 'D':
                    lastMoveDirection = Direction.RIGHT;
                    return HeroActionType.MOVE;

                // Combat
                case 'K':
                    return HeroActionType.ATTACK;
                case 'C':
                    return HeroActionType.CAST_SPELL;
                case 'P':
                    return HeroActionType.USE_POTION;
                case 'E':
                    return HeroActionType.EQUIP;

                // Special
                case 'T':
                    return HeroActionType.TELEPORT;
                case 'B':
                    return HeroActionType.RECALL;
                case 'M':
                    if (isOnNexus) {
                        return HeroActionType.MARKET;
                    }
                    io.printlnFail("Market is only available at Nexus!");
                    break;
                case 'R':
                    return HeroActionType.REMOVE_OBSTACLE;

                // Other
                case 'I':
                    showHeroInfo(hero);
                    break;
                case 'Q':
                    return null; // Signal to quit

                default:
                    io.printlnFail("Unknown command. Use W/A/S/D to move, K to attack, etc.");
            }
        }
    }

    private void showHeroInfo(Hero hero) {
        io.printlnHeader("═══════ HERO INFO ═══════");
        io.printlnTitle("  Name: " + hero.getName());
        io.printlnTitle("  Class: " + hero.getHeroClassName());
        io.printlnTitle("  Level: " + hero.getLevel());
        io.printlnTitle("  HP: " + hero.getHp() + "/" + hero.getMaxHp());
        io.printlnTitle("  MP: " + hero.getMp() + "/" + hero.getMaxMp());
        io.printlnTitle("  Strength: " + hero.getStrength());
        io.printlnTitle("  Dexterity: " + hero.getDexterity());
        io.printlnTitle("  Agility: " + hero.getAgility());
        io.printlnTitle("  Gold: " + hero.getGold());
        io.printlnTitle("  Weapon: " + (hero.getEquippedWeapon() != null ? hero.getEquippedWeapon().getName() : "None"));
        io.printlnTitle("  Armor: " + (hero.getEquippedArmor() != null ? hero.getEquippedArmor().getName() : "None"));
        io.printlnHeader("═════════════════════════");
    }

    @Override
    public Direction getLastMoveDirection() {
        return lastMoveDirection;
    }

    @Override
    public Direction promptDirection(String prompt, boolean allowCancel) {
        while (true) {
            io.printlnTitle(prompt);
            io.printlnTitle("  W = Up, S = Down, A = Left, D = Right" + (allowCancel ? ", Q = Cancel" : ""));
            io.printPrompt("Enter direction: ");

            String line = io.readLine();
            if (line == null || line.trim().isEmpty()) {
                io.printlnFail("Please enter a direction.");
                continue;
            }
            char input = Character.toUpperCase(line.trim().charAt(0));
            switch (input) {
                case 'W': return Direction.UP;
                case 'S': return Direction.DOWN;
                case 'A': return Direction.LEFT;
                case 'D': return Direction.RIGHT;
                case 'Q':
                    if (allowCancel) return null;
                    // fall through
                default:
                    io.printlnFail("Invalid input.");
            }
        }
    }

    @Override
    public Monster promptMonsterTarget(Hero hero, List<Monster> monsters) {
        if (monsters == null || monsters.isEmpty()) {
            return null;
        }
        io.printlnTitle("Choose a monster to target:");
        for (int i = 0; i < monsters.size(); i++) {
            Monster m = monsters.get(i);
            io.printlnTitle(String.format("  [%d] %s (Level %d, HP %d)",
                    i + 1, m.getName(), m.getLevel(), m.getHp()));
        }
        io.printlnTitle("  [0] Cancel");
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, monsters.size());
        if (choice == 0) return null;
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
            io.printlnTitle(String.format("  [%d] %s  [lane=%d, row=%d, col=%d]",
                    i + 1, h.getName(), worldMap.getHeroLane(h),
                    pos == null ? -1 : pos[0], pos == null ? -1 : pos[1]));
        }
        io.printlnTitle("  [0] Cancel");
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, candidates.size());
        if (choice == 0) return null;
        return candidates.get(choice - 1);
    }

    @Override
    public Spell promptSpellToCast(Hero hero, List<Spell> spells) {
        if (spells == null || spells.isEmpty()) {
            return null;
        }
        io.printlnHeader("Choose a spell to cast:");
        for (int i = 0; i < spells.size(); i++) {
            Spell s = spells.get(i);
            io.printlnTitle(String.format("  [%d] %s (Damage %d, ManaCost %d)",
                    i + 1, s.getName(), s.getDamage(), s.getManaCost()));
        }
        io.printlnTitle("  [0] Cancel");
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
        io.printlnHeader("Choose a potion to use:");
        for (int i = 0; i < potions.size(); i++) {
            Potion p = potions.get(i);
            io.printlnTitle(String.format("  [%d] %s (%s +%.0f)",
                    i + 1, p.getName(), p.getStatType(), p.getEffectAmount()));
        }
        io.printlnTitle("  [0] Cancel");
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, potions.size());
        if (choice == 0) return null;
        return potions.get(choice - 1);
    }

    @Override
    public EquipChoice promptEquipChoice(Hero hero) {
        io.printlnTitle("What do you want to equip?");
        io.printlnTitle("  [1] Weapon");
        io.printlnTitle("  [2] Armor");
        io.printlnTitle("  [0] Cancel");
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        int choice = io.readIntInRange(0, 2);
        switch (choice) {
            case 1: return EquipChoice.WEAPON;
            case 2: return EquipChoice.ARMOR;
            default: return EquipChoice.CANCEL;
        }
    }

    @Override
    public Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons) {
        if (weapons == null || weapons.isEmpty()) {
            return null;
        }
        io.printlnHeader("Choose a weapon to equip:");
        for (int i = 0; i < weapons.size(); i++) {
            Weapon w = weapons.get(i);
            io.printlnTitle(String.format("  [%d] %s (Damage %d, Hands %d)",
                    i + 1, w.getName(), w.getDamage(), w.getHandsRequired()));
        }
        io.printlnTitle("  [0] Cancel");
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
        io.printlnHeader("Choose armor to equip:");
        for (int i = 0; i < armors.size(); i++) {
            Armor a = armors.get(i);
            io.printlnTitle(String.format("  [%d] %s (Reduction %d)",
                    i + 1, a.getName(), a.getDamageReduction()));
        }
        io.printlnTitle("  [0] Cancel");
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
        io.printlnHeader(hero.getName() + " is using " + weapon.getName() + " (one-handed).");
        io.printlnTitle("  [1] One hand (normal damage)");
        io.printlnTitle("  [2] Two hands (increased damage)");
        io.printPrompt(MessageUtils.ENTER_CHOICE);
        return io.readIntInRange(1, 2);
    }

    @Override
    public void runMarketSession(Hero hero, Market market) {
        if (market == null) {
            io.printlnFail("No market available here!");
            return;
        }
        MarketService marketService = new MarketServiceImpl(market);
        MarketMenu marketMenu = new MarketMenuImpl(marketService, io);
        marketMenu.runMarketSession(hero);
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
