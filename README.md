# CS611-Assignment <Legends: Monsters and Heroes>
-------------------------------------------------------------
--------------
- Name: Azem Kakitaeva
- Email: azemk@bu.edu
- Student ID: U51216906


## File Structure 

```text
/battle/engine
  BattleEngine.java              // Interface defining the contract for executing battles
  BattleEngineImpl.java          // Implementation of the battle engine handling battle flow

/battle/enums
  EquipChoice.java               // Enum representing possible equipment choices
  HeroActionType.java            // Enum representing available hero actions during battle

/battle/heroAction
  HeroActionStrategy.java        // Strategy interface representing a hero’s chosen action logic
  BattleActionsConfig.java       // Configuration class mapping action types to strategies
  BattleContext.java             // Context class containing shared state for ongoing battles

/battle/heroAction/impl
  AttackAction.java              // Strategy implementing an attack action
  CastSpellAction.java           // Strategy implementing spell-casting behavior for heroes
  EquipAction.java               // Strategy allowing heroes to equip weapons or armor in battle
  UsePotionAction.java           // Strategy allowing heroes to consume potions during battle

/battle/menu
  BattleMenu.java                // Interface defining battle-related UI display operations
  BattleMenuImpl.java            // Implementation of BattleMenu interface

/game
  Game.java                      // Interface defining main game methods
  GameCommand.java               // Enum mapping keyboard commands to game actions
  GameImpl.java                  // Implementation of core game logic
  GameLauncher.java              // Entry class responsible for loading resources and starting the game

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
  Monster.java                   // Abstract base class defining core monster attributes and behavior
  MonsterFactory.java            // Factory class generating monsters for battles
  Spirit.java                    // Subclass of Monster representing a Spirit

/upload/base
  GenericFileLoader.java         // Generic loader for building objects from uploaded text files
  LineMapper.java                // Functional mapper converting a text line into an object
  TextFileUtils.java             // Utility class for reading and parsing text files

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

/worldMap/enums
  Direction.java                 // Enum representing movement directions on the world map
  TileType.java                  // Enum defining categories of map tiles

/worldMap
  MarketTileFeature.java         // Feature class marking a tile as containing a market
  Tile.java                      // Class representing a single grid cell of the world map
  TileFeature.java               // Abstract base class for map tile features
  WorldMap.java                  // Class responsible for generating and managing the game map
```


## Design Decisions 

* **Strong Object-Oriented Architecture**
  The game is built around clean OOP principles with abstract base classes (`Hero`, `Monster`, `Item`) and well-structured subclasses. This makes the system highly extendable and easy to maintain.

* **Use of Key Design Patterns**

    * **Factory Pattern**: `MonsterFactory` loads template monsters from files and dynamically generates battle-ready monsters scaled to the party level.
    * **Strategy Pattern**: `HeroActionStrategy` encapsulates hero actions (attack, spell, potion, equip, skip) into separate strategy objects, making new actions easy to add.
    * **Command Pattern**: `MarketCommand` defines executable market actions (buy, sell, view info, leave) and allows clean, modular menu behavior.

* **SOLID Principles**

    * Each class has a single purpose (Single Responsibility Principle).
    * New monsters, heroes, items or commands can be added without modifying existing code (Open-Closed Principle).
    * Interfaces decouple components, enabling flexible swapping of implementations (Dependency Inversion Principle).

* **Clear Separation of Concerns**

    * **Model Layer**: Hero, Monster, Item hierarchy, Market, WorldMap, Party.
    * **Logic Layer**: BattleEngine, HeroActionStrategy, MonsterFactory, MarketService, MarketCommand.
    * **Infrastructure Layer**: File loaders (`upload.*`), RuleConstants, ConsoleColors, IOUtils, centralized MessageUtils.
      This structure makes the codebase easy to understand, test and extend.

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

1. Download the `src` folder.
2. Open terminal/command prompt and navigate to the folder:
```bash
cd ~/IdeaProjects/monstersAndHeroes
```
3. Compile the code:
```bash
javac -d out $(find src -name "*.java")
```
4. Run the game:
```bash
java -cp out Main
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
