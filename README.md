# CS611-Assignment <Legends: Monsters and Heroes>
-------------------------------------------------------------
--------------
- Name: Azem Kakitaeva, Zhengzheng Tang, Antony Ponomarev
- Email: azemk@bu.edu, zztangbu@bu.edu, antonyp@bu.edu
- Student ID: U51216906, U07312313, U43948985


## File Structure 

```text
/battle/engine
  BattleEngine.java              // Battle engine contract
  BattleEngineImpl.java          // Turn-based battle flow (MH battles), RNG-injectable for deterministic tests

/battle/enums
  EquipChoice.java               // Weapon/Armor/Cancel
  HeroActionType.java            // ATTACK/CAST_SPELL/USE_POTION/EQUIP/SKIP (+ LoV actions)

/entity
  GamePiece.java                  // Common interface for Hero/Monster (position + alive query)

/game
  Game.java                      // Interface defining main game methods
  GameCommand.java               // Enum mapping keyboard commands to game actions
  GameImpl.java                  // Handles the game loop of Heroes and Monsters
  GameLauncher.java              // Entry class responsible for loading resources, selecting between games, and running the game
  LegendsOfValorGameImpl.java    // Handles the game loop of Legends of Valor

/hero/enums
  HeroSkill.java                 // Enum representing hero skill categories
  HeroType.java                  // Enum representing hero types

/hero
  Hero.java                      // Abstract base class defining shared hero attributes and behavior
  Inventory.java                 // Class managing a hero’s inventory
  Paladin.java                   // Subclass of Hero representing the Paladin hero type
  Party.java                     // Class representing the player's active group of heroes
  Sorcerer.java                  // Subclass of Hero representing the Sorcerer hero type
  Wallet.java                    // Class representing a hero’s gold amount 
  Warrior.java                   // Subclass of Hero representing the Warrior hero type

/market/command
  MarketAction.java              // Enum representing available market menu operations
  MarketCommand.java             // Command interface defining a market action executable by a hero
  MarketCommandConfig.java       // Configuration mapping MarketAction values to command implementations

/market/model
  Market.java                    // Class representing a market and its available items

/market/model/item
  Armor.java                     // Subclass of Item representing defensive armor
  Item.java                      // Abstract base class representing a generic market item
  ItemType.java                  // Enum defining item categories
  Potion.java                    // Subclass of Item representing a consumable potion
  Spell.java                     // Subclass of Item representing castable magical spells
  SpellType.java                 // Enum defining categories of spells
  StatType.java                  // Enum representing hero statistics affected by items
  Weapon.java                    // Subclass of Item representing weapon

/market/service
  MarketFactory.java             // Factory class responsible for constructing Market instances
  MarketService.java             // Interface defining market operations
  MarketServiceImpl.java         // Implementation of business logic for market operations

/market/ui
  MarketMenu.java                // Interface defining display operations for the market UI
  MarketMenuImpl.java            // Console implementation of the market interaction menu

/monster/enums
  MonsterAttribute.java          // Enum representing monster attributes

/monster
  Dragon.java                    // Subclass of Monster representing a Dragon 
  Exoskeleton.java               // Subclass of Monster representing an Exoskeleton 
  IMonsterFactory.java           // Port for creating monsters (deterministic in tests)
  Monster.java                   // Abstract base class defining core monster attributes and behavior
  MonsterFactory.java            // Factory class generating monsters for battles
  Spirit.java                    // Subclass of Monster representing a Spirit

/lov/usecase
  LovActionExecutor.java         // LoV actions use-case layer (no I/O), RNG-injectable for tests
  LovActionResult.java           // Use-case result object (messages for View)
  requests/*                     // Typed request objects per action
  helper/LovRangeUtils.java      // LoV 8-neighborhood range check

/ui
  /launcher                       // Console launcher (game mode + hero selection)
  /mh                             // MH view (all MH I/O)
  /lov                            // LoV view (all LoV I/O)
  /battle                         // Battle view (all battle I/O)
  /formatter                      // Map formatters (LegendsMapFormatter, MhMapFormatter)

/upload/base
  GenericFileLoader.java         // Generic loader for building objects from uploaded text files
  LineMapper.java                // Functional mapper converting a text line into an object
  TextFileReader.java            // Backend I/O port for reading data lines
  DefaultTextFileReader.java     // Default adapter wiring
  TextFileUtils.java             // Legacy helper (disabled; use TextFileReader adapter)

/upload/adapter
  FileSystemTextFileReader.java  // Adapter implementation backed by local filesystem

/upload
  ArmorFileLoader.java           // Loader class for constructing Armor objects from file data
  HeroFileLoader.java            // Loader class for constructing Hero objects from file data
  MonsterFileLoader.java         // Loader class for constructing Monster objects from file data
  PotionFileLoader.java          // Loader class for constructing Potion objects from file data
  SpellFileLoader.java           // Loader class for constructing Spell objects from file data
  WeaponFileLoader.java          // Loader class for constructing Weapon objects from file data

/utils
  ConsoleColors.java             // ANSI color definitions enabling styled console output
  ConsoleIOUtils.java            // Implementation of IOUtils interface
  GameConstants.java             // Centralized constants for game 
  IOUtils.java                   // Utility interface for handling generic input/output operations
  MessageUtils.java              // Repository for centralized UI text messages
  EndOfInputException.java       // Custom RuntimeException used to gracefully exit on EOF (no stack trace)

/worldMap/enums
  Direction.java                 // Enum representing movement directions on the world map
  TileType.java                  // Enum defining categories of map tiles

/worldMap
  MarketTileFeature.java         // Feature class marking a tile as containing a market
  Tile.java                      // Class representing a single grid cell of the world map
  TileFeature.java               // Abstract base class for map tile features
  WorldMap.java                  // Compatibility wrapper around MonstersAndHeroesWorldMap (legacy)
  MonstersAndHeroesWorldMap.java // Implementation of the world map for Monsters and Heroes
  LegendsOfValorWorldMap.java    // Implements the world map for Legends of valor
  ILegendsWorldMap.java          // World map extension used by Legends of Valor actions
  IWorldMap.java                 // General interface for creating world maps for different games

/worldMap/feature
  BushFeature.java               // Handles the bushes on the map, extends TerrainBonusFeature
  CaveFeature.java               // Handles the caves on the map, extends TerrainBonusFeature
  KoulouFeature.java             // Handles the Koulou tiles on the map, extends TerrainBonusFeature
  NexusFeature.java              // Handles the Nexus's on the map
  TerrainBonusFeature.java       // Abstract class that can be implemented by bush/cave/koulo that gives heroes stats boosts
```


## Design Decisions 

* **Strong Object-Oriented Architecture**
  The game is built around clean OOP principles with abstract base classes (`Hero`, `Monster`, `Item`) and well-structured subclasses. This makes the system highly extendable and easy to maintain.

* **Use of Key Design Patterns**

    * **Factory Pattern**: `MonsterFactory` loads template monsters from files and dynamically generates battle-ready monsters scaled to the party level.
    * **Command Pattern**: `MarketCommand` defines executable market actions (buy, sell, view info, leave) and allows clean, modular menu behavior.
    * **Ports & Adapters (Dependency Inversion)**:
      - `TextFileReader` abstracts file reading (backend I/O) so loaders are testable and replaceable.
      - `IMonsterFactory` abstracts monster creation so battles/LoV can be deterministic in tests.

* **SOLID Principles**

    * Each class has a single purpose (Single Responsibility Principle).
    * New monsters, heroes, items or commands can be added without modifying existing code (Open-Closed Principle).
    * Interfaces decouple components, enabling flexible swapping of implementations (Dependency Inversion Principle).

* **MVC Separation (Project-wide)**
  All console I/O lives in View implementations (`ui/*`). Model/UseCase layers never print/read directly.
  - **MH Controller**: `GameImpl`
  - **LoV Controller**: `LegendsOfValorGameImpl`
  - **LoV UseCase**: `LovActionExecutor` (+ request/result objects)
  - **Battle I/O**: `BattleView` + `ConsoleBattleView`
  - **Map rendering**: formatter classes (`LegendsMapFormatter`, `MhMapFormatter`)

* **Scalable & Extensible Design**
    * A GUI or web interface could be built on top of the existing logic.
    * New hero classes, monster types, spells, items or commands can be added simply by adding new files or subclasses.
    * The system supports easy localization, since all user-facing text is centralized in MessageUtils.


* **Enhanced Console UI with Color Output**
  The program uses `ConsoleColors` to create a visually appealing terminal interface.

* **Centralized Game Rules & UI Messages**

    * `GameConstants` holds all game constants.
    * `MessageUtils` centralizes all UI messages for consistency and future localization.

* **Comprehensive JavaDoc Documentation**
The entire codebase includes detailed JavaDoc for classes, interfaces, methods, and enums.
This improves readability, explains design intent and makes the system easier to maintain and extend. 


## Steps to Run

1. Download/clone the repository (make sure `src/` and `files/` are both present).
2. Open terminal/command prompt and navigate to the project root folder (the folder containing `src/`):
```bash
cd /path/to/oop_monsters_and_heroes
```
3. Compile the code:
```bash
rm -rf out
find src -name "*.java" > sources.txt
javac -Xlint:all -d out @sources.txt
```
4. Run the game:
```bash
java -cp out Main
```

## Tests (Pure Java, no JUnit)

Run the automated tests with assertions enabled:

```bash
java -ea -cp out TestRunner
```


## Game Flow
| Screen 1                                       | Screen 2                                       | Screen 3                                       |
|------------------------------------------------|------------------------------------------------|------------------------------------------------|
| <img src="gameFlow/Screen_1.png" width="300"/> | <img src="gameFlow/Screen_2.png" width="300"/> | <img src="gameFlow/Screen_3.png" width="300"/> |

| Screen 4                                       | Screen 5                                       | Screen 6                                       |
|------------------------------------------------|------------------------------------------------|------------------------------------------------|
| <img src="gameFlow/Screen_4.png" width="300"/> | <img src="gameFlow/Screen_5.png" width="300"/> | <img src="gameFlow/Screen_6.png" width="300"/> |

| Screen 7                                       | Screen 8                                       | Screen 9                                       |
|------------------------------------------------|------------------------------------------------|------------------------------------------------|
| <img src="gameFlow/Screen_7.png" width="300"/> | <img src="gameFlow/Screen_8.png" width="300"/> | <img src="gameFlow/Screen_9.png" width="300"/> |
