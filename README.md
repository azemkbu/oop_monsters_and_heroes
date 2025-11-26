# CS611-Assignment <Legends: Monsters and Heroes>
-------------------------------------------------------------
--------------
- Name: Azem Kakitaeva
- Email: azemk@bu.edu
- Student ID: U51216906


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
