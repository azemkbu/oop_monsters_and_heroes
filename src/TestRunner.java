import battle.enums.HeroActionType;
import hero.Hero;
import hero.Wallet;
import hero.Warrior;
import lov.usecase.LovActionExecutor;
import lov.usecase.LovActionResult;
import lov.usecase.requests.RemoveObstacleRequest;
import market.service.MarketFactory;
import monster.Dragon;
import monster.Monster;
import worldMap.LegendsOfValorWorldMap;
import worldMap.Tile;
import worldMap.enums.Direction;
import worldMap.enums.TileType;

import java.util.ArrayList;
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

        run(failures, "tileObstacle_isAccessible_and_removeObstacle", TestRunner::tileObstacle_isAccessible_and_removeObstacle);
        run(failures, "lov_moveHero_blocksObstacle_and_monsterTile", TestRunner::lov_moveHero_blocksObstacle_and_monsterTile);
        run(failures, "lov_teleport_and_recall_basic", TestRunner::lov_teleport_and_recall_basic);
        run(failures, "lov_spawnMonster_doesNotStackOnSpawn", TestRunner::lov_spawnMonster_doesNotStackOnSpawn);
        run(failures, "lov_moveMonsterSouth_blockedByObstacle", TestRunner::lov_moveMonsterSouth_blockedByObstacle);
        run(failures, "lov_removeObstacleAction_changesTileType", TestRunner::lov_removeObstacleAction_changesTileType);

        if (!failures.isEmpty()) {
            System.err.println("FAILED (" + failures.size() + "):");
            for (String f : failures) {
                System.err.println(" - " + f);
            }
            System.exit(1);
        }

        System.out.println("ALL TESTS PASSED (" + 6 + ")");
    }

    private static void run(List<String> failures, String name, Runnable test) {
        try {
            test.run();
        } catch (AssertionError ae) {
            failures.add(name + ": " + ae.getMessage());
        } catch (Throwable t) {
            failures.add(name + ": threw " + t.getClass().getSimpleName() + " - " + t.getMessage());
        }
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
        Monster monster = new Dragon("DragonA", 1, 10, 10, 10);
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

        // Hero at row 7 col 1 tries to move UP onto monster at row 6 col 1 -> must fail.
        assertTrue(!map.moveHero(hero, Direction.UP), "Hero must not move onto a monster tile");
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

        Monster m1 = new Dragon("Dragon1", 1, 10, 10, 10);
        Monster m2 = new Dragon("Dragon2", 1, 10, 10, 10);

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

        Monster m1 = new Dragon("Dragon1", 1, 10, 10, 10);
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

    private static void lov_removeObstacleAction_changesTileType() {
        LegendsOfValorWorldMap map = new LegendsOfValorWorldMap(new MarketFactory());
        makeLovDeterministicPlain(map);

        Hero hero = new Warrior("HeroA", 1, 10, 10, 10, 10, new Wallet(0), 0);
        map.placeHeroAtNexus(hero, 0); // row 7 col 0

        // Put obstacle at row 6 col 0 (UP from hero)
        map.getTile(6, 0).setType(TileType.OBSTACLE);
        map.getTile(6, 0).setFeature(null);

        LovActionExecutor executor = new LovActionExecutor(map);
        LovActionResult result = executor.execute(
                HeroActionType.REMOVE_OBSTACLE,
                hero,
                map.getAliveMonsters(),
                new RemoveObstacleRequest(Direction.UP)
        );
        assertTrue(result.isSuccess(), "RemoveObstacle usecase should succeed");

        assertEquals(TileType.PLAIN, map.getTile(6, 0).getType(), "RemoveObstacle action should turn OBSTACLE into PLAIN");
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
}


