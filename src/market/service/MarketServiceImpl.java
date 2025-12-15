package market.service;

import hero.Hero;
import market.model.Market;
import market.model.item.Item;
import utils.MessageUtils;

import java.util.List;

import static utils.GameConstants.SELL_PRICE_MULTIPLIER;


/**
 * Implementation of the {@link MarketService} interface
 */
public class MarketServiceImpl implements MarketService {

    private final Market market;

    public MarketServiceImpl(Market market) {
        this.market = market;
    }

    @Override
    public List<Item> getItemsForSale() {
        return market.getItems();
    }

    @Override
    public MarketResult buyItem(Hero hero, Item item) {
        if (!market.getItems().contains(item)) {
            return MarketResult.warning(MessageUtils.ITEM_IS_NOT_AVAILABLE);
        }

        if (hero.getGold() < item.getPrice()) {
            return MarketResult.warning(MessageUtils.NOT_ENOUGH_GOLD);
        }

        if (hero.getLevel() < item.getLevel()) {
            return MarketResult.warning(MessageUtils.NOT_ENOUGH_LEVEL);
        }

        hero.spendGold(item.getPrice());
        hero.addItem(item);
        market.removeItem(item);

        return MarketResult.success(String.format(MessageUtils.SUCCESSFUL_PURCHASE, hero.getName(), item.getName()));
    }

    @Override
    public MarketResult sellItem(Hero hero, Item item) {
        if (!hero.hasItem(item)) {
            return MarketResult.fail(MessageUtils.HERO_DOES_NOT_OWN_ITEM_MESSAGE);
        }

        int sellValue = calculateSellValue(item.getPrice());

        hero.addGold(sellValue);
        hero.removeItem(item);
        market.addItem(item);

        return MarketResult.success(String.format(MessageUtils.SUCCESSFUL_SELL, hero.getName(),  item.getName(), sellValue));
    }

    private int calculateSellValue(int price) {
        return (int) Math.round(price * SELL_PRICE_MULTIPLIER);
    }

}
