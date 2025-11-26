package battle.menu;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import market.model.item.Potion;
import market.model.item.Spell;
import monster.Monster;
import market.model.item.Weapon;
import market.model.item.Armor;

import java.util.List;

/**
 * Defines all user interactions related to battles
 */
public interface BattleMenu {

    /**
     * Asks the player which action the given hero should perform this turn.
     *
     * @param hero     the active hero
     * @param monsters the list of current enemy monsters
     * @return the chosen hero action type
     */
    HeroActionType chooseActionForHero(Hero hero, List<Monster> monsters);

    /**
     * Lets the player choose which monster the hero will target.
     *
     * @param hero     the attacking hero
     * @param monsters the list of available monster targets
     * @return the chosen monster, or {@code null} if the player cancels
     */
    Monster chooseMonsterTarget(Hero hero, List<Monster> monsters);

    /**
     * Asks the player whether they want to equip a weapon or armor.
     *
     * @param hero the hero who is changing equipment
     * @return the type of equipment to change (weapon or armor)
     */
    EquipChoice chooseEquipAction(Hero hero);

    /**
     * Lets the player choose which weapon to equip on the given hero.
     *
     * @param hero    the hero equipping a weapon
     * @param weapons the list of available weapons
     * @return the chosen weapon, or {@code null} if the player cancels
     */
    Weapon chooseWeaponToEquip(Hero hero, List<Weapon> weapons);

    /**
     * Lets the player choose which armor to equip on the given hero.
     *
     * @param hero   the hero equipping armor
     * @param armors the list of available armor items
     * @return the chosen armor, or {@code null} if the player cancels
     */
    Armor chooseArmorToEquip(Hero hero, List<Armor> armors);

    /**
     * Lets the player choose which spell the hero will cast.
     *
     * @param hero   the casting hero
     * @param spells the list of available spells
     * @return the chosen spell, or {@code null} if the player cancels
     */
    Spell chooseSpellToCast(Hero hero, List<Spell> spells);

    /**
     * Lets the player choose which potion the hero will use.
     *
     * @param hero    the hero using a potion
     * @param potions the list of available potions
     * @return the chosen potion, or {@code null} if the player cancels
     */
    Potion choosePotionToUse(Hero hero, List<Potion> potions);

    /**
     * Displays the current state of the battle: heroes and monsters with
     * their HP and other relevant stats.
     *
     * @param heroes   the heroes in the party
     * @param monsters the enemy monsters
     */
    void showBattleStatus(List<Hero> heroes, List<Monster> monsters);

    /**
     * Asks the player how they want to wield a one-handed weapon
     * (with one hand or with both hands).
     *
     * @param hero   the hero using the weapon
     * @param weapon the weapon being wielded
     * @return the number of hands chosen (typically 1 or 2)
     */
    int chooseHandsForWeapon(Hero hero, Weapon weapon);
}
