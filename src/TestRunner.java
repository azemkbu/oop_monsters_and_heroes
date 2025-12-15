import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import battle.engine.BattleEngine;
import battle.engine.BattleEngineImpl;
import combat.RangeCalculator;
import game.LegendsOfValorGameImpl;
import hero.Hero;
import hero.Paladin;
import hero.Party;
import hero.Sorcerer;
import hero.Wallet;
import hero.Warrior;
import monster.Dragon;
import monster.Exoskeleton;
import monster.Spirit;
import utils.RangeConstants;
import lov.usecase.requests.AttackRequest;
import lov.usecase.LovActionExecutor;
import lov.usecase.LovActionResult;
import lov.usecase.requests.CastSpellRequest;
import lov.usecase.requests.EquipRequest;
import lov.usecase.requests.MoveRequest;
import lov.usecase.requests.RemoveObstacleRequest;
import lov.usecase.requests.TeleportRequest;
import lov.usecase.requests.UsePotionRequest;
import market.model.Market;
import market.model.item.Armor;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.SpellType;
import market.model.item.StatType;
import market.model.item.Weapon;
import market.service.MarketFactory;
import market.service.MarketServiceImpl;
import market.service.MarketResult;
import monster.IMonsterFactory;
import monster.Monster;
import monster.enums.MonsterAttribute;
import ui.battle.BattleView;
import ui.lov.LovView;
import java.util.Random;
import worldMap.LegendsOfValorWorldMap;
import worldMap.Tile;
import worldMap.enums.Direction;
import worldMap.enums.TileType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Minimal pure-Java test runner (no JUnit).
 *
 * Run with:
 *   javac -d out $(find src -name "*.java")
 *   java -ea -cp out TestRunner
 */
public class TestRunner {

    public static void main(String[] args) {
        List<String> failures = new ArrayList<>();
        int total = 0;

        total += run(failures, "tileObstacle_isAccessible_and_removeObstacle", TestRunner::tileObstacle_isAccessible_and_removeObstacle);
        total += run(failures, "lov_moveHero_blocksObstacle_and_monsterTile", TestRunner::lov_moveHero_blocksObstacle_and_monsterTile);
        total += run(failures, "lov_teleport_and_recall_basic", TestRunner::lov_teleport_and_recall_basic);
        total += run(failures, "lov_spawnMonster_doesNotStackOnSpawn", TestRunner::lov_spawnMonster_doesNotStackOnSpawn);
        total += run(failures, "lov_moveMonsterSouth_blockedByObstacle", TestRunner::lov_moveMonsterSouth_blockedByObstacle);

        // UseCase: all LovActionExecutor actions + key branches
        total += run(failures, "lov_action_move_cancel", TestRunner::lov_action_move_cancel);
        total += run(failures, "lov_action_move_blocked", TestRunner::lov_action_move_blocked);
        total += run(failures, "lov_action_move_success", TestRunner::lov_action_move_success);
        total += run(failures, "lov_action_teleport_cancel", TestRunner::lov_action_teleport_cancel);
        total += run(failures, "lov_action_teleport_success", TestRunner::lov_action_teleport_success);
        total += run(failures, "lov_action_recall_success", TestRunner::lov_action_recall_success);
        total += run(failures, "lov_action_removeObstacle_allBranches", TestRunner::lov_action_removeObstacle_allBranches);
        total += run(failures, "lov_action_attack_allBranches", TestRunner::lov_action_attack_allBranches);
        total += run(failures, "lov_action_castSpell_allBranches", TestRunner::lov_action_castSpell_allBranches);
        total += run(failures, "lov_action_usePotion_allBranches", TestRunner::lov_action_usePotion_allBranches);
        total += run(failures, "lov_action_equip_allBranches", TestRunner::lov_action_equip_allBranches);
        total += run(failures, "lov_action_skip", TestRunner::lov_action_skip);

        // Controller: main loop key paths (spawn cadence, victory, quit, market hook)
        total += run(failures, "lov_controller_quit_on_prompt", TestRunner::lov_controller_quit_on_prompt);
        total += run(failures, "lov_controller_initialSpawn_onePerLane", TestRunner::lov_controller_initialSpawn_onePerLane);
        total += run(failures, "lov_controller_spawnInterval_spawnsNewWave", TestRunner::lov_controller_spawnInterval_spawnsNewWave);
        total += run(failures, "lov_controller_heroVictory", TestRunner::lov_controller_heroVictory);
        total += run(failures, "lov_controller_monsterVictory", TestRunner::lov_controller_monsterVictory);
        total += run(failures, "lov_controller_marketQuit_stopsImmediately", TestRunner::lov_controller_marketQuit_stopsImmediately);
        total += run(failures, "lov_market_buySell_and_failBranches", TestRunner::lov_market_buySell_and_failBranches);
        total += run(failures, "lov_weapon_swap_changesDamageAndUses", TestRunner::lov_weapon_swap_changesDamageAndUses);
        total += run(failures, "lov_armor_reducesDamage_and_breaks_correctly", TestRunner::lov_armor_reducesDamage_and_breaks_correctly);
        total += run(failures, "lov_potion_strength_increasesAttackDamage", TestRunner::lov_potion_strength_increasesAttackDamage);
        total += run(failures, "lov_cooccupancy_heroCanMoveOntoMonsterCell", TestRunner::lov_cooccupancy_heroCanMoveOntoMonsterCell);
        total += run(failures, "lov_respawn_deadHero_nextRound_fullHpMp_and_backToNexus", TestRunner::lov_respawn_deadHero_nextRound_fullHpMp_and_backToNexus);

        // Battle system (BattleEngineImpl) tests
        total += run(failures, "battle_heroesWin_attack_rewardsApplied", TestRunner::battle_heroesWin_attack_rewardsApplied);
        total += run(failures, "battle_monstersWin_postBattleRecovery_revivesHero", TestRunner::battle_monstersWin_postBattleRecovery_revivesHero);
        total += run(failures, "battle_potion_then_attack_effect_applies", TestRunner::battle_potion_then_attack_effect_applies);
        total += run(failures, "battle_castSpell_consumesMp_and_appliesDebuff", TestRunner::battle_castSpell_consumesMp_and_appliesDebuff);

        // Attack range system tests
        total += run(failures, "range_heroClasses_haveDifferentBaseRange", TestRunner::range_heroClasses_haveDifferentBaseRange);
        total += run(failures, "range_monsterTypes_haveDifferentBaseRange", TestRunner::range_monsterTypes_haveDifferentBaseRange);
        total += run(failures, "range_weaponRangeBonus_extendsHeroRange", TestRunner::range_weaponRangeBonus_extendsHeroRange);
        total += run(failures, "range_getMonstersInRange_usesDynamicRange", TestRunner::range_getMonstersInRange_usesDynamicRange);

        if (!failures.isEmpty()) {
            System.err.println("FAILED (" + failures.size() + "):");
            for (String f : failures) {
                System.err.println(" - " + f);
            }
            System.exit(1);
        }

        System.out.println("ALL TESTS PASSED (" + total + ")");
    }

    private static int run(List<String> failures, String name, Runnable test) {
        try {
            test.run();
        } catch (AssertionError ae) {
            failures.add(name + ": " + ae.getMessage());
        } catch (Throwable t) {
            failures.add(name + ": threw " + t.getClass().getSimpleName() + " - " + t.getMessage());
        }
        return 1;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(message + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }

    // ==================== TESTS ====================

    private static void tileObstacle_isAccessible_and_removeObstacle() {
        Tile t = new Tile(TileType.OBSTACLE, null);
        assertTrue(!t.isAccessible(), "Obstacle tile must be inaccessible");
        assertTrue(t.isObstacle(), "Tile should report isObstacle() for OBSTACLE");

        boolean removed = t.removeObstacle();
        assertTrue(removed, "removeObstacle should return true for OBSTACLE");
        assertEquals(TileType.PLAIN, t.getType(), "Obstacle must become PLAIN after removal");
        assertEquals(null, t.getFeature(), "Obstacle removal must clear feature");
        assertTrue(t.isAccessible(), "PLAIN tile must be accessible");
    }

    private static void lov_moveHero_blocksObstacle_and_monsterTile() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = new Warrior("HeroA", 1, 10, 10, 10, 10, new Wallet(0), 0);
        map.placeHeroAtNexus(hero, 0); // row 7, col 0

        // Put an obstacle at row 6 col 1, and move hero to row 7 col 1 first.
        map.getTile(6, 1).setType(TileType.OBSTACLE);
        map.getTile(6, 1).setFeature(null);

        assertTrue(map.moveHero(hero, Direction.RIGHT), "Hero should be able to move RIGHT on nexus row");
        assertTrue(!map.moveHero(hero, Direction.UP), "Hero must not be able to move UP into an OBSTACLE");

        // Move monster down to be in front/diagonal of the hero and ensure hero can't step onto it.
        Monster monster = new TestMonster("M1", 1, false, 10, 10, 10);
        map.spawnMonster(monster, 0); // row 0, col 1

        // Clear path and move monster to row 6, col 1 (diagonal to hero at row 7 col 1).
        map.getTile(1, 1).setType(TileType.PLAIN);
        map.getTile(1, 1).setFeature(null);
        map.getTile(2, 1).setType(TileType.PLAIN);
        map.getTile(2, 1).setFeature(null);
        map.getTile(3, 1).setType(TileType.PLAIN);
        map.getTile(3, 1).setFeature(null);
        map.getTile(4, 1).setType(TileType.PLAIN);
        map.getTile(4, 1).setFeature(null);
        map.getTile(5, 1).setType(TileType.PLAIN);
        map.getTile(5, 1).setFeature(null);
        // row 6 col 1 is currently obstacle; make it plain for monster occupancy test
        map.getTile(6, 1).setType(TileType.PLAIN);
        map.getTile(6, 1).setFeature(null);

        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(monster), "Monster should be able to move south step " + (i + 1));
        }
        int[] mPos = map.getMonsterPosition(monster);
        assertEquals(6, mPos[0], "Monster should be at row 6 after moving");
        assertEquals(1, mPos[1], "Monster should remain at col 1");

        // Dis.txt: a cell can hold one hero and one monster => moving onto a monster tile is allowed.
        assertTrue(map.moveHero(hero, Direction.UP), "Hero should be able to move onto a monster cell (co-occupancy)");
    }

    private static void lov_teleport_and_recall_basic() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero h1 = new Warrior("HeroA", 1, 10, 10, 10, 10, new Wallet(0), 0);
        Hero h2 = new Warrior("HeroB", 1, 10, 10, 10, 10, new Wallet(0), 0);

        map.placeHeroAtNexus(h1, 0); // lane 0
        map.placeHeroAtNexus(h2, 1); // lane 1 (row 7 col 3)

        // Move target hero (h2) up twice to row 5 col 3
        assertTrue(map.moveHero(h2, Direction.UP), "HeroB should move UP to row 6");
        assertTrue(map.moveHero(h2, Direction.UP), "HeroB should move UP to row 5");

        int[] targetPos = map.getHeroPosition(h2);
        assertEquals(5, targetPos[0], "HeroB should be at row 5");

        boolean teleported = map.teleportHero(h1, h2);
        assertTrue(teleported, "HeroA should be able to teleport near HeroB in another lane");

        int[] h1Pos = map.getHeroPosition(h1);
        assertTrue(h1Pos != null, "HeroA must have a position after teleport");
        assertTrue(h1Pos[0] >= targetPos[0], "Teleport destination must not be ahead of target hero");

        map.recallHero(h1);
        int[] recalled = map.getHeroPosition(h1);
        assertEquals(LegendsOfValorWorldMap.HERO_NEXUS_ROW, recalled[0], "Recall must return hero to HERO_NEXUS_ROW");
        assertEquals(LegendsOfValorWorldMap.LANE_COLUMNS[0][0], recalled[1], "Recall must return hero to their lane spawn col");
    }

    private static void lov_spawnMonster_doesNotStackOnSpawn() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Monster m1 = new TestMonster("M1", 1, false, 10, 10, 10);
        Monster m2 = new TestMonster("M2", 1, false, 10, 10, 10);

        map.spawnMonster(m1, 0);
        map.spawnMonster(m2, 0); // should be ignored because spawn occupied by m1

        assertEquals(1, map.getMonsters().size(), "Second monster should not spawn on an occupied spawn tile");
        int[] pos1 = map.getMonsterPosition(m1);
        assertEquals(0, pos1[0], "Spawn row must be 0");
        assertEquals(1, pos1[1], "Lane 0 monster spawn col must be 1");
    }

    private static void lov_moveMonsterSouth_blockedByObstacle() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Monster m1 = new TestMonster("M1", 1, false, 10, 10, 10);
        map.spawnMonster(m1, 0); // row 0 col 1

        // Place obstacle directly in front at row 1 col 1
        map.getTile(1, 1).setType(TileType.OBSTACLE);
        map.getTile(1, 1).setFeature(null);

        boolean moved = map.moveMonsterSouth(m1);
        assertTrue(!moved, "Monster should not move into an obstacle");

        int[] pos = map.getMonsterPosition(m1);
        assertEquals(0, pos[0], "Monster row must remain 0 if move blocked");
        assertEquals(1, pos[1], "Monster col must remain 1 if move blocked");
    }

    // ==================== UseCase tests (LovActionExecutor) ====================

    private static void lov_action_move_cancel() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0); // row 7 col 0

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.MOVE,
                hero,
                map.getAliveMonsters(),
                new MoveRequest(null)
        );
        assertTrue(!result.isSuccess(), "MOVE with null direction should be canceled");
    }

    private static void lov_action_move_blocked() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        // Block UP from row 7 col 0 -> row 6 col 0
        map.getTile(6, 0).setType(TileType.OBSTACLE);
        map.getTile(6, 0).setFeature(null);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.MOVE,
                hero,
                map.getAliveMonsters(),
                new MoveRequest(Direction.UP)
        );
        assertTrue(!result.isSuccess(), "MOVE into OBSTACLE should fail");
    }

    private static void lov_action_move_success() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.MOVE,
                hero,
                map.getAliveMonsters(),
                new MoveRequest(Direction.RIGHT)
        );
        assertTrue(result.isSuccess(), "MOVE RIGHT on nexus row should succeed");
        assertTrue(result.shouldRenderMap(), "MOVE should request map render");
    }

    private static void lov_action_teleport_cancel() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.TELEPORT,
                hero,
                map.getAliveMonsters(),
                new TeleportRequest(null)
        );
        assertTrue(!result.isSuccess(), "TELEPORT with null target should be canceled");
    }

    private static void lov_action_teleport_success() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero h1 = newHero("HeroA");
        Hero h2 = newHero("HeroB");
        map.placeHeroAtNexus(h1, 0);
        map.placeHeroAtNexus(h2, 1);
        assertTrue(map.moveHero(h2, Direction.UP), "HeroB should move UP");
        assertTrue(map.moveHero(h2, Direction.UP), "HeroB should move UP again");

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.TELEPORT,
                h1,
                map.getAliveMonsters(),
                new TeleportRequest(h2)
        );
        assertTrue(result.isSuccess(), "TELEPORT should succeed");
        assertTrue(result.shouldRenderMap(), "TELEPORT should request map render");
    }

    private static void lov_action_recall_success() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);
        assertTrue(map.moveHero(hero, Direction.RIGHT), "Move first so recall has effect");
        assertTrue(map.moveHero(hero, Direction.UP), "Move UP");

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(
                HeroActionType.RECALL,
                hero,
                map.getAliveMonsters(),
                null
        );
        assertTrue(result.isSuccess(), "RECALL should succeed");
        int[] pos = map.getHeroPosition(hero);
        assertEquals(LegendsOfValorWorldMap.HERO_NEXUS_ROW, pos[0], "Recall must return hero to nexus row");
    }

    private static void lov_action_removeObstacle_allBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0); // row 7 col 0

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));

        LovActionResult canceled = executor.execute(
                HeroActionType.REMOVE_OBSTACLE,
                hero,
                map.getAliveMonsters(),
                new RemoveObstacleRequest(null)
        );
        assertTrue(!canceled.isSuccess(), "REMOVE_OBSTACLE with null dir should cancel");

        // Non-obstacle -> fail
        LovActionResult nonObs = executor.execute(
                HeroActionType.REMOVE_OBSTACLE,
                hero,
                map.getAliveMonsters(),
                new RemoveObstacleRequest(Direction.UP)
        );
        assertTrue(!nonObs.isSuccess(), "REMOVE_OBSTACLE should fail if not obstacle");

        // Obstacle -> success
        map.getTile(6, 0).setType(TileType.OBSTACLE);
        map.getTile(6, 0).setFeature(null);

        LovActionResult ok = executor.execute(
                HeroActionType.REMOVE_OBSTACLE,
                hero,
                map.getAliveMonsters(),
                new RemoveObstacleRequest(Direction.UP)
        );
        assertTrue(ok.isSuccess(), "REMOVE_OBSTACLE should succeed on obstacle");
        assertEquals(TileType.PLAIN, map.getTile(6, 0).getType(), "Obstacle should become PLAIN");
    }

    private static void lov_action_attack_allBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0); // row 7 col 0

        Weapon weapon = new Weapon("W", 0, 1, 50, 1, 2);
        hero.getInventory().add(weapon);
        hero.equipWeapon(weapon);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));

        LovActionResult noMonsters = executor.execute(HeroActionType.ATTACK, hero, Collections.emptyList(), new AttackRequest(null, null));
        assertTrue(!noMonsters.isSuccess(), "ATTACK with no monsters should not be success");

        Monster far = new TestMonster("Far", 1, false, 10, 0, 0);
        map.spawnMonster(far, 0);
        LovActionResult outOfRange = executor.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(far, 2));
        assertTrue(!outOfRange.isSuccess(), "ATTACK out of range should fail");

        // Move monster down to be in range (row 6 col 1) adjacent to hero at row 7 col 0 (diagonal)
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(far), "Monster should move south to reach range");
        }
        int[] mpos = map.getMonsterPosition(far);
        assertEquals(6, mpos[0], "Monster should be at row 6");

        // Dodged branch
        Monster dodger = new TestMonster("Dodger", 1, true, 10, 0, 0);
        map.spawnMonster(dodger, 1);
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(dodger), "Dodger move south");
        }
        LovActionResult dodged = executor.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(dodger, 2));
        assertTrue(!dodged.isSuccess(), "ATTACK should be reported as not success if dodged (current rule)");

        // Hit branch
        LovActionResult hit = executor.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(far, 2));
        assertTrue(hit.isSuccess(), "ATTACK hit should succeed");
        assertEquals(1, weapon.getUsesRemaining(), "Weapon uses should decrement after attack");
    }

    private static void lov_action_castSpell_allBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);
        hero.setMp(0);

        Spell spell = new Spell("S", 0, 1, 50, 10, SpellType.FIRE);
        hero.getInventory().add(spell);

        Monster target = new TestMonster("T", 1, false, 10, 0, 0);
        map.spawnMonster(target, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult notEnough = executor.execute(HeroActionType.CAST_SPELL, hero, map.getAliveMonsters(), new CastSpellRequest(spell, target));
        assertTrue(!notEnough.isSuccess(), "CAST_SPELL should fail if not enough MP");

        // Give MP; for the rest of this test we'll use a different target in range.
        hero.setMp(999);
        map.removeMonster(target);

        // Dodge spell branch: roll=0.0, set dodgeProb>0, and ensure target is IN RANGE.
        // Spawn in lane 0 so after moving south it ends up adjacent to hero (row 6 col 1 vs hero row 7 col 0).
        Monster highDodge = new TestMonster("HighDodge", 1, false, 10, 0, 1000);
        map.spawnMonster(highDodge, 0);
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(highDodge), "HighDodge moves into range");
        }

        Spell spell2 = new Spell("S2", 0, 1, 10, 1, SpellType.ICE);
        hero.getInventory().add(spell2);
        int mpBefore = hero.getMp();
        LovActionExecutor dodgeExec = new LovActionExecutor(map, new FixedRandom(0.0, 0)); // force dodge
        LovActionResult dodged = dodgeExec.execute(HeroActionType.CAST_SPELL, hero, map.getAliveMonsters(), new CastSpellRequest(spell2, highDodge));
        assertTrue(dodged.isSuccess(), "CAST_SPELL should be success even if monster dodges (current rule)");
        assertEquals(mpBefore - spell2.getManaCost(), hero.getMp(), "Spell cast consumes MP even on dodge");

        // Hit + debuff branch: roll=0.99 avoid dodge
        Spell fire = new Spell("Fire", 0, 1, 10, 1, SpellType.FIRE);
        hero.getInventory().add(fire);
        int defenseBefore = highDodge.getDefense();
        LovActionExecutor hitExec = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult hit = hitExec.execute(HeroActionType.CAST_SPELL, hero, map.getAliveMonsters(), new CastSpellRequest(fire, highDodge));
        assertTrue(hit.isSuccess(), "CAST_SPELL should succeed");
        assertTrue(highDodge.getDefense() <= defenseBefore, "FIRE spell should reduce monster defense");
    }

    private static void lov_action_usePotion_allBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));

        LovActionResult none = executor.execute(HeroActionType.USE_POTION, hero, map.getAliveMonsters(), new UsePotionRequest(null));
        assertTrue(!none.isSuccess(), "USE_POTION with null should fail");

        Potion p = new Potion("P", 0, 1, 5, StatType.STRENGTH);
        hero.getInventory().add(p);
        int before = hero.getStrength();
        LovActionResult ok = executor.execute(HeroActionType.USE_POTION, hero, map.getAliveMonsters(), new UsePotionRequest(p));
        assertTrue(ok.isSuccess(), "USE_POTION should succeed");
        assertEquals(before + 5, hero.getStrength(), "Potion should increase stat");
        assertTrue(!hero.getInventory().contains(p), "Potion should be removed after use");
    }

    private static void lov_action_equip_allBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));

        LovActionResult canceled = executor.execute(HeroActionType.EQUIP, hero, map.getAliveMonsters(), new EquipRequest(null, null, null));
        assertTrue(!canceled.isSuccess(), "EQUIP with null choice should cancel");

        Weapon w = new Weapon("W", 0, 1, 10, 2, 0);
        Armor a = new Armor("A", 0, 1, 5, 0);
        hero.getInventory().add(w);
        hero.getInventory().add(a);

        LovActionResult equipW = executor.execute(HeroActionType.EQUIP, hero, map.getAliveMonsters(), new EquipRequest(EquipChoice.WEAPON, w, null));
        assertTrue(equipW.isSuccess(), "Equip weapon should succeed");
        assertEquals(w, hero.getEquippedWeapon(), "Weapon should be equipped");

        LovActionResult equipA = executor.execute(HeroActionType.EQUIP, hero, map.getAliveMonsters(), new EquipRequest(EquipChoice.ARMOR, null, a));
        assertTrue(equipA.isSuccess(), "Equip armor should succeed");
        assertEquals(a, hero.getEquippedArmor(), "Armor should be equipped");
    }

    private static void lov_action_skip() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("HeroA");
        map.placeHeroAtNexus(hero, 0);

        LovActionExecutor executor = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        LovActionResult result = executor.execute(HeroActionType.SKIP, hero, map.getAliveMonsters(), null);
        assertTrue(result.isSuccess(), "SKIP should succeed");
        assertTrue(!result.getWarningMessages().isEmpty(), "SKIP should return a warning message");
    }

    // ==================== Controller tests (LegendsOfValorGameImpl) ====================

    private static void lov_controller_quit_on_prompt() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        party.addHero(h1);
        map.placeHeroAtNexus(h1, 0);

        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(new TestMonster("M1", 1, false, 10, 10, 10)));
        FakeLovView view = new FakeLovView().withContinueCalls(false); // quit immediately

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();
        assertEquals(1, view.roundHeaders, "Quit path should stop immediately after first prompt");
    }

    private static void lov_controller_initialSpawn_onePerLane() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        Hero h2 = newHero("H2");
        Hero h3 = newHero("H3");
        party.addHero(h1);
        party.addHero(h2);
        party.addHero(h3);
        map.placeHeroAtNexus(h1, 0);
        map.placeHeroAtNexus(h2, 1);
        map.placeHeroAtNexus(h3, 2);

        List<Monster> wave = new ArrayList<>();
        wave.add(new TestMonster("M1", 1, false, 10, 10, 10));
        wave.add(new TestMonster("M2", 1, false, 10, 10, 10));
        wave.add(new TestMonster("M3", 1, false, 10, 10, 10));
        IMonsterFactory factory = new FixedMonsterFactory(wave);

        FakeLovView view = new FakeLovView().withContinueCalls(false); // quit at round 1 prompt
        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();

        assertEquals(3, map.getMonsters().size(), "Initial spawn should add one monster per lane");
    }

    private static void lov_controller_spawnInterval_spawnsNewWave() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        // Block row 6 to prevent monster victory before round 8.
        for (int lane = 0; lane < LegendsOfValorWorldMap.LANE_COLUMNS.length; lane++) {
            int col = LegendsOfValorWorldMap.LANE_COLUMNS[lane][1];
            map.getTile(6, col).setType(TileType.OBSTACLE);
            map.getTile(6, col).setFeature(null);
        }

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        Hero h2 = newHero("H2");
        Hero h3 = newHero("H3");
        party.addHero(h1);
        party.addHero(h2);
        party.addHero(h3);
        map.placeHeroAtNexus(h1, 0);
        map.placeHeroAtNexus(h2, 1);
        map.placeHeroAtNexus(h3, 2);

        // Provide enough monsters for two waves.
        List<Monster> monsters = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            monsters.add(new TestMonster("M" + i, 1, false, 10, 10, 10));
        }
        IMonsterFactory factory = new SequencedMonsterFactory(monsters);

        // Continue 8 rounds, then quit on round 9 prompt.
        FakeLovView view = new FakeLovView().withContinueCalls(true, true, true, true, true, true, true, true, false);
        view.defaultAction = HeroActionType.SKIP;

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();

        // By round 8 end, a second wave should have spawned (spawn tiles should be free because monsters moved to row 5).
        assertTrue(map.getMonsters().size() >= 6, "After spawn interval, total monsters should increase (at least 2 waves)");
        assertTrue(view.warningCount > 0, "Spawn interval should display at least one warning message");
    }

    private static void lov_controller_heroVictory() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        party.addHero(h1);
        map.placeHeroAtNexus(h1, 0);

        // Move hero to monster nexus row (0)
        for (int i = 0; i < 7; i++) {
            assertTrue(map.moveHero(h1, Direction.UP), "Hero should move up towards monster nexus");
        }

        IMonsterFactory factory = new FixedMonsterFactory(Collections.emptyList());
        FakeLovView view = new FakeLovView().withContinueCalls(true);
        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();
        assertTrue(view.successCount > 0, "Hero victory should show success");
    }

    private static void lov_controller_monsterVictory() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        IMonsterFactory emptyFactory = new FixedMonsterFactory(Collections.emptyList());
        FakeLovView view = new FakeLovView().withContinueCalls(true);

        Monster m = new TestMonster("M", 1, false, 10, 10, 10);
        map.spawnMonster(m, 0);
        for (int i = 0; i < 7; i++) {
            assertTrue(map.moveMonsterSouth(m), "Monster should move south towards hero nexus");
        }

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, emptyFactory, view, new FixedRandom(0.99, 0));
        game.start();
        assertTrue(view.failCount > 0, "Monster victory should show fail");
    }

    private static void lov_controller_marketQuit_stopsImmediately() {
        // Now tests that returning null from promptHeroAction (user pressed Q) stops the game
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        party.addHero(h1);
        map.placeHeroAtNexus(h1, 0);

        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(new TestMonster("M1", 1, false, 10, 10, 10)));
        FakeLovView view = new FakeLovView().withContinueCalls(true);
        view.defaultAction = null; // Simulate user pressing Q to quit

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();

        assertTrue(view.successCount > 0, "Quit should show success goodbye message");
        assertTrue(view.roundHeaders <= 1, "Game should stop immediately after quit");
    }

    private static void lov_market_buySell_and_failBranches() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("Buyer");
        map.placeHeroAtNexus(hero, 0);

        Tile tile = map.getTile(hero.getRow(), hero.getCol());
        assertTrue(tile != null, "Hero must be on a valid tile");
        Market market = tile.getMarket();
        assertTrue(market != null, "Nexus tile should provide a market");

        MarketServiceImpl svc = new MarketServiceImpl(market);
        List<market.model.item.Item> items = svc.getItemsForSale();
        assertTrue(items != null && !items.isEmpty(), "Market should have items for sale");

        // Use a controlled item so level/gold checks are deterministic.
        Weapon good = new Weapon("GOOD", 50, hero.getLevel(), 10, 2, 0);
        market.addItem(good);

        // Fail: item not available
        Weapon notInMarket = new Weapon("NA", 50, hero.getLevel(), 10, 2, 0);
        MarketResult r0 = svc.buyItem(hero, notInMarket);
        assertTrue(!r0.isSuccess(), "Buy should fail when item is not in market");

        // Fail: not enough gold
        hero.spendGold(hero.getGold()); // set to 0
        MarketResult r1 = svc.buyItem(hero, good);
        assertTrue(!r1.isSuccess(), "Buy should fail when not enough gold");

        // Fail: not enough level (set hero level to 1, force item level > 1 by making a custom item)
        market.model.item.Weapon highLevel = new Weapon("HL", 100, hero.getLevel() + 10, 10, 2, 0);
        market.addItem(highLevel);
        hero.addGold(100000);
        MarketResult r2 = svc.buyItem(hero, highLevel);
        assertTrue(!r2.isSuccess(), "Buy should fail when not enough level");

        // Success: buy and then sell
        MarketResult bought = svc.buyItem(hero, good);
        assertTrue(bought.isSuccess(), "Buy should succeed with enough gold and level");
        assertTrue(hero.getInventory().contains(good), "Bought item should be in hero inventory");
        assertTrue(!market.getItems().contains(good), "Bought item should be removed from market");

        MarketResult sold = svc.sellItem(hero, good);
        assertTrue(sold.isSuccess(), "Sell should succeed for owned item");
        assertTrue(!hero.getInventory().contains(good), "Sold item should be removed from hero inventory");
        assertTrue(market.getItems().contains(good), "Sold item should be added back to market");

        // Fail: sell not owned
        MarketResult sellFail = svc.sellItem(hero, good);
        assertTrue(!sellFail.isSuccess(), "Selling an unowned item should fail");
    }

    private static void lov_weapon_swap_changesDamageAndUses() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("Fighter");
        map.placeHeroAtNexus(hero, 0); // row 7 col 0

        // Put a monster adjacent at row 6 col 1.
        Monster m = new TestMonster("M", 1, false, 9999, 0, 0);
        map.spawnMonster(m, 0);
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(m), "Monster should move into range");
        }

        Weapon w1 = new Weapon("W1", 0, 1, 0, 2, 2);    // no weapon damage
        Weapon w2 = new Weapon("W2", 0, 1, 100, 2, 2);  // big weapon damage
        hero.getInventory().add(w1);
        hero.getInventory().add(w2);

        LovActionExecutor exec = new LovActionExecutor(map, new FixedRandom(0.99, 0));

        // Equip W1, attack -> should consume W1 uses
        LovActionResult eq1 = exec.execute(HeroActionType.EQUIP, hero, map.getAliveMonsters(), new EquipRequest(EquipChoice.WEAPON, w1, null));
        assertTrue(eq1.isSuccess(), "Equip W1 should succeed");
        int hp0 = m.getHp();
        LovActionResult a1 = exec.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(m, null));
        assertTrue(a1.isSuccess(), "Attack with W1 should succeed");
        int hp1 = m.getHp();
        int dmg1 = hp0 - hp1;
        assertTrue(dmg1 >= 0, "Monster HP should not increase");
        assertEquals(1, w1.getUsesRemaining(), "W1 uses should decrement");

        // Swap to W2, attack -> damage should increase vs W1
        LovActionResult eq2 = exec.execute(HeroActionType.EQUIP, hero, map.getAliveMonsters(), new EquipRequest(EquipChoice.WEAPON, w2, null));
        assertTrue(eq2.isSuccess(), "Equip W2 should succeed");
        int hp2 = m.getHp();
        LovActionResult a2 = exec.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(m, null));
        assertTrue(a2.isSuccess(), "Attack with W2 should succeed");
        int hp3 = m.getHp();
        int dmg2 = hp2 - hp3;
        assertTrue(dmg2 > dmg1, "Weapon swap to higher-damage weapon should increase dealt damage");
        assertEquals(1, w2.getUsesRemaining(), "W2 uses should decrement");
    }

    private static void lov_armor_reducesDamage_and_breaks_correctly() {
        Hero hero = newHero("Tank");
        int hpStart = hero.getHp();

        Armor armor = new Armor("A", 0, 1, 10, 1); // one use, reduces 10
        hero.getInventory().add(armor);
        hero.equipArmor(armor);

        // First hit: reduced by 10, then armor breaks & unequips
        hero.takeDamage(30);
        assertEquals(hpStart - 20, hero.getHp(), "Armor should reduce first hit by damageReduction");
        assertTrue(hero.getEquippedArmor() == null, "Broken armor should be unequipped");

        // Second hit: no reduction
        hero.takeDamage(30);
        assertEquals(hpStart - 20 - 30, hero.getHp(), "After armor breaks, subsequent hits should not be reduced");
    }

    private static void lov_potion_strength_increasesAttackDamage() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("Buffer");
        map.placeHeroAtNexus(hero, 0);

        Monster m = new TestMonster("M", 1, false, 9999, 0, 0);
        map.spawnMonster(m, 0);
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(m), "Monster should move into range");
        }

        // Baseline attack damage with no weapon
        hero.equipWeapon(null);
        LovActionExecutor exec = new LovActionExecutor(map, new FixedRandom(0.99, 0));
        int hp0 = m.getHp();
        LovActionResult base = exec.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(m, null));
        assertTrue(base.isSuccess(), "Baseline attack should succeed");
        int hp1 = m.getHp();
        int dmgBase = hp0 - hp1;

        // Use strength potion, then attack again -> damage should increase
        Potion p = new Potion("STR", 0, 1, 50, StatType.STRENGTH);
        hero.getInventory().add(p);
        LovActionResult used = exec.execute(HeroActionType.USE_POTION, hero, map.getAliveMonsters(), new UsePotionRequest(p));
        assertTrue(used.isSuccess(), "Potion use should succeed");

        int hp2 = m.getHp();
        LovActionResult after = exec.execute(HeroActionType.ATTACK, hero, map.getAliveMonsters(), new AttackRequest(m, null));
        assertTrue(after.isSuccess(), "Attack after potion should succeed");
        int hp3 = m.getHp();
        int dmgAfter = hp2 - hp3;

        assertTrue(dmgAfter > dmgBase, "Strength potion should increase subsequent attack damage");
    }

    private static void lov_cooccupancy_heroCanMoveOntoMonsterCell() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = newHero("H");
        map.placeHeroAtNexus(hero, 0); // row 7 col 0
        assertTrue(map.moveHero(hero, Direction.RIGHT), "Move hero to col 1");

        Monster monster = new TestMonster("M", 1, false, 10, 10, 10);
        map.spawnMonster(monster, 0); // row 0 col 1
        for (int i = 0; i < 6; i++) {
            assertTrue(map.moveMonsterSouth(monster), "Monster should move south into lane");
        }
        int[] mPos = map.getMonsterPosition(monster);
        assertEquals(6, mPos[0], "Monster should reach row 6");
        assertEquals(1, mPos[1], "Monster should be at col 1");

        // Hero moves UP onto the monster cell is allowed (co-occupancy).
        assertTrue(map.moveHero(hero, Direction.UP), "Hero can move onto monster cell (co-occupancy)");

        int[] hPos = map.getHeroPosition(hero);
        assertEquals(6, hPos[0], "Hero should now share the cell at row 6");
        assertEquals(1, hPos[1], "Hero should now share the cell at col 1");
        assertTrue(map.getMonsterAt(6, 1) != null, "Monster should still be present on the shared cell");
    }

    private static void lov_respawn_deadHero_nextRound_fullHpMp_and_backToNexus() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Party party = new Party(3);
        Hero h1 = newHero("H1");
        party.addHero(h1);
        map.placeHeroAtNexus(h1, 0);

        // Move away, then kill hero.
        assertTrue(map.moveHero(h1, Direction.RIGHT), "Move away from spawn");
        h1.setHp(0);
        assertTrue(!h1.isAlive(), "Hero should be dead before round starts");

        // Start game for one round, with no hero actions and immediate quit on 2nd prompt.
        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(new TestMonster("M1", 1, false, 10, 10, 10)));
        FakeLovView view = new FakeLovView().withContinueCalls(true, false);
        view.defaultAction = HeroActionType.SKIP;

        LegendsOfValorGameImpl game = new LegendsOfValorGameImpl(map, party, factory, view, new FixedRandom(0.99, 0));
        game.start();

        // After round-start respawn, hero should be alive, full HP/MP, and at their nexus.
        assertTrue(h1.isAlive(), "Hero should respawn at round start");
        assertEquals(h1.getMaxHp(), h1.getHp(), "Respawn should restore full HP");
        assertEquals(h1.getMaxMp(), h1.getMp(), "Respawn should restore full MP");

        int[] pos = map.getHeroPosition(h1);
        assertEquals(LegendsOfValorWorldMap.HERO_NEXUS_ROW, pos[0], "Respawn should return hero to HERO_NEXUS_ROW");
        assertEquals(LegendsOfValorWorldMap.LANE_COLUMNS[0][0], pos[1], "Respawn should return hero to their lane spawn col");
    }

    // ==================== Battle system tests (BattleEngineImpl) ====================

    private static void battle_heroesWin_attack_rewardsApplied() {
        Party party = new Party(3);
        Hero hero = newHeroNoDodge("Hero");
        party.addHero(hero);

        Monster m = new TestMonster("M", 1, false, 10, 0, 0);
        m.setHp(1); // ensure one hit kills
        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(m));

        FakeBattleView view = new FakeBattleView()
                .withHeroActions(HeroActionType.ATTACK)
                .withMonsterTargets(m);

        BattleEngine engine = new BattleEngineImpl(view, factory, new FixedRandom(0.99, 0));
        boolean heroesWon = engine.runBattle(party, null);
        assertTrue(heroesWon, "Heroes should win when the only monster is killed");

        assertTrue(hero.getGold() >= 100, "Battle rewards should grant gold to surviving hero");
    }

    private static void battle_monstersWin_postBattleRecovery_revivesHero() {
        Party party = new Party(3);
        Hero hero = newHeroNoDodge("Hero");
        party.addHero(hero);

        // Monster will one-shot hero
        Monster m = new TestMonster("M", 1, false, 100000, 0, 0);
        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(m));

        FakeBattleView view = new FakeBattleView()
                .withHeroActions(HeroActionType.SKIP);

        BattleEngine engine = new BattleEngineImpl(view, factory, new FixedRandom(0.99, 0));
        boolean heroesWon = engine.runBattle(party, null);
        assertTrue(!heroesWon, "Heroes should lose when all heroes are defeated");

        // Post-battle recovery revives defeated heroes to half HP (>=1)
        assertTrue(hero.isAlive(), "Hero should be revived after losing battle");
        assertEquals(hero.getMaxHp() / 2, hero.getHp(), "Hero should revive to half max HP after battle");
    }

    private static void battle_potion_then_attack_effect_applies() {
        Party party = new Party(3);
        Hero hero = newHeroNoDodge("Hero");
        party.addHero(hero);

        // Add strength potion: +50 strength so next attack damage increases
        Potion p = new Potion("STR", 0, 1, 50, StatType.STRENGTH);
        hero.getInventory().add(p);

        Monster m = new TestMonster("M", 1, false, 1, 0, 0);
        m.setHp(5); // damage after potion should be 5 -> kill
        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(m));

        FakeBattleView view = new FakeBattleView()
                .withHeroActions(HeroActionType.USE_POTION, HeroActionType.ATTACK)
                .withPotionChoices(p)
                .withMonsterTargets(m);

        BattleEngine engine = new BattleEngineImpl(view, factory, new FixedRandom(0.99, 0));
        boolean heroesWon = engine.runBattle(party, null);
        assertTrue(heroesWon, "Heroes should win after using potion then attacking next round");
        assertTrue(!hero.getInventory().contains(p), "Potion should be consumed and removed from inventory");
        assertTrue(!m.isAlive(), "Monster should be defeated by the attack after potion");
    }

    private static void battle_castSpell_consumesMp_and_appliesDebuff() {
        Party party = new Party(3);
        Hero hero = newHeroNoDodge("Mage");
        party.addHero(hero);

        hero.setMp(999);
        Spell ice = new Spell("ICE", 0, 1, 200, 10, SpellType.ICE);
        hero.getInventory().add(ice);

        Monster m = new TestMonster("M", 1, false, 100, 0, 0);
        m.setHp(10); // ensure spell kills
        double baseDamageBefore = m.getBaseDamage();

        IMonsterFactory factory = new FixedMonsterFactory(Collections.singletonList(m));
        FakeBattleView view = new FakeBattleView()
                .withHeroActions(HeroActionType.CAST_SPELL)
                .withSpellChoices(ice)
                .withMonsterTargets(m);

        int mpBefore = hero.getMp();
        BattleEngine engine = new BattleEngineImpl(view, factory, new FixedRandom(0.99, 0));
        boolean heroesWon = engine.runBattle(party, null);
        assertTrue(heroesWon, "Heroes should win if spell kills the only monster");
        assertEquals(mpBefore - ice.getManaCost(), hero.getMp(), "Casting a spell should consume MP");
        assertTrue(!hero.getInventory().contains(ice), "Spell item should be consumed and removed from inventory");
        assertTrue(m.getBaseDamage() < baseDamageBefore, "ICE spell should reduce monster base damage");
    }

    // ==================== TEST HELPERS ====================

    private static Hero newHero(String name) {
        return new Warrior(name, 1, 50, 50, 50, 50, new Wallet(0), 0);
    }

    private static Hero newHeroNoDodge(String name) {
        // agility=0 -> dodge chance 0, avoids randomness in battle tests
        return new Warrior(name, 1, 50, 50, 0, 50, new Wallet(0), 0);
    }

    private static final class FixedMonsterFactory implements IMonsterFactory {
        private final List<Monster> monsters;

        private FixedMonsterFactory(List<Monster> monsters) {
            this.monsters = monsters;
        }

        @Override
        public List<Monster> createMonstersForParty(Party party) {
            return new ArrayList<>(monsters);
        }
    }

    private static final class SequencedMonsterFactory implements IMonsterFactory {
        private final List<Monster> seq;
        private int idx = 0;

        private SequencedMonsterFactory(List<Monster> seq) {
            this.seq = seq;
        }

        @Override
        public List<Monster> createMonstersForParty(Party party) {
            int n = party.getMonsterCountForBattle();
            List<Monster> out = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                if (idx >= seq.size()) {
                    // Fallback deterministic monster
                    out.add(new TestMonster("MF" + idx, party.getHighestLevel(), false, 10, 10, 10));
                    idx++;
                } else {
                    out.add(seq.get(idx++));
                }
            }
            return out;
        }
    }

    private static final class FakeLovView implements LovView {
        private final List<Boolean> continueAnswers = new ArrayList<>();
        private int continueIdx = 0;

        HeroActionType defaultAction = HeroActionType.SKIP;

        int successCount = 0;
        int failCount = 0;
        int warningCount = 0;
        int marketCalled = 0;
        int roundHeaders = 0;

        private FakeLovView withContinueCalls(boolean... answers) {
            continueAnswers.clear();
            for (boolean b : answers) continueAnswers.add(b);
            continueIdx = 0;
            return this;
        }

        @Override
        public void showStarting() {}

        @Override
        public void showRoundHeader(int round) { roundHeaders++; }

        @Override
        public void refreshDisplay(int round, List<Hero> heroes, List<Monster> monsters) {
            // Fake implementation - no-op (roundHeaders tracked by showRoundHeader)
        }

        @Override
        public void renderMap() {}

        @Override
        public boolean promptContinueOrQuit() {
            if (continueIdx >= continueAnswers.size()) {
                return true;
            }
            return continueAnswers.get(continueIdx++);
        }

        @Override
        public void showHeroesAndMonstersStatus(List<Hero> heroes, List<Monster> monsters) {}

        @Override
        public HeroActionType promptHeroAction(Hero hero, int heroIndex, List<Monster> monsters, boolean isOnNexus) {
            return defaultAction;
        }

        @Override
        public Direction getLastMoveDirection() { return Direction.UP; }

        @Override
        public Direction promptDirection(String prompt, boolean allowCancel) { return null; }

        @Override
        public Monster promptMonsterTarget(Hero hero, List<Monster> monsters) {
            if (monsters == null || monsters.isEmpty()) return null;
            return monsters.get(0);
        }

        @Override
        public Hero promptTeleportTarget(Hero hero, List<Hero> candidates) {
            if (candidates == null || candidates.isEmpty()) return null;
            return candidates.get(0);
        }

        @Override
        public Spell promptSpellToCast(Hero hero, List<Spell> spells) {
            if (spells == null || spells.isEmpty()) return null;
            return spells.get(0);
        }

        @Override
        public Potion promptPotionToUse(Hero hero, List<Potion> potions) {
            if (potions == null || potions.isEmpty()) return null;
            return potions.get(0);
        }

        @Override
        public EquipChoice promptEquipChoice(Hero hero) { return EquipChoice.CANCEL; }

        @Override
        public Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons) { return null; }

        @Override
        public Armor promptArmorToEquip(Hero hero, List<Armor> armors) { return null; }

        @Override
        public int promptHandsForWeapon(Hero hero, Weapon weapon) { return 1; }

        @Override
        public void runMarketSession(Hero hero, Market market) {
            marketCalled++;
        }

        @Override
        public void showSuccess(String message) { successCount++; }

        @Override
        public void showFail(String message) { failCount++; }

        @Override
        public void showWarning(String message) { warningCount++; }

        @Override
        public void waitForUserAcknowledge() { /* no-op for tests */ }
    }

    private static final class FakeBattleView implements BattleView {
        private final List<HeroActionType> heroActions = new ArrayList<>();
        private int heroActionIdx = 0;

        private final List<Monster> monsterTargets = new ArrayList<>();
        private int monsterTargetIdx = 0;

        private final List<Potion> potionChoices = new ArrayList<>();
        private int potionIdx = 0;

        private final List<Spell> spellChoices = new ArrayList<>();
        private int spellIdx = 0;

        private FakeBattleView withHeroActions(HeroActionType... actions) {
            heroActions.clear();
            Collections.addAll(heroActions, actions);
            heroActionIdx = 0;
            return this;
        }

        private FakeBattleView withMonsterTargets(Monster... targets) {
            monsterTargets.clear();
            Collections.addAll(monsterTargets, targets);
            monsterTargetIdx = 0;
            return this;
        }

        private FakeBattleView withPotionChoices(Potion... potions) {
            potionChoices.clear();
            Collections.addAll(potionChoices, potions);
            potionIdx = 0;
            return this;
        }

        private FakeBattleView withSpellChoices(Spell... spells) {
            spellChoices.clear();
            Collections.addAll(spellChoices, spells);
            spellIdx = 0;
            return this;
        }

        @Override
        public void showBattleStatus(List<Hero> heroes, List<Monster> monsters) {}

        @Override
        public HeroActionType promptHeroAction(Hero hero, List<Monster> monsters) {
            if (heroActionIdx >= heroActions.size()) {
                return HeroActionType.SKIP;
            }
            return heroActions.get(heroActionIdx++);
        }

        @Override
        public Monster promptMonsterTarget(Hero hero, List<Monster> monsters) {
            if (monsterTargetIdx < monsterTargets.size()) {
                return monsterTargets.get(monsterTargetIdx++);
            }
            if (monsters == null || monsters.isEmpty()) return null;
            return monsters.get(0);
        }

        @Override
        public Spell promptSpellToCast(Hero hero, List<Spell> spells) {
            if (spellIdx < spellChoices.size()) {
                return spellChoices.get(spellIdx++);
            }
            if (spells == null || spells.isEmpty()) return null;
            return spells.get(0);
        }

        @Override
        public Potion promptPotionToUse(Hero hero, List<Potion> potions) {
            if (potionIdx < potionChoices.size()) {
                return potionChoices.get(potionIdx++);
            }
            if (potions == null || potions.isEmpty()) return null;
            return potions.get(0);
        }

        @Override
        public EquipChoice promptEquipChoice(Hero hero) {
            return EquipChoice.CANCEL;
        }

        @Override
        public Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons) { return null; }

        @Override
        public Armor promptArmorToEquip(Hero hero, List<Armor> armors) { return null; }

        @Override
        public int promptHandsForWeapon(Hero hero, Weapon weapon) { return 1; }

        @Override
        public void showSuccess(String msg) {}

        @Override
        public void showWarning(String msg) {}

        @Override
        public void showFail(String msg) {}
    }

    private static final class TestMonster extends Monster {
        private final boolean alwaysDodge;

        private TestMonster(String name, int level, boolean alwaysDodge, int baseDamage, int defense, int dodgeAbility) {
            super(name, level, baseDamage, defense, dodgeAbility);
            this.alwaysDodge = alwaysDodge;
        }

        @Override
        protected java.util.Set<MonsterAttribute> getFavoredAttributes() {
            return java.util.Collections.emptySet();
        }

        @Override
        public boolean dodgesAttack() {
            return alwaysDodge;
        }
    }

    // ==================== TEST HELPERS ====================

    private static void makeLovDeterministicPlain(LegendsOfValorWorldMap map) {
        int size = map.getSize();
        int[][] laneCols = LegendsOfValorWorldMap.LANE_COLUMNS;

        // Normalize all lane tiles (rows 1..size-2) to PLAIN so movement is predictable.
        for (int row = 1; row < size - 1; row++) {
            for (int lane = 0; lane < laneCols.length; lane++) {
                for (int col : laneCols[lane]) {
                    map.getTile(row, col).setType(TileType.PLAIN);
                    map.getTile(row, col).setFeature(null);
                }
            }
        }
    }

    // No IO fakes needed: tests cover Model/UseCase only (MVC).

    // ==================== RANGE SYSTEM TESTS ====================

    private static void range_heroClasses_haveDifferentBaseRange() {
        // Create heroes of different classes
        Warrior warrior = new Warrior("TestWarrior", 1, 100, 100, 100, 100, new Wallet(1000), 0);
        Sorcerer sorcerer = new Sorcerer("TestSorcerer", 1, 100, 100, 100, 100, new Wallet(1000), 0);
        Paladin paladin = new Paladin("TestPaladin", 1, 100, 100, 100, 100, new Wallet(1000), 0);

        // Verify each class has its expected base range
        assertEquals(RangeConstants.WARRIOR_RANGE, warrior.getBaseAttackRange(),
            "Warrior should have WARRIOR_RANGE");
        assertEquals(RangeConstants.SORCERER_RANGE, sorcerer.getBaseAttackRange(),
            "Sorcerer should have SORCERER_RANGE");
        assertEquals(RangeConstants.PALADIN_RANGE, paladin.getBaseAttackRange(),
            "Paladin should have PALADIN_RANGE");

        // Sorcerer should have longer range than warrior
        assertTrue(sorcerer.getBaseAttackRange() > warrior.getBaseAttackRange(),
            "Sorcerer should have longer range than Warrior");
    }

    private static void range_monsterTypes_haveDifferentBaseRange() {
        Dragon dragon = new Dragon("TestDragon", 1, 100, 100, 50);
        Spirit spirit = new Spirit("TestSpirit", 1, 100, 100, 50);
        Exoskeleton exoskeleton = new Exoskeleton("TestExo", 1, 100, 100, 50);

        assertEquals(RangeConstants.DRAGON_RANGE, dragon.getBaseAttackRange(),
            "Dragon should have DRAGON_RANGE");
        assertEquals(RangeConstants.SPIRIT_RANGE, spirit.getBaseAttackRange(),
            "Spirit should have SPIRIT_RANGE");
        assertEquals(RangeConstants.EXOSKELETON_RANGE, exoskeleton.getBaseAttackRange(),
            "Exoskeleton should have EXOSKELETON_RANGE");

        // Dragon and Spirit should have longer range than Exoskeleton
        assertTrue(dragon.getBaseAttackRange() > exoskeleton.getBaseAttackRange(),
            "Dragon should have longer range than Exoskeleton");
    }

    private static void range_weaponRangeBonus_extendsHeroRange() {
        Warrior warrior = new Warrior("TestWarrior", 1, 100, 100, 100, 100, new Wallet(1000), 0);
        
        // Without weapon, range should be base
        assertEquals(RangeConstants.WARRIOR_RANGE, RangeCalculator.getEffectiveRange(warrior),
            "Without weapon, effective range should equal base range");

        // Equip sword with no range bonus
        Weapon sword = new Weapon("Sword", 100, 1, 500, 1, 0, 0);
        warrior.equipWeapon(sword);
        assertEquals(RangeConstants.WARRIOR_RANGE, RangeCalculator.getEffectiveRange(warrior),
            "Sword with 0 range bonus should not change effective range");

        // Equip bow with +2 range bonus
        Weapon bow = new Weapon("Bow", 100, 1, 300, 2, 0, 2);
        warrior.equipWeapon(bow);
        assertEquals(RangeConstants.WARRIOR_RANGE + 2, RangeCalculator.getEffectiveRange(warrior),
            "Bow with +2 range bonus should extend effective range");
    }

    private static void range_getMonstersInRange_usesDynamicRange() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        // Create a sorcerer (range 2) and a warrior (range 1)
        Sorcerer sorcerer = new Sorcerer("RangeSorcerer", 1, 100, 100, 100, 100, new Wallet(1000), 0);
        Warrior warrior = new Warrior("RangeWarrior", 1, 100, 100, 100, 100, new Wallet(1000), 0);

        // Place heroes in lane 0 and lane 1
        map.placeHeroAtNexus(sorcerer, 0);  // Row 7, Lane 0
        map.placeHeroAtNexus(warrior, 1);   // Row 7, Lane 1
        
        // Move sorcerer up several times to row 3
        map.moveHero(sorcerer, Direction.UP); // Row 6
        map.moveHero(sorcerer, Direction.UP); // Row 5
        map.moveHero(sorcerer, Direction.UP); // Row 4
        map.moveHero(sorcerer, Direction.UP); // Row 3
        
        // Create monster at row 0 (monster nexus)
        Dragon dragon = new Dragon("RangeDragon", 1, 100, 100, 50);
        map.spawnMonster(dragon, 0);  // Spawns at row 0

        // Sorcerer at row 3, monster at row 0 -> distance = 3
        // Sorcerer base range = 2 -> should NOT be in range
        List<Monster> inRangeForSorcerer = map.getMonstersInRange(sorcerer);
        // Distance is 3, sorcerer range is 2, so should be empty
        assertTrue(inRangeForSorcerer.isEmpty() || !inRangeForSorcerer.contains(dragon),
            "Sorcerer at distance 3 should not reach monster with range 2");
    }

    /**
     * Deterministic Random for tests.
     * - nextDouble(): returns a fixed value
     * - nextInt(bound): returns a fixed int mod bound
     */
    private static final class FixedRandom extends Random {
        private static final long serialVersionUID = 1L;
        private final double fixedDouble;
        private final int fixedInt;

        private FixedRandom(double fixedDouble, int fixedInt) {
            this.fixedDouble = fixedDouble;
            this.fixedInt = fixedInt;
        }

        @Override
        public double nextDouble() {
            return fixedDouble;
        }

        @Override
        public int nextInt(int bound) {
            if (bound <= 0) {
                throw new IllegalArgumentException("bound must be positive");
            }
            int v = fixedInt % bound;
            return v < 0 ? v + bound : v;
        }
    }
}


