package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import battle.menu.BattleMenu;
import hero.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import market.model.item.Item;
import market.model.item.ItemType;
import market.model.item.Spell;
import monster.Monster;
import utils.GameConstants;
import utils.IOUtils;
import utils.MessageUtils;

/**
 * Base implementation of casting a spell
 */
public abstract class BaseCastSpellAction implements HeroActionStrategy {

    @Override
    public boolean execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ioUtils) {

        BattleMenu menu = context.getBattleMenu();

        List<Spell> spells = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Spell) {
                spells.add((Spell) item);
            }
        }

        if (spells.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_SPELLS);
            return false;
        }

        Spell chosen = menu.chooseSpellToCast(hero, spells);

        if (chosen == null) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.SPELL));
            return false;
        }

        if (hero.getMp() < chosen.getManaCost()) {
            ioUtils.printlnWarning(String.format(MessageUtils.NOT_ENOUGH_MP_TO_CAST_SPELL, hero.getName(), chosen.getName()));
            return false;
        }

        List<Monster> availableMonsters = getAvailableMonsters(hero, monsters, context);

        if (availableMonsters == null || availableMonsters.isEmpty()) {
            handleNoAvailableMonsters(hero, monsters, context, ioUtils);
            return false;
        }

        Monster monster = menu.chooseMonsterTarget(hero, availableMonsters);

        if (monster == null) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTER_SELECTED);
            return false;
        }

        hero.setMp(hero.getMp() - chosen.getManaCost());

        applySpell(hero, monster, chosen, ioUtils);
        hero.getInventory().remove(chosen);

        if (monster.getHp() == 0) {
            ioUtils.printlnSuccess(String.format(
                    MessageUtils.SUCCESSFUL_SPELL_CAST,
                    hero.getName(),
                    chosen.getName(),
                    monster.getName(),
                    calculateFinalDamage(hero, monster, chosen)
            ));
            ioUtils.printlnSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
        } else {
            ioUtils.printlnSuccess(String.format(
                    MessageUtils.SUCCESSFUL_SPELL_CAST,
                    hero.getName(),
                    chosen.getName(),
                    monster.getName(),
                    calculateFinalDamage(hero, monster, chosen)
            ));
        }
        return true;
    }

    /**
     * Default: all alive monsters are valid targets.
     * LoV will override this to apply range restrictions.
     */
    protected List<Monster> getAvailableMonsters(Hero hero,
                                                 List<Monster> monsters,
                                                 BattleContext context) {
        List<Monster> alive = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.getHp() > 0) {
                alive.add(m);
            }
        }
        return alive;
    }

    /**
     * Default: if no valid targets, say there are no monsters to cast on.
     * LoV will override to say "no enemies in range" if monsters exist but are out of range.
     */
    protected void handleNoAvailableMonsters(Hero hero,
                                             List<Monster> monsters,
                                             BattleContext context,
                                             IOUtils ioUtils) {
        ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_CAST_SPELL);
    }



    protected int calculateFinalDamage(Hero hero, Monster monster, Spell spell) {
        double baseDamage = spell.getDamage();
        double dexterity = hero.getDexterity();
        double spellDamage = baseDamage
                + (dexterity / GameConstants.HERO_SPELL_DEX_DIVISOR) * baseDamage;

        double effectiveDamage = spellDamage - monster.getDefense();
        return (int) Math.max(0, Math.round(effectiveDamage));
    }


    protected void applySpell(Hero hero,
                              Monster monster,
                              Spell spell,
                              IOUtils ioUtils) {

        int finalDamage = calculateFinalDamage(hero, monster, spell);

        double dodgeStat = monster.getDodgeChance();
        double dodgeProb = dodgeStat * GameConstants.MONSTER_DODGE_MULTIPLIER;
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < dodgeProb) {
            ioUtils.printlnTitle(String.format(MessageUtils.MONSTER_DODGED_SPELL, monster.getName()));
            return;
        }

        monster.setHp(Math.max(0, monster.getHp() - finalDamage));

        double remainingFactor = 1.0 - GameConstants.MONSTER_SKILL_LOSS_MULTIPLIER;

        switch (spell.getType()) {
            case ICE: {
                double damage = monster.getBaseDamage();
                monster.setBaseDamage(damage * remainingFactor);
            }
            case FIRE: {
                double defense = monster.getDefense();
                monster.setDefense((int) (defense * remainingFactor));
            }
            case LIGHTNING: {
                double dodge = monster.getDodgeChance();
                monster.setDodgeAbility((int) (dodge * remainingFactor));
            }
        }
    }
}
