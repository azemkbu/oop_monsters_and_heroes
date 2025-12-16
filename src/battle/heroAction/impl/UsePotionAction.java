package battle.heroAction.impl;

import battle.heroAction.BattleContext;
import battle.heroAction.HeroActionStrategy;
import battle.menu.BattleMenu;
import hero.Hero;
import java.util.ArrayList;
import java.util.List;
import market.model.item.Item;
import market.model.item.ItemType;
import market.model.item.Potion;
import monster.Monster;
import utils.IOUtils;
import utils.MessageUtils;


/**
 * Implementation of the use potion action during {@link Hero} turn
 */
public class UsePotionAction implements HeroActionStrategy {

    @Override
    public boolean execute(Hero hero,
                        List<Monster> monsters,
                        BattleContext context,
                        IOUtils ioUtils) {

        BattleMenu menu = context.getBattleMenu();

        List<Potion> potions = new ArrayList<>();
        for (Item item : hero.getInventory()) {
            if (item instanceof Potion) {
                potions.add((Potion) item);
            }
        }

        if (potions.isEmpty()) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_TO_USE, ItemType.POTION.name()));
            return false;
        }

        Potion chosen = menu.choosePotionToUse(hero, potions);

        if (chosen == null) {
            ioUtils.printlnWarning(String.format(MessageUtils.NO_ITEM_SELECTED, ItemType.POTION));
            return false;
        }

        double amount = chosen.getEffectAmount();

        switch (chosen.getStatType()) {
            case HP: {
                hero.setHp((int) (hero.getHp() + amount));
            }
            case MP: {
                hero.setMp((int) (hero.getMp() + amount));
            }
            case STRENGTH: {
                hero.setStrength((int) (hero.getStrength() + amount));
            }
            case DEXTERITY: {
                hero.setDexterity((int) (hero.getDexterity() + amount));
            }
            case AGILITY: {
                hero.setAgility((int) (hero.getAgility() + amount));
            }
        }
        ioUtils.printlnSuccess(String.format(
                MessageUtils.SUCCESSFUL_POTION_USE_MESSAGE,
                hero.getName(),
                chosen.getName(),
                chosen.getStatType().name(),
                (int) amount
        ));
        hero.getInventory().remove(chosen);
        return true;
    }
}
