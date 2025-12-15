package ui.battle;

import battle.enums.EquipChoice;
import battle.enums.HeroActionType;
import hero.Hero;
import market.model.item.Armor;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import monster.Monster;

import java.util.List;

/**
 * View interface for battle interactions (all console I/O belongs here).
 */
public interface BattleView {
    void showBattleStatus(List<Hero> heroes, List<Monster> monsters);

    HeroActionType promptHeroAction(Hero hero, List<Monster> monsters);

    Monster promptMonsterTarget(Hero hero, List<Monster> monsters);

    Spell promptSpellToCast(Hero hero, List<Spell> spells);

    Potion promptPotionToUse(Hero hero, List<Potion> potions);

    EquipChoice promptEquipChoice(Hero hero);

    Weapon promptWeaponToEquip(Hero hero, List<Weapon> weapons);

    Armor promptArmorToEquip(Hero hero, List<Armor> armors);

    int promptHandsForWeapon(Hero hero, Weapon weapon);

    void showSuccess(String msg);

    void showWarning(String msg);

    void showFail(String msg);
}


