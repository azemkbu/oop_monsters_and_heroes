package market.service;

import hero.Hero;
import market.model.item.Item;

import java.util.List;

/**
 * Business logic for trading items between a hero and a market
 */
public interface MarketService {

    /**
     * @return all items currently available for sale in this market
     */
    List<Item> getItemsForSale();

    /**
     * Buy the given item for the hero
     *
     * @param hero hero who wants to buy the item
     * @param item item to buy
     * @return {@code true} if the purchase was successful, {@code false} otherwise
     */
    boolean buyItem(Hero hero, Item item);

    /**
     * Sell the given item from the hero's inventory to the market
     *
     * @param hero hero who wants to sell the item
     * @param item item to sell
     * @return {@code true} if the sale was successful, {@code false} otherwise
     */
    boolean sellItem(Hero hero, Item item);
}
