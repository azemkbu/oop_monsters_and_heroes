package market.model;

import market.model.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a market that stores list of  {@link Item}s for sale
 */
public class Market {

    private final List<Item> itemsForSale = new ArrayList<>();

    public void addItem(Item item) {
        itemsForSale.add(item);
    }

    public void removeItem(Item item) {
        itemsForSale.remove(item);
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(itemsForSale);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Market Items:\n");
        for (Item item : itemsForSale) {
            sb.append(" - ").append(item).append("\n");
        }
        return sb.toString();
    }
}
