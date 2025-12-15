package worldMap;

import combat.RangeCalculator;
import entity.GamePiece;
import hero.Hero;
import hero.Party;
import java.security.SecureRandom;
import java.util.*;
import market.model.Market;
import market.service.MarketFactory;
import monster.Monster;
import utils.IOUtils;
import worldMap.enums.Direction;
import worldMap.enums.TileType;
import worldMap.feature.BushFeature;
import worldMap.feature.CaveFeature;
import worldMap.feature.KoulouFeature;
import worldMap.feature.NexusFeature;

/**
 * Implementation of {@link IWorldMap} for Legends of Valor game.
 * Represents an 8x8 grid divided into three lanes separated by walls.
 * Manages hero and monster positions via GamePiece interface.
 * 
 * Layout:
 * - Columns 0-1: Top Lane
 * - Column 2: Wall (Inaccessible)
 * - Columns 3-4: Mid Lane
 * - Column 5: Wall (Inaccessible)
 * - Columns 6-7: Bot Lane
 * - Row 0: Monster Nexus
 * - Row 7: Hero Nexus
 */
public class LegendsOfValorWorldMap implements ILegendsWorldMap {

    /** Default map size for Legends of Valor */
    public static final int DEFAULT_SIZE = 8;

    /** Lane definitions: each lane consists of two columns */
    public static final int[][] LANE_COLUMNS = {
            {0, 1},  // Top lane (Lane 0)
            {3, 4},  // Mid lane (Lane 1)
            {6, 7}   // Bot lane (Lane 2)
    };

    /** Wall columns that separate lanes */
    public static final int[] WALL_COLUMNS = {2, 5};

    /** Monster Nexus row (top of map) */
    public static final int MONSTER_NEXUS_ROW = 0;

    /** Hero Nexus row (bottom of map) */
    public static final int HERO_NEXUS_ROW = 7;

    /** Different distributions of all the different type of tiles */
    public static final double BUSH_RATIO = 0.20;
    public static final double CAVE_RATIO = 0.20;
    public static final double KOULOU_RATIO = 0.20;
    public static final double PLAIN_RATIO = 0.30;
    public static final double OBSTACLE_RATIO = 0.10;

    private final int size;
    private final Tile[][] grid;
    private final Random random = new SecureRandom();
    private final MarketFactory marketFactory;

    // ==================== ENTITY MANAGEMENT ====================
    // Track entity positions for quick lookup
    // Key: Entity, Value: int[]{row, col}
    
    /** Hero positions: maps each hero to their [row, col] */
    private final Map<Hero, int[]> heroPositions;
    
    /** Monster positions: maps each monster to their [row, col] */
    private final Map<Monster, int[]> monsterPositions;
    
    /** All heroes in this game */
    private final List<Hero> heroes;
    
    /** All monsters currently on the map */
    private final List<Monster> monsters;
    
    /** Maps each hero to their assigned lane (for recall) */
    private final Map<Hero, Integer> heroLanes;

    /**
     * Creates a new Legends of Valor world map.
     * @param marketFactory factory for creating markets
     */
    public LegendsOfValorWorldMap(MarketFactory marketFactory) {
        this.size = DEFAULT_SIZE;
        this.grid = new Tile[size][size];
        this.marketFactory = marketFactory;
        
        // Initialize entity management
        this.heroPositions = new HashMap<>();
        this.monsterPositions = new HashMap<>();
        this.heroes = new ArrayList<>();
        this.monsters = new ArrayList<>();
        this.heroLanes = new HashMap<>();
        
        generateLayout();
    }

    // ==================== UNIFIED GAMEPIECE QUERY ====================

    /**
     * Gets any GamePiece (Hero or Monster) at the specified position.
     * This is the unified query method for all game pieces.
     * 
     * Uses GamePiece interface for polymorphic access.
     * 
     * @param row the row
     * @param col the column
     * @return the GamePiece at that position, or null if none
     */
    public GamePiece getPieceAt(int row, int col) {
        Hero hero = getHeroAt(row, col);
        if (hero != null) return hero;
        
        Monster monster = getMonsterAt(row, col);
        if (monster != null) return monster;
        
        return null;
    }

    /**
     * Checks if any piece is at the specified position.
     * @param row the row
     * @param col the column
     * @return true if a piece is at the position
     */
    public boolean hasPieceAt(int row, int col) {
        return getPieceAt(row, col) != null;
    }

    // ==================== HERO MANAGEMENT ====================

    /**
     * Places a hero at their starting position (Hero Nexus).
     * Heroes spawn in the left column of their lane.
     * 
     * Also updates the Hero's position via GamePiece interface.
     * 
     * @param hero The hero to place
     * @param lane The lane to place them in (0=top, 1=mid, 2=bot)
     */
    public void placeHeroAtNexus(Hero hero, int lane) {
        int row = HERO_NEXUS_ROW;
        int col = LANE_COLUMNS[lane][0]; // Left column of lane
        
        heroPositions.put(hero, new int[]{row, col});
        hero.setPosition(row, col);  // Sync GamePiece position
        heroLanes.put(hero, lane);
        
        if (!heroes.contains(hero)) {
            heroes.add(hero);
        }
    }

    /**
     * Gets the position of a hero.
     * @param hero the hero
     * @return int[]{row, col} or null if not found
     */
    public int[] getHeroPosition(Hero hero) {
        return heroPositions.get(hero);
    }

    /**
     * Gets the hero at a specific position.
     * @param row the row
     * @param col the column
     * @return the hero at that position, or null if none
     */
    public Hero getHeroAt(int row, int col) {
        for (Map.Entry<Hero, int[]> entry : heroPositions.entrySet()) {
            int[] pos = entry.getValue();
            if (pos[0] == row && pos[1] == col) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets all heroes.
     * @return unmodifiable list of all heroes
     */
    public List<Hero> getHeroes() {
        return Collections.unmodifiableList(heroes);
    }

    /**
     * Gets all alive heroes.
     * @return list of alive heroes
     */
    public List<Hero> getAliveHeroes() {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.isAlive()) {
                alive.add(h);
            }
        }
        return alive;
    }

    /**
     * Gets the assigned lane for a hero.
     * @param hero the hero
     * @return lane index (0, 1, 2) or -1 if not assigned
     */
    public int getHeroLane(Hero hero) {
        Integer lane = heroLanes.get(hero);
        return lane != null ? lane : -1;
    }

    /**
     * Moves a hero in the specified direction.
     * Validates: bounds, accessibility, not occupied by another hero,
     * and cannot move behind (north of) a monster without killing it.
     * 
     * @param hero the hero to move
     * @param direction the direction to move
     * @return true if the move was successful
     */
    public boolean moveHero(Hero hero, Direction direction) {
        int[] pos = heroPositions.get(hero);
        if (pos == null) return false;

        int newRow = pos[0] + direction.getRow();
        int newCol = pos[1] + direction.getCol();

        // Check bounds and accessibility
        if (!isAccessible(newRow, newCol)) {
            return false;
        }

        // Check if another hero is there
        if (getHeroAt(newRow, newCol) != null) {
            return false;
        }

        // Allow hero + monster co-occupancy (Dis.txt: a cell can hold up to one hero and one monster).

        // Check if trying to move behind (north of) a monster
        if (direction == Direction.UP) {
            // Cannot move north if there's a monster between current and target
            if (!canMoveNorth(hero, pos[0], pos[1], newRow, newCol)) {
                return false;
            }
        }

        // Update position (both Map and GamePiece)
        heroPositions.put(hero, new int[]{newRow, newCol});
        hero.setPosition(newRow, newCol);  // Sync GamePiece position
        return true;
    }

    /**
     * Checks if hero can move north (towards monster nexus).
     * Rule: Cannot move PAST a monster (to a row north of the monster) if that monster has the hero in attack range.
     * Hero CAN move onto the monster's cell (co-occupancy is allowed).
     * Uses dynamic attack range from RangeCalculator.
     */
    private boolean canMoveNorth(Hero hero, int fromRow, int fromCol, int toRow, int toCol) {
        for (Monster m : getAliveMonsters()) {
            int[] monsterPos = monsterPositions.get(m);
            if (monsterPos == null) continue;
            
            int monsterRange = RangeCalculator.getEffectiveRange(m);
            
            // If hero's current position is within monster's attack range
            if (RangeCalculator.isWithinRange(fromRow, fromCol, monsterPos[0], monsterPos[1], monsterRange)) {
                // And hero is trying to move north PAST the monster (not onto it)
                // toRow < monsterPos[0] means moving to a row north of the monster
                if (toRow < monsterPos[0]) {
                    return false;  // Blocked by monster - must kill it first
                }
            }
        }
        return true;
    }

    /**
     * Teleports a hero to a position adjacent to another hero in a different lane.
     * Rules:
     * - Must be different lanes
     * - Cannot teleport ahead of target hero
     * - Cannot teleport to occupied position
     * - Cannot teleport behind a monster
     * 
     * @param hero the hero to teleport
     * @param targetHero the hero to teleport near
     * @return true if teleport successful
     */
    public boolean teleportHero(Hero hero, Hero targetHero) {
        int[] fromPos = heroPositions.get(hero);
        int[] targetPos = heroPositions.get(targetHero);
        
        if (fromPos == null || targetPos == null) return false;

        int fromLane = getLaneIndex(fromPos[1]);
        int targetLane = getLaneIndex(targetPos[1]);

        // Must be different lanes
        if (fromLane == targetLane) return false;

        int targetRow = targetPos[0];
        int[] targetLaneCols = LANE_COLUMNS[targetLane];

        // Find valid teleport destination
        for (int col : targetLaneCols) {
            for (int row = targetRow; row < size; row++) { // Cannot teleport ahead (lower row)
                // Cannot teleport onto another hero (but may teleport onto a monster: hero+monster can share a cell)
                if (getHeroAt(row, col) == null) {
                    Tile destTile = getTile(row, col);
                    if (destTile.isAccessible() && isAdjacent(targetPos[0], targetPos[1], row, col)) {
                        // Valid teleport destination (update both Map and GamePiece)
                        heroPositions.put(hero, new int[]{row, col});
                        hero.setPosition(row, col);  // Sync GamePiece position
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recalls a hero back to their spawn nexus.
     * @param hero the hero to recall
     */
    public void recallHero(Hero hero) {
        Integer lane = heroLanes.get(hero);
        if (lane != null) {
            placeHeroAtNexus(hero, lane);
        }
    }

    // ==================== MONSTER MANAGEMENT ====================

    /**
     * Spawns a monster at the Monster Nexus.
     * Monsters spawn in the right column of their lane.
     * 
     * Also updates the Monster's position via GamePiece interface.
     * 
     * @param monster the monster to spawn
     * @param lane the lane to spawn in (0=top, 1=mid, 2=bot)
     */
    public void spawnMonster(Monster monster, int lane) {
        int row = MONSTER_NEXUS_ROW;
        int col = LANE_COLUMNS[lane][1]; // Right column of lane
        
        // Do not spawn on top of an existing piece.
        if (getHeroAt(row, col) != null || getMonsterAt(row, col) != null) {
            return;
        }

        monsterPositions.put(monster, new int[]{row, col});
        monster.setPosition(row, col);  // Sync GamePiece position
        if (!monsters.contains(monster)) {
            monsters.add(monster);
        }
    }

    /**
     * Gets the position of a monster.
     * @param monster the monster
     * @return int[]{row, col} or null if not found
     */
    public int[] getMonsterPosition(Monster monster) {
        return monsterPositions.get(monster);
    }

    /**
     * Gets the monster at a specific position.
     * @param row the row
     * @param col the column
     * @return the monster at that position, or null if none
     */
    public Monster getMonsterAt(int row, int col) {
        for (Map.Entry<Monster, int[]> entry : monsterPositions.entrySet()) {
            int[] pos = entry.getValue();
            if (pos[0] == row && pos[1] == col) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Gets all monsters.
     * @return unmodifiable list of all monsters
     */
    public List<Monster> getMonsters() {
        return Collections.unmodifiableList(monsters);
    }

    /**
     * Gets all alive monsters.
     * @return list of alive monsters
     */
    public List<Monster> getAliveMonsters() {
        List<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.isAlive()) {
                alive.add(m);
            }
        }
        return alive;
    }

    /**
     * Moves a monster one step south (towards hero nexus).
     * @param monster the monster to move
     * @return true if move successful
     */
    public boolean moveMonsterSouth(Monster monster) {
        int[] pos = monsterPositions.get(monster);
        if (pos == null) return false;

        int newRow = pos[0] + 1; // Move south
        int col = pos[1];

        if (newRow >= size) return false;

        Tile tile = getTile(newRow, col);
        if (!tile.isAccessible()) return false;

        // Check if another monster is there
        if (getMonsterAt(newRow, col) != null) return false;

        // Monsters cannot move onto a hero tile (they must attack when in range)
        if (getHeroAt(newRow, col) != null) return false;

        // Update position (both Map and GamePiece)
        monsterPositions.put(monster, new int[]{newRow, col});
        monster.setPosition(newRow, col);  // Sync GamePiece position
        return true;
    }



    public boolean moveMonsterEast(Monster monster) {
        int[] pos = monsterPositions.get(monster);
        if (pos == null) return false;

        int row = pos[0];
        int newCol = pos[1] + 1; // Move east (col increases)

        if (newCol >= size) return false;

        Tile tile = getTile(row, newCol);
        if (tile == null || !tile.isAccessible()) return false;

        // Check if another monster is there
        if (getMonsterAt(row, newCol) != null) return false;

        // Monsters cannot move onto a hero tile (they must attack when in range)
        if (getHeroAt(row, newCol) != null) return false;

        // Update position (both Map and GamePiece)
        monsterPositions.put(monster, new int[]{row, newCol});
        monster.setPosition(row, newCol);
        return true;
    }

    public boolean moveMonsterWest(Monster monster) {
        int[] pos = monsterPositions.get(monster);
        if (pos == null) return false;

        int row = pos[0];
        int newCol = pos[1] - 1; // Move west (col decreases)

        if (newCol < 0) return false;

        Tile tile = getTile(row, newCol);
        if (tile == null || !tile.isAccessible()) return false;

        // Check if another monster is there
        if (getMonsterAt(row, newCol) != null) return false;

        // Monsters cannot move onto a hero tile (they must attack when in range)
        if (getHeroAt(row, newCol) != null) return false;

        // Update position (both Map and GamePiece)
        monsterPositions.put(monster, new int[]{row, newCol});
        monster.setPosition(row, newCol);
        return true;
    }

    /**
     * Finds the closest hero to a monster that the monster could pursue.
     * Returns null if no heroes are alive.
     */
    public Hero findClosestHero(Monster monster) {
        int[] monsterPos = monsterPositions.get(monster);
        if (monsterPos == null) return null;

        Hero closest = null;
        int minDist = Integer.MAX_VALUE;

        for (Hero h : heroes) {
            if (!h.isAlive()) continue;
            int[] heroPos = heroPositions.get(h);
            if (heroPos == null) continue;

            int dist = Math.abs(monsterPos[0] - heroPos[0]) + Math.abs(monsterPos[1] - heroPos[1]);
            if (dist < minDist) {
                minDist = dist;
                closest = h;
            }
        }
        return closest;
    }

    /**
     * Uses BFS to find the first step of the shortest path from monster to target hero.
     * Returns the next position [row, col] the monster should move to, or null if no path exists.
     */
    public int[] findNextStepTowardHero(Monster monster, Hero targetHero) {
        int[] monsterPos = monsterPositions.get(monster);
        int[] heroPos = heroPositions.get(targetHero);
        if (monsterPos == null || heroPos == null) return null;

        int startRow = monsterPos[0];
        int startCol = monsterPos[1];
        int goalRow = heroPos[0];
        int goalCol = heroPos[1];

        // If already adjacent, no need to move
        if (isAdjacent(startRow, startCol, goalRow, goalCol)) {
            return null;
        }

        // BFS to find shortest path
        // parent[row][col] stores the previous position in the path
        int[][][] parent = new int[size][size][];
        boolean[][] visited = new boolean[size][size];

        java.util.Queue<int[]> queue = new java.util.LinkedList<>();
        queue.offer(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        // 4-directional movement (up, down, left, right)
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int row = current[0];
            int col = current[1];

            // Check if we've reached adjacent to goal (attack range)
            if (isAdjacent(row, col, goalRow, goalCol)) {
                // Reconstruct path back to find first step from start
                return reconstructFirstStep(parent, startRow, startCol, row, col);
            }

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) continue;
                if (visited[newRow][newCol]) continue;

                Tile tile = getTile(newRow, newCol);
                if (tile == null || !tile.isAccessible()) continue;

                // Monster cannot move onto another monster's cell
                if (getMonsterAt(newRow, newCol) != null) continue;

                // Monster cannot move onto a hero's cell (must attack from adjacent)
                if (getHeroAt(newRow, newCol) != null && !(newRow == goalRow && newCol == goalCol)) continue;

                visited[newRow][newCol] = true;
                parent[newRow][newCol] = new int[]{row, col};
                queue.offer(new int[]{newRow, newCol});
            }
        }

        // No path found
        return null;
    }

    /**
     * Reconstructs the first step from start position by tracing back from end.
     */
    private int[] reconstructFirstStep(int[][][] parent, int startRow, int startCol, int endRow, int endCol) {
        int row = endRow;
        int col = endCol;

        // Trace back until we find the cell whose parent is the start
        while (parent[row][col] != null) {
            int[] prev = parent[row][col];
            if (prev[0] == startRow && prev[1] == startCol) {
                // This cell is the first step from start
                return new int[]{row, col};
            }
            row = prev[0];
            col = prev[1];
        }
        return null;
    }

    /**
     * Moves a monster to a specific position (used by BFS pathfinding).
     * @return true if successful
     */
    public boolean moveMonsterTo(Monster monster, int newRow, int newCol) {
        int[] pos = monsterPositions.get(monster);
        if (pos == null) return false;

        if (newRow < 0 || newRow >= size || newCol < 0 || newCol >= size) return false;

        Tile tile = getTile(newRow, newCol);
        if (tile == null || !tile.isAccessible()) return false;

        if (getMonsterAt(newRow, newCol) != null) return false;
        if (getHeroAt(newRow, newCol) != null) return false;

        monsterPositions.put(monster, new int[]{newRow, newCol});
        monster.setPosition(newRow, newCol);
        return true;
    }

    /**
     * Gets the lane (0, 1, 2) a monster is in, or -1 if not on map.
     */
    public int getMonsterLane(Monster monster) {
        int[] pos = monsterPositions.get(monster);
        if (pos == null) return -1;
        int col = pos[1];
        for (int i = 0; i < LANE_COLUMNS.length; i++) {
            int[] laneCols = LANE_COLUMNS[i];
            if (col >= laneCols[0] && col <= laneCols[1]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Removes a dead monster from the world.
     * @param monster the monster to remove
     */
    public void removeMonster(Monster monster) {
        monsterPositions.remove(monster);
        monsters.remove(monster);
    }

    // ==================== COMBAT RANGE QUERIES ====================

    /**
     * Checks if two positions are adjacent (within attack range).
     * Attack range includes current cell and all 8 neighbors.
     * 
     * @return true if positions are adjacent or same
     */
    public boolean isAdjacent(int row1, int col1, int row2, int col2) {
        int rowDiff = Math.abs(row1 - row2);
        int colDiff = Math.abs(col1 - col2);
        return rowDiff <= 1 && colDiff <= 1;
    }

    /**
     * Checks if a hero is within attack range of a position.
     */
    public boolean isInAttackRange(Hero hero, int targetRow, int targetCol) {
        int[] pos = heroPositions.get(hero);
        if (pos == null) return false;
        return isAdjacent(pos[0], pos[1], targetRow, targetCol);
    }

    /**
     * Gets all monsters within attack range of a hero.
     * @param hero the hero
     * @return list of monsters in range
     */
    public List<Monster> getMonstersInRange(Hero hero) {
        List<Monster> inRange = new ArrayList<>();
        int[] heroPos = heroPositions.get(hero);
        if (heroPos == null) return inRange;

        int range = RangeCalculator.getEffectiveRange(hero);

        for (Monster m : getAliveMonsters()) {
            int[] monsterPos = monsterPositions.get(m);
            if (monsterPos != null && 
                RangeCalculator.isWithinRange(heroPos[0], heroPos[1], monsterPos[0], monsterPos[1], range)) {
                inRange.add(m);
            }
        }
        return inRange;
    }

    /**
     * Gets all heroes within attack range of a monster.
     * @param monster the monster
     * @return list of heroes in range
     */
    public List<Hero> getHeroesInRange(Monster monster) {
        List<Hero> inRange = new ArrayList<>();
        int[] monsterPos = monsterPositions.get(monster);
        if (monsterPos == null) return inRange;

        int range = RangeCalculator.getEffectiveRange(monster);

        for (Hero h : heroes) {
            if (h.isAlive()) {
                int[] heroPos = heroPositions.get(h);
                if (heroPos != null && 
                    RangeCalculator.isWithinRange(monsterPos[0], monsterPos[1], heroPos[0], heroPos[1], range)) {
                    inRange.add(h);
                }
            }
        }
        return inRange;
    }

    // ==================== TERRAIN BONUS QUERIES ====================

    /**
     * Gets terrain bonus multipliers for a hero at their current position.
     * 
     * This is the query-based approach: instead of modifying hero stats,
     * we return the multipliers and let the caller apply them.
     * 
     * @param hero the hero
     * @return map of stat name to multiplier (e.g., "strength" -> 1.10)
     */
    public Map<String, Double> getTerrainBonus(Hero hero) {
        Map<String, Double> bonus = new HashMap<>();
        int[] pos = heroPositions.get(hero);
        if (pos == null) return bonus;

        Tile tile = getTile(pos[0], pos[1]);
        
        double strMult = tile.getStrengthMultiplier();
        double dexMult = tile.getDexterityMultiplier();
        double agiMult = tile.getAgilityMultiplier();
        
        if (strMult != 1.0) bonus.put("strength", strMult);
        if (dexMult != 1.0) bonus.put("dexterity", dexMult);
        if (agiMult != 1.0) bonus.put("agility", agiMult);
        
        return bonus;
    }

    // ==================== VICTORY CONDITIONS ====================

    /**
     * Checks if any hero has reached the monster nexus (heroes win).
     * @return true if a hero is at row 0
     */
    public boolean isHeroVictory() {
        for (Hero hero : heroes) {
            int[] pos = heroPositions.get(hero);
            if (pos != null && pos[0] == MONSTER_NEXUS_ROW) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any monster has reached the hero nexus (monsters win).
     * @return true if a monster is at row 7
     */
    public boolean isMonsterVictory() {
        for (Monster monster : getAliveMonsters()) {
            int[] pos = monsterPositions.get(monster);
            if (pos != null && pos[0] == HERO_NEXUS_ROW) {
                return true;
            }
        }
        return false;
    }

    // ==================== IWorldMap INTERFACE ====================

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Tile getTile(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IndexOutOfBoundsException("Invalid tile coordinates: (" + row + ", " + col + ')');
        }
        return grid[row][col];
    }

    @Override
    public boolean checkBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    @Override
    public boolean isAccessible(int row, int col) {
        return checkBounds(row, col) && grid[row][col].isAccessible();
    }

    @Override
    public boolean moveParty(Party party, Direction direction) {
        // For backward compatibility with original game
        int currentRow = party.getRow();
        int currentCol = party.getCol();

        int newRow = currentRow + direction.getRow();
        int newCol = currentCol + direction.getCol();

        if (!isAccessible(newRow, newCol)) {
            return false;
        }

        party.setPosition(newRow, newCol);
        return true;
    }

    @Override
    public Tile getPartyTile(Party party) {
        return getTile(party.getRow(), party.getCol());
    }

    public void printMap(Party party) {
        throw new UnsupportedOperationException("LOV map does not print directly. Use LovView + LegendsMapFormatter.");
    }

    /**
     * Prints the map showing heroes and monsters positions.
     */
    public void printMap() {
        throw new UnsupportedOperationException("LOV map does not print directly. Use LovView + LegendsMapFormatter.");
    }

    public IOUtils getIoUtils() {
        throw new UnsupportedOperationException("LOV map does not expose IO. Use LovView for all I/O.");
    }

    // ==================== LANE UTILITIES ====================

    /**
     * Gets the lane index for a given column.
     * @param col the column index
     * @return the lane index (0=top, 1=mid, 2=bot), or -1 if wall
     */
    public int getLaneIndex(int col) {
        for (int lane = 0; lane < LANE_COLUMNS.length; lane++) {
            for (int laneCol : LANE_COLUMNS[lane]) {
                if (col == laneCol) {
                    return lane;
                }
            }
        }
        return -1; // Wall column
    }

    /**
     * Checks if the given column is a wall.
     * @param col the column index
     * @return true if the column is a wall
     */
    public boolean isWallColumn(int col) {
        for (int wallCol : WALL_COLUMNS) {
            if (col == wallCol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given column is a wall.
     * @param col the column index
     * @return true if the column is a wall
     */
    public boolean isObstacle(int col) {
        for (int wallCol : WALL_COLUMNS) {
            if (col == wallCol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the spawn column for a hero in the given lane.
     * Heroes spawn in the left column of their lane.
     * @param laneIndex the lane index (0=top, 1=mid, 2=bot)
     * @return the spawn column
     */
    public int getHeroSpawnColumn(int laneIndex) {
        return LANE_COLUMNS[laneIndex][0];
    }

    /**
     * Gets the spawn column for a monster in the given lane.
     * Monsters spawn in the right column of their lane.
     * @param laneIndex the lane index (0=top, 1=mid, 2=bot)
     * @return the spawn column
     */
    public int getMonsterSpawnColumn(int laneIndex) {
        return LANE_COLUMNS[laneIndex][1];
    }

    /**
     * Checks if two columns are in the same lane.
     */
    public boolean isSameLane(int col1, int col2) {
        int lane1 = getLaneIndex(col1);
        int lane2 = getLaneIndex(col2);
        return lane1 != -1 && lane1 == lane2;
    }

    // ==================== MAP GENERATION ====================

    /**
     * Generates the Legends of Valor map layout.
     */
    private void generateLayout() {
        // First, set all tiles to inaccessible
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                grid[row][col] = new Tile(TileType.INACCESSIBLE, null);
            }
        }

        // Generate lane tiles
        for (int laneIndex = 0; laneIndex < LANE_COLUMNS.length; laneIndex++) {
            int[] laneCols = LANE_COLUMNS[laneIndex];
            generateLaneTiles(laneIndex, laneCols);
        }
    }

    /**
     * Generates tiles for a single lane.
     */
    private void generateLaneTiles(int laneIndex, int[] laneCols) {
        int tilesPerColumn = size - 2; // Rows 1 to 6
        int totalLaneTiles = tilesPerColumn * laneCols.length;

        // Create distribution of tile types
        List<TileType> tileTypes = new ArrayList<>();
        int numBush = (int) Math.round(totalLaneTiles * BUSH_RATIO);
        int numCave = (int) Math.round(totalLaneTiles * CAVE_RATIO);
        int numKoulou = (int) Math.round(totalLaneTiles * KOULOU_RATIO);
        int numObstacles = (int) Math.round(totalLaneTiles * OBSTACLE_RATIO);
        int numPlain = totalLaneTiles - numBush - numCave - numKoulou - numObstacles;
        
        // Adds the different file types to a list that we can then shuffle
        addTileTypes(tileTypes, TileType.BUSH, numBush);
        addTileTypes(tileTypes, TileType.CAVE, numCave);
        addTileTypes(tileTypes, TileType.KOULOU, numKoulou);
        addTileTypes(tileTypes, TileType.OBSTACLE, numObstacles);
        addTileTypes(tileTypes, TileType.PLAIN, numPlain);

        Collections.shuffle(tileTypes, random);

        // Set Nexus tiles
        for (int col : laneCols) {
            grid[MONSTER_NEXUS_ROW][col] = createNexusTile(false, laneIndex);
            grid[HERO_NEXUS_ROW][col] = createNexusTile(true, laneIndex);
        }

        // Fill lane tiles (rows 1 to 6)
        int tileIndex = 0;
        for (int row = 1; row < size - 1; row++) {
            for (int col : laneCols) {
                TileType type = tileTypes.get(tileIndex++);
                TileFeature feature = createFeatureForType(type);
                grid[row][col] = new Tile(type, feature);
            }
        }
    }


    // Creates the nexuses for heroes and monsters
    private Tile createNexusTile(boolean isHeroNexus, int laneIndex) {
        Market market = isHeroNexus ? marketFactory.createRandomMarket() : null;
        NexusFeature feature = new NexusFeature(market, isHeroNexus, laneIndex);
        return new Tile(TileType.NEXUS, feature);
    }

    private TileFeature createFeatureForType(TileType type) {
        switch (type) {
            case BUSH:
                return new BushFeature();
            case CAVE:
                return new CaveFeature();
            case KOULOU:
                return new KoulouFeature();
            default:
                return null;
        }
    }

    // Adds tile to the tile
    private void addTileTypes(List<TileType> list, TileType type, int count) {
        for (int i = 0; i < count; i++) {
            list.add(type);
        }
    }

    /**
     * Checks if a hero has reached the monster's Nexus.
     */
    public boolean isAtMonsterNexus(int row) {
        return row == MONSTER_NEXUS_ROW;
    }

    /**
     * Checks if a monster has reached the hero's Nexus.
     */
    public boolean isAtHeroNexus(int row) {
        return row == HERO_NEXUS_ROW;
    }
}
