package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.menu.BattleMenu;
import battle.heroAction.HeroActionStrategy;
import hero.Hero;
import market.model.item.Item;
import market.model.item.ItemType;
import market.model.item.Spell;
import monster.Monster;
import utils.MessageUtils;
import utils.GameConstants;
import utils.IOUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


/**
 * Implementation of the cast spell during {@link Hero} turn
 */
public class CastSpellAction implements HeroActionStrategy {

    @Override
    public void execute(Hero hero,
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
            return;
        }

        Spell chosen = menu.chooseSpellToCast(hero, spells);

        if (chosen == null) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.SPELL));
            return;
        }

        if (hero.getMp() < chosen.getManaCost()) {
            ioUtils.printlnWarning(String.format(MessageUtils.NOT_ENOUGH_MP_TO_CAST_SPELL, hero.getName(), chosen.getName()));
            return;
        }

        List<Monster> aliveMonsters = new ArrayList<>();
        for (Monster m : monsters) {
            if (m.getHp() > 0) {
                aliveMonsters.add(m);
            }
        }

        if (aliveMonsters.isEmpty()) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTERS_TO_CAST_SPELL);
            return;
        }

        Monster monster = menu.chooseMonsterTarget(hero, aliveMonsters);
        if (monster == null) {
            ioUtils.printlnWarning(MessageUtils.NO_MONSTER_SELECTED);
            return;
        }

        hero.setMp(hero.getMp() - chosen.getManaCost());

        double baseDamage = chosen.getDamage();
        double dexterity = hero.getDexterity();
        double spellDamage = baseDamage + (dexterity / GameConstants.HERO_SPELL_DEX_DIVISOR) * baseDamage;

        double effectiveDamage = spellDamage - monster.getDefense();
        int finalDamage = (int) Math.max(0, Math.round(effectiveDamage));

        double dodgeStat = monster.getDodgeChance();
        double dodgeProb = dodgeStat * GameConstants.MONSTER_DODGE_MULTIPLIER;

        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < dodgeProb) {
            ioUtils.printlnTitle(String.format(MessageUtils.MONSTER_DODGED_SPELL, monster.getName()));
            return;
        }

        monster.setHp(Math.max(0, monster.getHp() - finalDamage));

        double remainingFactor = 1.0 - GameConstants.MONSTER_SKILL_LOSS_MULTIPLIER;

        switch (chosen.getType()) {
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

        hero.getInventory().remove(chosen);

        if (monster.getHp() == 0) {
            ioUtils.printlnSuccess(String.format(MessageUtils.SUCCESSFUL_SPELL_CAST,
                    hero.getName(), chosen.getName(), monster.getName(), finalDamage));
            ioUtils.printlnSuccess(MessageUtils.HEROES_DEFEAT_MONSTERS);
        } else {
            ioUtils.printlnSuccess(String.format(MessageUtils.SUCCESSFUL_SPELL_CAST,
                    hero.getName(), chosen.getName(), monster.getName(), finalDamage));
        }
    }
}
