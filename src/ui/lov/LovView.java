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

    HeroActionType promptHeroAction(Hero hero, List<Monster> monsters);

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
     * Market entry prompt + handling belongs to View.
     * @return true if the user requested to quit the game, false otherwise
     */
    boolean maybeEnterMarket(Hero hero, Market market);

    void showSuccess(String message);

    void showFail(String message);

    void showWarning(String message);
}


