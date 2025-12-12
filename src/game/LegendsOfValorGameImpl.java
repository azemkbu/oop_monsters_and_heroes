package game;

import battle.enums.HeroActionType;
import battle.heroAction.BattleActionsConfig;
import battle.heroAction.BattleContext;
import battle.heroAction.GameType;
import battle.heroAction.HeroActionStrategy;
import battle.menu.BattleMenu;
import hero.Hero;
import hero.Party;
import market.model.Market;
import market.service.MarketService;
import market.service.MarketServiceImpl;
import market.ui.MarketMenu;
import market.ui.MarketMenuImpl;
import monster.Monster;
import monster.MonsterFactory;
import utils.GameConstants;
import utils.IOUtils;
import utils.MessageUtils;
import worldMap.LegendsOfValorWorldMap;
import worldMap.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private final BattleMenu battleMenu;
    private final MonsterFactory monsterFactory;
    private final IOUtils io;

    private int round = 1;
    private boolean running = true;

    public LegendsOfValorGameImpl(LegendsOfValorWorldMap worldMap,
                                  Party party,
                                  BattleMenu battleMenu,
                                  MonsterFactory monsterFactory,
                                  IOUtils io) {
        this.worldMap = worldMap;
        this.party = party;
        this.battleMenu = battleMenu;
        this.monsterFactory = monsterFactory;
        this.io = io;
    }

    public void start() {
        io.printlnHeader("Starting Legends of Valor...");

        // Spawn the initial wave (one monster per lane)
        spawnMonstersAllLanes();

        Map<HeroActionType, HeroActionStrategy> actions =
                BattleActionsConfig.createActions(GameType.LEGENDS_OF_VALOR, worldMap, io);
        BattleContext context = new BattleContext(battleMenu);

        while (running) {
            io.printlnHeader("===== Round " + round + " =====");
            worldMap.printMap();
            if (wantsQuitThisRound()) {
                io.printlnSuccess("Quitting Legends of Valor. Goodbye!");
                return;
            }

            if (worldMap.isHeroVictory()) {
                io.printlnSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                io.printlnFail("Monsters win!");
                return;
            }

            runHeroesTurn(actions, context);

            if (worldMap.isHeroVictory()) {
                io.printlnSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                io.printlnFail("Monsters win!");
                return;
            }

            runMonstersTurn();

            if (worldMap.isHeroVictory()) {
                io.printlnSuccess("Heroes win! A hero reached the Monster Nexus.");
                return;
            }
            if (worldMap.isMonsterVictory() || party.allHeroesDefeated()) {
                io.printlnFail("Monsters win!");
                return;
            }

            if (round % GameConstants.LOV_MONSTER_SPAWN_INTERVAL == 0) {
                io.printlnWarning("A new wave of monsters is spawning!");
                spawnMonstersAllLanes();
            }

            round++;
        }
    }

    private void runHeroesTurn(Map<HeroActionType, HeroActionStrategy> actions, BattleContext context) {
        List<Hero> heroes = party.getHeroes();

        for (Hero hero : heroes) {
            if (!hero.isAlive()) {
                continue;
            }

            maybeEnterMarket(hero);

            List<Monster> aliveMonsters = worldMap.getAliveMonsters();
            battleMenu.showBattleStatus(worldMap.getAliveHeroes(), aliveMonsters);

            HeroActionType actionType = battleMenu.chooseActionForHero(hero, aliveMonsters);
            if (actionType == HeroActionType.SKIP) {
                io.printlnWarning(String.format(MessageUtils.SKIP_TURN, hero.getName()));
                continue;
            }

            HeroActionStrategy strategy = actions.get(actionType);
            if (strategy == null) {
                io.printlnFail(MessageUtils.UNKNOWN_COMMAND);
                continue;
            }

            strategy.execute(hero, aliveMonsters, context, io);

            cleanupDeadMonstersAndReward(hero);

            if (worldMap.isHeroVictory()) {
                return;
            }
            if (party.allHeroesDefeated()) {
                return;
            }
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
                Hero target = targets.get((int) (Math.random() * targets.size()));
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
            io.printlnWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, monster.getName(), hero.getName()));
            return;
        }

        hero.takeDamage(damage);
        io.printlnWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK, monster.getName(), hero.getName(), damage));

        if (!hero.isAlive()) {
            io.printlnWarning(String.format(MessageUtils.CHARACTER_FAINTED, hero.getName()));
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

        return Math.random() < chance;
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

    private void maybeEnterMarket(Hero hero) {
        Tile tile = worldMap.getTile(hero.getRow(), hero.getCol());
        if (tile == null) {
            return;
        }

        Market market = tile.getMarket();
        if (market == null) {
            return;
        }

        io.printPrompt("Enter market for " + hero.getName() + "? (y/n, q to quit): ");
        String line = io.readLine();
        if (line.trim().isEmpty()) {
            return;
        }
        char c = Character.toLowerCase(line.trim().charAt(0));
        if (c == 'q') {
            io.printlnSuccess("Quitting Legends of Valor. Goodbye!");
            running = false;
            return;
        }
        if (c != 'y') {
            return;
        }

        MarketService marketService = new MarketServiceImpl(market, io);
        MarketMenu marketMenu = new MarketMenuImpl(marketService, io);
        marketMenu.runMarketSession(hero);
    }

    private boolean wantsQuitThisRound() {
        io.printPrompt("Press ENTER to continue, or Q to quit: ");
        String line = io.readLine();
        String trimmed = line.trim();
        return !trimmed.isEmpty() && (trimmed.equalsIgnoreCase("q"));
    }
}


