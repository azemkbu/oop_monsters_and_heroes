package hero;

import market.model.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents {@link Hero}'s inventory
 * **/
public final class Inventory {

    private final List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    public void addItem(Item item) {
        if(item == null){
            throw new IllegalArgumentException("Item cannot be null");
        }
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
