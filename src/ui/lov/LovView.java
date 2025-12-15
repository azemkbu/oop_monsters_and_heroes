package ui.lov;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import market.model.Market;
import market.model.item.Armor;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import monster.Monster;
import worldMap.enums.Direction;

import java.util.List;

/**
 * View layer for Legends of Valor (all input/output belongs here).
 */
public interface LovView {

    void showStarting();

    void showRoundHeader(int round);

    /**
     * Clears the screen and redraws the map + status panels.
     * Used for "refresh" mode UI instead of endless scrolling.
     */
    void refreshDisplay(int round, List<Hero> heroes, List<Monster> monsters);

    void renderMap();

    /**
     * @return true if the user wants to continue, false if they want to quit
     */
    boolean promptContinueOrQuit();

    void showHeroesAndMonstersStatus(List<Hero> heroes, List<Monster> monsters);

    /**
     * Prompts for hero action using letter commands (W/A/S/D for move, K for attack, etc.)
     * Returns the action type and direction (for movement) in one step.
     * 
     * @param hero The current hero
     * @param monsters Alive monsters on the map
     * @param isOnNexus Whether the hero is currently on a Nexus (enables Market option)
     * @return The chosen action type, or null if user wants to quit
     */
    HeroActionType promptHeroAction(Hero hero, List<Monster> monsters, boolean isOnNexus);

    /**
     * Gets the direction from the last WASD input (used after promptHeroAction returns MOVE).
     * @return The direction from the last movement command
     */
    Direction getLastMoveDirection();

    Direction promptDirection(String prompt, boolean allowCancel);

    Monster promptMonsterTarget(Hero hero, List<Monster> monsters);

    Hero promptTeleportTarget(Hero hero, List<Hero> candidates);

    Spell promptSpellToCast(Hero hero, List<Spell> spells);

    Potion promptPotionToUse(Hero hero, List<Potion> potions);

    EquipChoice promptEquipChoice(Hero hero);

    Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons);

    Armor promptArmorToEquip(Hero hero, List<Armor> armors);

    int promptHandsForWeapon(Hero hero, Weapon weapon);

    /**
     * Runs the market session for a hero (called when user inputs 'M').
     * No confirmation prompt - user already chose to enter market.
     */
    void runMarketSession(Hero hero, Market market);

    void showSuccess(String message);

    void showFail(String message);

    void showWarning(String message);

    /**
     * Pauses for user to read messages (press any key to continue).
     */
    void waitForUserAcknowledge();
}


