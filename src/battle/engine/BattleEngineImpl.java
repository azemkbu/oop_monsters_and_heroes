package battle.engine;

import battle.enums.HeroActionType;
import battle.enums.EquipChoice;
import hero.Hero;
import hero.Party;
import market.model.item.Item;
import market.model.item.Armor;
import market.model.item.ItemType;
import market.model.item.Potion;
import market.model.item.Spell;
import market.model.item.Weapon;
import monster.Monster;
import monster.MonsterFactory;
import ui.battle.BattleView;
import utils.MessageUtils;
import utils.GameConstants;
import worldMap.IWorldMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of {@link BattleEngine} interface
 **/

public class BattleEngineImpl implements BattleEngine {

    private final BattleView view;
    private final MonsterFactory monsterFactory;

    public BattleEngineImpl(BattleView view, MonsterFactory monsterFactory) {
        this.view = view;
        this.monsterFactory = monsterFactory;
    }

    @Override
    public boolean runBattle(Party party, IWorldMap iWorldMap) {
        List<Monster> monsters = monsterFactory.createMonstersForParty(party);
        List<Monster> originalMonsters = new ArrayList<>(monsters);
        List<Hero> heroes = new ArrayList<>(party.getHeroes());
        view.showBattleStatus(heroes, monsters);

        while (true) {
            for (Hero hero : heroes) {
                if (!hero.isAlive()) {
                    continue;
                }

                if (allMonstersDefeated(monsters)) {
                    view.showSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
                    grantBattleRewards(party, originalMonsters);
                    applyPostBattleRecovery(party);
                    return true;
                }

                HeroActionType actionType = view.promptHeroAction(hero, monsters);

                if (actionType == HeroActionType.SKIP) {
                    view.showWarning(String.format(MessageUtils.SKIP_TURN, hero.getName()));
                } else {
                    executeHeroAction(hero, monsters, actionType);
                }

                removeDeadMonsters(monsters);
                view.showBattleStatus(heroes, monsters);

                if (allMonstersDefeated(monsters)) {
                    view.showSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
                    grantBattleRewards(party, originalMonsters);
                    applyPostBattleRecovery(party);
                    return true;
                }
            }

            // --- Monsters' turn ---
            for (Monster monster : monsters) {
                if (!monster.isAlive()) {
                    continue;
                }

                Hero hero = pickRandomAliveHero(heroes);
                if (hero == null) {
                    view.showFail(MessageUtils.MONSTERS_DEFEAT_HEROES);
                    applyPostBattleRecovery(party);
                    return false;
                }

                int damage = monster.computeAttackDamage();
                boolean dodged = hero.dodgesAttack();
                if (dodged) {
                    view.showWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, monster.getName(), hero.getName()));
                } else {
                    hero.takeDamage(damage);
                    view.showWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK,  monster.getName(), hero.getName(), damage));
                }

                if (!hero.isAlive()) {
                    view.showWarning(String.format(MessageUtils.CHARACTER_FAINTED, hero.getName()));
                }
            }

            if (party.allHeroesDefeated()) {
                view.showFail(MessageUtils.MONSTERS_DEFEAT_HEROES);
                applyPostBattleRecovery(party);
                return false;
            }

            for (Hero hero : heroes) {
                if (hero.isAlive()) {
                    hero.recoverAfterRound();
                }
            }

            view.showBattleStatus(heroes, monsters);
        }
    }

    private void executeHeroAction(Hero hero, List<Monster> monsters, HeroActionType actionType) {
        switch (actionType) {
            case ATTACK:
                doAttack(hero, monsters);
                break;
            case CAST_SPELL:
                doCastSpell(hero, monsters);
                break;
            case USE_POTION:
                doUsePotion(hero);
                break;
            case EQUIP:
                doEquip(hero);
                break;
            default:
                view.showFail(MessageUtils.UNKNOWN_COMMAND);
        }
    }

    private void doAttack(Hero hero, List<Monster> monsters) {
        List<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.isAlive()) alive.add(m);
        }
        if (alive.isEmpty()) {
            view.showWarning(MessageUtils.NO_MONSTERS_TO_ATTACK);
            return;
        }

        Monster target = view.promptMonsterTarget(hero, alive);
        if (target == null) {
            view.showWarning(MessageUtils.NO_MONSTER_SELECTED);
            return;
        }

        int weaponDamage = 0;
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null) {
            weaponDamage = weapon.getDamage();
            if (weapon.getHandsRequired() == 1) {
                int hands = view.promptHandsForWeapon(hero, weapon);
                if (hands == 2) {
                    weaponDamage = (int) Math.round(weaponDamage * GameConstants.ONE_HANDED_WEAPON_BONUS_MULTIPLIER);
                }
            }
        }

        int damage = (int) Math.round((hero.getStrength() + weaponDamage) * GameConstants.HERO_ATTACK_MULTIPLIER);

        if (target.dodgesAttack()) {
            view.showWarning(String.format(MessageUtils.ATTACK_WAS_DODGED, hero.getName(), target.getName()));
            return;
        }

        int dealtDamage = target.takeDamage(damage);
        view.showWarning(String.format(MessageUtils.SUCCESSFUL_ATTACK, hero.getName(), target.getName(), dealtDamage));

        if (weapon != null) {
            weapon.consumeUse();
            if (!weapon.isUsable()) {
                view.showFail(String.format(MessageUtils.ITEM_CAN_NO_LONGER_BE_USED, weapon.getName()));
            } else {
                view.showWarning(String.format(MessageUtils.REMAINING_ITEM_USES, weapon.getName(), weapon.getUsesRemaining()));
            }
        }

        if (!target.isAlive()) {
            view.showWarning(String.format(MessageUtils.CHARACTER_DEFEATED, target.getName()));
        }
    }

    private void doCastSpell(Hero hero, List<Monster> monsters) {
        List<Spell> spells = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Spell) {
                spells.add((Spell) item);
            }
        }
        if (spells.isEmpty()) {
            view.showWarning(MessageUtils.NO_SPELLS);
            return;
        }

        Spell spell = view.promptSpellToCast(hero, spells);
        if (spell == null) {
            view.showWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.SPELL));
            return;
        }

        if (hero.getMp() < spell.getManaCost()) {
            view.showWarning(String.format(MessageUtils.NOT_ENOUGH_MP_TO_CAST_SPELL, hero.getName(), spell.getName()));
            return;
        }

        List<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.isAlive()) alive.add(m);
        }
        if (alive.isEmpty()) {
            view.showWarning(MessageUtils.NO_MONSTERS_TO_CAST_SPELL);
            return;
        }

        Monster target = view.promptMonsterTarget(hero, alive);
        if (target == null) {
            view.showWarning(MessageUtils.NO_MONSTER_SELECTED);
            return;
        }

        hero.setMp(hero.getMp() - spell.getManaCost());
        hero.getInventory().remove(spell);

        int finalDamage = calculateSpellDamage(hero, target, spell);

        double dodgeProb = target.getDodgeChance() * GameConstants.MONSTER_DODGE_MULTIPLIER;
        double roll = ThreadLocalRandom.current().nextDouble();
        if (roll < dodgeProb) {
            view.showWarning(String.format(MessageUtils.MONSTER_DODGED_SPELL, target.getName()));
            return;
        }

        target.setHp(Math.max(0, target.getHp() - finalDamage));
        applySpellDebuff(target, spell);

        view.showSuccess(String.format(
                MessageUtils.SUCCESSFUL_SPELL_CAST,
                hero.getName(),
                spell.getName(),
                target.getName(),
                finalDamage
        ));

        if (target.getHp() == 0) {
            view.showSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
        }
    }

    private int calculateSpellDamage(Hero hero, Monster monster, Spell spell) {
        double baseDamage = spell.getDamage();
        double dexterity = hero.getDexterity();
        double spellDamage = baseDamage
                + (dexterity / GameConstants.HERO_SPELL_DEX_DIVISOR) * baseDamage;
        double effectiveDamage = spellDamage - monster.getDefense();
        return (int) Math.max(0, Math.round(effectiveDamage));
    }

    private void applySpellDebuff(Monster monster, Spell spell) {
        double remainingFactor = 1.0 - GameConstants.MONSTER_SKILL_LOSS_MULTIPLIER;
        switch (spell.getType()) {
            case ICE: {
                double damage = monster.getBaseDamage();
                monster.setBaseDamage(damage * remainingFactor);
                break;
            }
            case FIRE: {
                double defense = monster.getDefense();
                monster.setDefense((int) (defense * remainingFactor));
                break;
            }
            case LIGHTNING: {
                int dodge = (int) Math.round(monster.getDodgeAbility() * remainingFactor);
                monster.setDodgeAbility(dodge);
                break;
            }
            default:
                break;
        }
    }

    private void doUsePotion(Hero hero) {
        List<Potion> potions = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Potion) {
                potions.add((Potion) item);
            }
        }
        if (potions.isEmpty()) {
            view.showWarning(String.format(MessageUtils.NO_ITEM_TO_USE, ItemType.POTION.name()));
            return;
        }

        Potion potion = view.promptPotionToUse(hero, potions);
        if (potion == null) {
            view.showWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.POTION));
            return;
        }

        double amount = potion.getEffectAmount();
        switch (potion.getStatType()) {
            case HP:
                hero.setHp((int) (hero.getHp() + amount));
                break;
            case MP:
                hero.setMp((int) (hero.getMp() + amount));
                break;
            case STRENGTH:
                hero.setStrength((int) (hero.getStrength() + amount));
                break;
            case DEXTERITY:
                hero.setDexterity((int) (hero.getDexterity() + amount));
                break;
            case AGILITY:
                hero.setAgility((int) (hero.getAgility() + amount));
                break;
            default:
                break;
        }
        hero.getInventory().remove(potion);
        view.showSuccess(String.format(
                MessageUtils.SUCCESSFUL_POTION_USE_MESSAGE,
                hero.getName(),
                potion.getName(),
                potion.getStatType().name(),
                (int) amount
        ));
    }

    private void doEquip(Hero hero) {
        EquipChoice choice = view.promptEquipChoice(hero);
        switch (choice) {
            case WEAPON: {
                List<Weapon> weapons = new ArrayList<>();
                for (Item item : hero.getInventory()) {
                    if (item instanceof Weapon) weapons.add((Weapon) item);
                }
                if (weapons.isEmpty()) {
                    view.showWarning(String.format(MessageUtils.NO_ITEM_TO_EQUIP, ItemType.WEAPON.name()));
                    return;
                }
                Weapon chosen = view.promptWeaponToEquip(hero, weapons);
                if (chosen == null) {
                    view.showWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.WEAPON.name()));
                    return;
                }
                hero.equipWeapon(chosen);
                view.showWarning(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), chosen.getName()));
                return;
            }
            case ARMOR: {
                List<Armor> armors = new ArrayList<>();
                for (Item item : hero.getInventory()) {
                    if (item instanceof Armor) armors.add((Armor) item);
                }
                if (armors.isEmpty()) {
                    view.showWarning(String.format(MessageUtils.NO_ITEM_TO_EQUIP, ItemType.ARMOR.name()));
                    return;
                }
                Armor chosen = view.promptArmorToEquip(hero, armors);
                if (chosen == null) {
                    view.showWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.ARMOR.name()));
                    return;
                }
                hero.equipArmor(chosen);
                view.showWarning(String.format(MessageUtils.SUCCESSFUL_EQUIP, hero.getName(), chosen.getName()));
                return;
            }
            case CANCEL:
            default:
                view.showFail(MessageUtils.CANCELED);
        }
    }

    private boolean allMonstersDefeated(List<Monster> monsters) {
        for (Monster m : monsters) {
            if (m.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private void removeDeadMonsters(List<Monster> monsters) {
        monsters.removeIf(m -> !m.isAlive());
    }

    private Hero pickRandomAliveHero(List<Hero> heroes) {
        List<Hero> alive = new ArrayList<>();
        for (Hero h : heroes) {
            if (h.isAlive()) {
                alive.add(h);
            }
        }
        if (alive.isEmpty()) {
            return null;
        }
        int idx = (int) (Math.random() * alive.size());
        return alive.get(idx);
    }

    private void applyPostBattleRecovery(Party party) {
        for (Hero hero : party.getHeroes()) {
            if (hero.isAlive()) {
                continue;
            }
            int maxHp = hero.getLevel() * GameConstants.HERO_HP_PER_LEVEL;
            int revivedHp = maxHp / 2;
            if (revivedHp < 1) {
                revivedHp = 1;
            }
            hero.setHp(revivedHp);
        }
    }

    private void grantBattleRewards(Party party, List<Monster> originalMonsters) {
        int numberOfMonsters = originalMonsters.size();

        int highestMonsterLevel = 0;
        for (Monster m : originalMonsters) {
            if (m.getLevel() > highestMonsterLevel) {
                highestMonsterLevel = m.getLevel();
            }
        }

        for (Hero hero : party.getHeroes()) {
            boolean heroFainted = !hero.isAlive();
            hero.rewardFromBattle(highestMonsterLevel, numberOfMonsters, heroFainted);
        }
    }
}
