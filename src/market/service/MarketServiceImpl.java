package market.service;

import hero.Hero;
import market.model.Market;
import market.model.item.Item;
import utils.IOUtils;
import utils.MessageUtils;

import java.util.List;

import static utils.GameConstants.SELL_PRICE_MULTIPLIER;


/**
 * Implementation of the {@link MarketService} interface
 */
public class MarketServiceImpl implements MarketService {

    private final Market market;
    private final IOUtils ioUtils;

    public MarketServiceImpl(Market market, IOUtils ioUtils) {
        this.market = market;
        this.ioUtils = ioUtils;
    }

    @Override
    public List<Item> getItemsForSale() {
        return market.getItems();
    }

    @Override
    public boolean buyItem(Hero hero, Item item) {
        if (!market.getItems().contains(item)) {
            ioUtils.printlnWarning(MessageUtils.ITEM_IS_NOT_AVAILABLE);
            return false;
        }

        if (hero.getGold() < item.getPrice()) {
            ioUtils.printlnWarning(MessageUtils.NOT_ENOUGH_GOLD);
            return false;
        }

        if (hero.getLevel() < item.getLevel()) {
            ioUtils.printlnWarning(MessageUtils.NOT_ENOUGH_LEVEL);
            return false;
        }

        hero.spendGold(item.getPrice());
        hero.addItem(item);
        market.removeItem(item);

        ioUtils.printlnSuccess(String.format(MessageUtils.SUCCESSFUL_PURCHASE, hero.getName(), item.getName()));
        return true;
    }

    @Override
    public boolean sellItem(Hero hero, Item item) {
        if (!hero.hasItem(item)) {
            ioUtils.printlnFail(MessageUtils.HERO_DOES_NOT_OWN_ITEM_MESSAGE);
            return false;
        }

        int sellValue = calculateSellValue(item.getPrice());

        hero.addGold(sellValue);
        hero.removeItem(item);
        market.addItem(item);

        ioUtils.printlnSuccess(String.format(MessageUtils.SUCCESSFUL_SELL, hero.getName(),  item.getName(), sellValue));
        return true;
    }

    private int calculateSellValue(int price) {
        return (int) Math.round(price * SELL_PRICE_MULTIPLIER);
    }

}
