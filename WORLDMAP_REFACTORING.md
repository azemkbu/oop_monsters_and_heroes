# WorldMap Refactoring for Legends of Valor

> **Author:** ZZTang  
> **Branch:** `ZZTang`  
> **Date:** December 10, 2025

---

## Overview

This document describes the WorldMap refactoring work for the Legends of Valor extension. The changes follow the design plan we discussed:

1. Create interface for WorldMap and implementations for each game type
2. Add new TileTypes (Decorator pattern - add features for each tile)
3. Add Entity Management for hero/monster position tracking

---

## Files Changed/Added

### New Files

| File | Description |
|------|-------------|
| `src/entity/GamePiece.java` | **NEW** Interface for all game pieces (Hero, Monster) |
| `src/worldMap/IWorldMap.java` | Interface defining contract for all world maps |
| `src/worldMap/MonstersAndHeroesWorldMap.java` | Original game map implementation (8x8 random tiles) |
| `src/worldMap/LegendsOfValorWorldMap.java` | LOV game map (8x8, 3 lanes, entity management) |
| `src/worldMap/feature/TerrainBonusFeature.java` | Abstract base class for terrain bonuses |
| `src/worldMap/feature/BushFeature.java` | +10% Dexterity bonus |
| `src/worldMap/feature/CaveFeature.java` | +10% Agility bonus |
| `src/worldMap/feature/KoulouFeature.java` | +10% Strength bonus |
| `src/worldMap/feature/NexusFeature.java` | Spawn point + Market feature |

### Modified Files

| File | Changes |
|------|---------|
| `src/hero/Hero.java` | **NEW** Implements GamePiece interface (position tracking) |
| `src/monster/Monster.java` | **NEW** Implements GamePiece interface (position tracking) |
| `src/worldMap/WorldMap.java` | Now extends MonstersAndHeroesWorldMap (backward compatible) |
| `src/worldMap/Tile.java` | Added query-based terrain bonus methods |
| `src/worldMap/TileFeature.java` | Changed to query-based design |
| `src/worldMap/enums/TileType.java` | Added NEXUS, PLAIN, BUSH, CAVE, KOULOU, OBSTACLE |
| `src/utils/ConsoleColors.java` | Added more color constants |
| `src/utils/GameConstants.java` | Added LOV-specific constants |

---

## LOV Map Layout

```
Column:  0   1  |  2  |  3   4  |  5  |  6   7
        ─────────────────────────────────────────
Row 0:  [N] [N] | [I] | [N] [N] | [I] | [N] [N]  ← Monster Nexus
Row 1:  [?] [?] | [I] | [?] [?] | [I] | [?] [?]
Row 2:  [?] [?] | [I] | [?] [?] | [I] | [?] [?]
...     Random: B/C/K/P tiles (20%/20%/20%/40%)
Row 6:  [?] [?] | [I] | [?] [?] | [I] | [?] [?]
Row 7:  [N] [N] | [I] | [N] [N] | [I] | [N] [N]  ← Hero Nexus (with Market)
        ─────────────────────────────────────────
         Top Lane   Mid Lane     Bot Lane
```

- **Columns 0-1**: Top Lane
- **Column 2**: Wall (Inaccessible)
- **Columns 3-4**: Mid Lane
- **Column 5**: Wall (Inaccessible)
- **Columns 6-7**: Bot Lane

---

## Bug Found & Fixed

### Original Design (Buggy)

The original terrain effect design had a **shared state bug**:

```java
// OLD BUGGY DESIGN in TerrainBonusFeature:
public class BushFeature extends TerrainBonusFeature {
    private int lastBonusApplied = 0;  // ❌ Instance variable shared across all calls!

    public void applyEffect(Hero hero) {
        lastBonusApplied = (int)(hero.getDexterity() * 0.10);
        hero.setDexterity(hero.getDexterity() + lastBonusApplied);  // ❌ Modifies hero directly
    }

    public void removeEffect(Hero hero) {
        hero.setDexterity(hero.getDexterity() - lastBonusApplied);  // ❌ Uses stale value!
    }
}
```

**Bug Scenario:**
1. Hero A enters bush → `lastBonusApplied = 50`
2. Hero B enters same bush → `lastBonusApplied = 80` (overwritten!)
3. Hero A leaves bush → removes 80 instead of 50 → **WRONG STATS!**

### New Design (Query-based)

Following the LOV reference project pattern:

```java
// NEW SAFE DESIGN:
public class BushFeature extends TerrainBonusFeature {
    
    // Just return the multiplier - no state modification!
    public double getDexterityMultiplier() {
        return 1.10;  // 10% bonus
    }
}

// BattleEngine calculates effective stats when needed:
int effectiveDex = (int)(hero.getDexterity() * tile.getDexterityMultiplier());
```

**Benefits:**
- ✅ No shared state - stateless and thread-safe
- ✅ Hero's base stats never modified
- ✅ Easy to query anytime without lifecycle management
- ✅ No need to track enter/exit events

---

## GamePiece Interface (NEW)

A unified interface for all game pieces (Hero and Monster):

```java
public interface GamePiece {
    String getName();
    boolean isAlive();
    
    // Position tracking
    int getRow();
    int getCol();
    void setPosition(int row, int col);
    
    // Type checking
    default boolean isHero() { return false; }
    default boolean isMonster() { return false; }
}
```

### Design Principles Applied

| Principle | How Applied |
|-----------|-------------|
| **Interface Segregation** | Only essential position/status methods defined |
| **Dependency Inversion** | WorldMap depends on GamePiece abstraction |
| **Open/Closed** | New piece types just implement GamePiece |
| **Polymorphism** | Unified `getPieceAt()` method works for all |

### Changes to Hero and Monster

Both classes now:
1. `implements GamePiece`
2. Have `row` and `col` fields for position
3. Override `isHero()` or `isMonster()` to return `true`

```java
// Hero.java
public abstract class Hero implements GamePiece {
    private int row, col;
    
    @Override public int getRow() { return row; }
    @Override public int getCol() { return col; }
    @Override public void setPosition(int row, int col) { this.row = row; this.col = col; }
    @Override public boolean isHero() { return true; }
}
```

---

## Entity Management (Updated)

The `LegendsOfValorWorldMap` now tracks all hero and monster positions:

```java
// Internal tracking
private final Map<Hero, int[]> heroPositions;      // Hero -> [row, col]
private final Map<Monster, int[]> monsterPositions; // Monster -> [row, col]
```

### Available Methods

#### Unified GamePiece Query (NEW)
```java
world.getPieceAt(row, col);              // Returns any GamePiece (Hero or Monster) or null
world.hasPieceAt(row, col);              // Returns true if any piece at position
```

#### Hero Management
```java
world.placeHeroAtNexus(hero, lane);     // Place hero at spawn (lane 0/1/2)
world.getHeroPosition(hero);             // Returns int[]{row, col}
world.getHeroAt(row, col);               // Returns Hero or null
world.getHeroLane(hero);                 // Returns assigned lane (0/1/2)
world.moveHero(hero, Direction.UP);      // Move with rule validation
world.teleportHero(hero, targetHero);    // Teleport to different lane
world.recallHero(hero);                  // Return to spawn nexus
```

#### Monster Management
```java
world.spawnMonster(monster, lane);       // Spawn at monster nexus
world.getMonsterPosition(monster);       // Returns int[]{row, col}
world.getMonsterAt(row, col);            // Returns Monster or null
world.moveMonsterSouth(monster);         // Move toward hero nexus
world.removeMonster(monster);            // Remove dead monster
```

#### Combat Range Queries
```java
world.getMonstersInRange(hero);          // List<Monster> in attack range
world.getHeroesInRange(monster);         // List<Hero> in attack range
world.isAdjacent(row1, col1, row2, col2); // Check if positions are adjacent
```

#### Victory Conditions
```java
world.isHeroVictory();     // Any hero at row 0 (monster nexus)?
world.isMonsterVictory();  // Any monster at row 7 (hero nexus)?
```

---

## Integration Guide for BattleEngine

### 1. Query Terrain Bonus for Combat Calculations

```java
// When calculating attack damage:
int[] heroPos = world.getHeroPosition(hero);
Tile heroTile = world.getTile(heroPos[0], heroPos[1]);

int effectiveStrength = (int)(hero.getStrength() * heroTile.getStrengthMultiplier());
int effectiveDexterity = (int)(hero.getDexterity() * heroTile.getDexterityMultiplier());
int effectiveAgility = (int)(hero.getAgility() * heroTile.getAgilityMultiplier());

// Use effective stats for damage/spell/dodge calculations
int damage = (int)(effectiveStrength * ATTACK_MULTIPLIER);
double dodgeChance = effectiveAgility * DODGE_MULTIPLIER;
```

### 2. Get Enemies in Attack Range

```java
// For hero's turn - find attackable monsters
List<Monster> targets = world.getMonstersInRange(hero);
if (targets.isEmpty()) {
    // No monsters in range - hero cannot attack
}

// For monster's turn - find attackable heroes
List<Hero> heroesInDanger = world.getHeroesInRange(monster);
if (!heroesInDanger.isEmpty()) {
    // Monster attacks a hero
    Hero target = heroesInDanger.get(0);
} else {
    // Monster moves south
    world.moveMonsterSouth(monster);
}
```

### 3. Handle Hero Movement

```java
// Movement with built-in rule validation:
// - Cannot move into walls
// - Cannot move into another hero's position
// - Cannot move north past a monster (must kill first)

boolean moved = world.moveHero(hero, Direction.UP);
if (!moved) {
    io.printlnFail("Cannot move in that direction!");
}
```

### 4. Handle Teleport and Recall

```java
// Teleport to teammate in different lane
boolean teleported = world.teleportHero(hero, targetHero);
// Rules: different lane only, not ahead of target, not behind monster

// Recall to spawn nexus
world.recallHero(hero);  // Always succeeds, returns to original lane
```

### 5. Check Victory Conditions

```java
// After each hero/monster move:
if (world.isHeroVictory()) {
    io.printlnSuccess("Heroes win! Reached the Monster Nexus!");
    return true;  // End game
}

if (world.isMonsterVictory()) {
    io.printlnFail("Monsters win! They reached your Nexus!");
    return false;  // End game
}
```

### 6. Spawn New Monsters (Every N Rounds)

```java
if (roundNumber % MONSTER_SPAWN_INTERVAL == 0) {
    for (int lane = 0; lane < 3; lane++) {
        Monster monster = monsterFactory.createMonsterForLevel(highestHeroLevel);
        world.spawnMonster(monster, lane);
    }
}
```

---

## Class Diagram

```
================== WORLD MAP HIERARCHY ==================

                    ┌─────────────────┐
                    │    IWorldMap    │
                    │   (interface)   │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            ▼                                 ▼
┌───────────────────────┐      ┌──────────────────────────┐
│ MonstersAndHeroes     │      │ LegendsOfValorWorldMap   │
│     WorldMap          │      │  - getPieceAt()          │
│  (original game)      │      │  - moveHero()            │
└───────────────────────┘      │  - getMonstersInRange()  │
            ▲                  │  - isHeroVictory()       │
            │                  └──────────────────────────┘
┌───────────────────────┐      
│      WorldMap         │
│   (deprecated)        │
└───────────────────────┘


================== GAME PIECE HIERARCHY ==================

         ┌──────────────┐
         │  GamePiece   │
         │ (interface)  │
         │ - getName()  │
         │ - isAlive()  │
         │ - getRow()   │
         │ - getCol()   │
         └──────┬───────┘
                │
        ┌───────┴───────┐
        ▼               ▼
   ┌─────────┐     ┌─────────┐
   │  Hero   │     │ Monster │
   │ isHero()│     │isMonster│
   │ = true  │     │ = true  │
   └─────────┘     └─────────┘


================== TILE FEATURE HIERARCHY ==================

         ┌──────────────┐
         │ TileFeature  │
         │ (interface)  │
         └──────┬───────┘
                │
    ┌───────────┼───────────┬───────────┐
    ▼           ▼           ▼           ▼
┌────────┐ ┌──────────┐ ┌────────┐ ┌────────────┐
│ Market │ │ Terrain  │ │ Nexus  │ │   Bush/    │
│Feature │ │  Bonus   │ │Feature │ │ Cave/Koulou│
│        │ │ Feature  │ │        │ │  Features  │
└────────┘ │(abstract)│ └────────┘ └────────────┘
           └──────────┘
```

---

## Questions?

Feel free to reach out if you have any questions about the implementation or need any changes!

— ZZ

