package game;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import hero.Party;
import java.util.ArrayList;
import java.util.List;
import lov.usecase.LovActionExecutor;
import lov.usecase.LovActionRequest;
import lov.usecase.LovActionResult;
import lov.usecase.requests.AttackRequest;
import lov.usecase.requests.CastSpellRequest;
import lov.usecase.requests.EquipRequest;
import lov.usecase.requests.MoveRequest;
import lov.usecase.requests.RecallRequest;
import lov.usecase.requests.RemoveObstacleRequest;
import lov.usecase.requests.TeleportRequest;
import lov.usecase.requests.UsePotionRequest;
import market.model.Market;
import market.model.item.Armor;
import market.model.item.Item;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import monster.Monster;
import monster.IMonsterFactory;
import ui.lov.LovView;
import utils.GameConstants;
import utils.MessageUtils;
import worldMap.LegendsOfValorWorldMap;
import worldMap.Tile;
import worldMap.enums.Direction;
import java.util.Random;

/**
 * Main game loop for Legends of Valor.
 *
 * This mode is round-based:
 * - Heroes take turns (attack/cast/move/teleport/recall/etc.)
 * - Monsters then attack if in range, otherwise advance south
 * - Every N rounds new monsters spawn at the Monster Nexus
 *
 * Win conditions:
 * - Heroes win if any hero reaches the Monster Nexus row
 * - Monsters win if any monster reaches the Hero Nexus row, or all heroes are defeated
 */
public class LegendsOfValorGameImpl {

    private final LegendsOfValorWorldMap worldMap;
    private final Party party;
    private final IMonsterFactory monsterFactory;
    private final LovView view;
    private final LovActionExecutor actionExecutor;
    private final Random rng;

    private int round = 1;
    private boolean running = true;

    public LegendsOfValorGameImpl(LegendsOfValorWorldMap worldMap,
                                  Party party,
                                  IMonsterFactory monsterFactory,
                                  LovView view) {
        this(worldMap, party, monsterFactory, view, new Random());
    }

    public LegendsOfValorGameImpl(LegendsOfValorWorldMap worldMap,
                                  Party party,
                                  IMonsterFactory monsterFactory,
                                  LovView view,
                                  Random rng) {
        this.worldMap = worldMap;
        this.party = party;
        this.monsterFactory = monsterFactory;
        this.view = view;
        this.rng = (rng == null) ? new Random() : rng;
        this.actionExecutor = new LovActionExecutor(worldMap, this.rng);
    }

    public void start() {
        view.showStarting();

        // Spawn the initial wave (one monster per lane)
        spawnMonstersAllLanes();

        while (running) {
            view.showRoundHeader(round);
            view.renderMap();
            if (!view.promptContinueOrQuit()) {
                view.showSuccess("Quitting Legends of Valor. Goodbye!");
                return;
            }

            if (worldMap.isHeroVictory()) {
                view.showSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                view.showFail("Monsters win!");
                return;
            }

            runHeroesTurn();
            if (!running) {
                return;
            }

            if (worldMap.isHeroVictory()) {
                view.showSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                view.showFail("Monsters win!");
                return;
            }

            runMonstersTurn();
            if (!running) {
                return;
            }

            if (worldMap.isHeroVictory()) {
                view.showSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                view.showFail("Monsters win!");
                return;
            }

            if (round % GameConstants.LOV_MONSTER_SPAWN_INTERVAL == 0) {
                view.showWarning("A new wave of monsters is spawning!");
                spawnMonstersAllLanes();
            }

            round++;
        }
    }

    private void runHeroesTurn() {
        List<Hero> heroes = party.getHeroes();

        for (Hero hero : heroes) {
            if (!hero.isAlive()) {
                continue;
            }

            List<Monster> aliveMonsters = worldMap.getAliveMonsters();
            if (maybeEnterMarket(hero)) {
                view.showSuccess("Quitting Legends of Valor. Goodbye!");
                return;
            }
            view.showHeroesAndMonstersStatus(worldMap.getAliveHeroes(), aliveMonsters);

            HeroActionType actionType = view.promptHeroAction(hero, aliveMonsters);
            LovActionRequest request = buildRequestForAction(actionType, hero, aliveMonsters);
            LovActionResult result = actionExecutor.execute(actionType, hero, aliveMonsters, request);
            renderActionResult(result);
            if (result.shouldRenderMap()) {
                view.renderMap();
            }

            cleanupDeadMonstersAndReward(hero);

            if (worldMap.isHeroVictory()) {
                return;
            }
            if (party.allHeroesDefeated()) {
                return;
            }
        }
    }

    private void renderActionResult(LovActionResult result) {
        if (result == null) {
            view.showFail(MessageUtils.UNKNOWN_COMMAND);
            return;
        }
        for (String msg : result.getFailMessages()) {
            view.showFail(msg);
        }
        for (String msg : result.getWarningMessages()) {
            view.showWarning(msg);
        }
        for (String msg : result.getSuccessMessages()) {
            view.showSuccess(msg);
        }
    }

    private void runMonstersTurn() {
        List<Monster> monsters = new ArrayList<>(worldMap.getAliveMonsters());

        for (Monster monster : monsters) {
            if (!monster.isAlive()) {
                continue;
            }

            List<Hero> targets = worldMap.getHeroesInRange(monster);
            if (!targets.isEmpty()) {
                Hero target = targets.get(rng.nextInt(targets.size()));
                monsterAttack(monster, target);
                continue;
            }
            

            worldMap.moveMonsterSouth(monster);
            

        }

        // End-of-round recovery for alive heroes (keeps original feel)
        for (Hero hero : party.getHeroes()) {
            if (hero.isAlive()) {
                hero.recoverAfterRound();
            }
        }
    }

    private void monsterAttack(Monster monster, Hero hero) {
        int damage = monster.computeAttackDamage();
        boolean dodged = heroDodgesWithTerrain(hero);

        if (dodged) {
            view.showWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, monster.getName(), hero.getName()));
            return;
        }

        hero.takeDamage(damage);
        view.showWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK, monster.getName(), hero.getName(), damage));

        if (!hero.isAlive()) {
            view.showWarning(String.format(MessageUtils.CHARACTER_FAINTED, hero.getName()));
        }
    }

    private boolean heroDodgesWithTerrain(Hero hero) {
        Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
        double agiMultiplier = (tile != null) ? tile.getAgilityMultiplier() : 1.0;

        double agility = hero.getAgility() * agiMultiplier;
        double chance = agility * GameConstants.HERO_DODGE_MULTIPLIER;

        if (chance < 0.0) {
            chance = 0.0;
        }
        if (chance > GameConstants.MAX_DODGE_CHANCE) {
            chance = GameConstants.MAX_DODGE_CHANCE;
        }

        return rng.nextDouble() < chance;
    }

    private void cleanupDeadMonstersAndReward(Hero hero) {
        List<Monster> allMonsters = new ArrayList<>(worldMap.getMonsters());
        for (Monster monster : allMonsters) {
            if (monster.isAlive()) {
                continue;
            }

            // Reward the hero who took the action this turn (simple/consistent rule).
            hero.addGold(monster.getLevel() * GameConstants.LOV_GOLD_PER_MONSTER_LEVEL);
            hero.gainExperience(GameConstants.LOV_EXP_PER_MONSTER);

            worldMap.removeMonster(monster);
        }
    }

    private void spawnMonstersAllLanes() {
        // Create one monster per hero (party size should be 3 in LoV).
        List<Monster> monsters = monsterFactory.createMonstersForParty(party);
        int lanes = Math.min(LegendsOfValorWorldMap.LANE_COLUMNS.length, monsters.size());

        for (int lane = 0; lane < lanes; lane++) {
            worldMap.spawnMonster(monsters.get(lane), lane);
        }
    }

    private boolean maybeEnterMarket(Hero hero) {
        Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
        if (tile == null) {
            return false;
        }

        Market market = tile.getMarket();
        if (market == null) {
            return false;
        }
        boolean wantsQuit = view.maybeEnterMarket(hero, market);
        if (wantsQuit) {
            running = false;
            return true;
        }
        return false;
    }

    private LovActionRequest buildRequestForAction(HeroActionType actionType, Hero hero, List<Monster> aliveMonsters) {
        if (actionType == null) {
            return null;
        }

        switch (actionType) {
            case MOVE: {
                Direction dir = view.promptDirection("Choose where " + hero.getName() + " would like to move:", true);
                return new MoveRequest(dir);
            }
            case REMOVE_OBSTACLE: {
                Direction dir = view.promptDirection("Choose where " + hero.getName() + " would like to remove an obstacle:", true);
                return new RemoveObstacleRequest(dir);
            }
            case TELEPORT: {
                List<Hero> candidates = new ArrayList<>();
                for (Hero h : worldMap.getHeroes()) {
                    if (h != hero && h.isAlive()) {
                        candidates.add(h);
                    }
                }
                Hero target = view.promptTeleportTarget(hero, candidates);
                return new TeleportRequest(target);
            }
            case RECALL:
                return new RecallRequest();
            case ATTACK: {
                List<Monster> inRange = worldMap.getMonstersInRange(hero);
                Monster target = view.promptMonsterTarget(hero, inRange);
                Integer hands = null;
                Weapon weapon = hero.getEquippedWeapon();
                if (weapon != null && weapon.getHandsRequired() == 1) {
                    hands = view.promptHandsForWeapon(hero, weapon);
                }
                return new AttackRequest(target, hands);
            }
            case CAST_SPELL: {
                List<Spell> spells = collectSpells(hero);
                Spell spell = view.promptSpellToCast(hero, spells);
                List<Monster> inRange = worldMap.getMonstersInRange(hero);
                Monster target = view.promptMonsterTarget(hero, inRange);
                return new CastSpellRequest(spell, target);
            }
            case USE_POTION: {
                List<Potion> potions = collectPotions(hero);
                Potion potion = view.promptPotionToUse(hero, potions);
                return new UsePotionRequest(potion);
            }
            case EQUIP: {
                EquipChoice choice = view.promptEquipChoice(hero);
                if (choice == EquipChoice.WEAPON) {
                    List<Weapon> weapons = collectWeapons(hero);
                    Weapon w = view.promptWeaponToEquip(hero, weapons);
                    return new EquipRequest(choice, w, null);
                } else if (choice == EquipChoice.ARMOR) {
                    List<Armor> armors = collectArmors(hero);
                    Armor a = view.promptArmorToEquip(hero, armors);
                    return new EquipRequest(choice, null, a);
                }
                return new EquipRequest(choice, null, null);
            }
            case SKIP:
            default:
                return null;
        }
    }

    private List<Spell> collectSpells(Hero hero) {
        List<Spell> spells = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Spell) {
                spells.add((Spell) item);
            }
        }
        return spells;
    }

    private List<Potion> collectPotions(Hero hero) {
        List<Potion> potions = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Potion) {
                potions.add((Potion) item);
            }
        }
        return potions;
    }

    private List<Weapon> collectWeapons(Hero hero) {
        List<Weapon> weapons = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Weapon) {
                weapons.add((Weapon) item);
            }
        }
        return weapons;
    }

    private List<Armor> collectArmors(Hero hero) {
        List<Armor> armors = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Armor) {
                armors.add((Armor) item);
            }
        }
        return armors;
    }
}


