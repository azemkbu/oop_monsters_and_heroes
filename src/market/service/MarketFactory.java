package market.service;

import market.model.Market;
import market.model.item.Item;
import utils.GameConstants;
import upload.ArmorFileLoader;
import upload.PotionFileLoader;
import upload.SpellFileLoader;
import upload.WeaponFileLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Factory class for creating unique Market instances by selecting random subsets from the item pool
 */
public class MarketFactory {

    private final List<Item> items;
    private final Random random = new Random();

    public MarketFactory() {
        List<Item> allItems = new ArrayList<>();

        allItems.addAll(WeaponFileLoader.loadWeapons(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Weaponry.txt"));
        allItems.addAll(ArmorFileLoader.loadArmors(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Armory.txt"));
        allItems.addAll(SpellFileLoader.loadAllSpells(GameConstants.BASE_DIR_TO_UPLOAD_FILES));
        allItems.addAll(PotionFileLoader.loadPotions(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Potions.txt"));

        this.items = Collections.unmodifiableList(allItems);

        if (items.isEmpty()) {
            throw new IllegalStateException("No market items loaded from directory: " + GameConstants.BASE_DIR_TO_UPLOAD_FILES);
        }
    }


    /**
     * Create a new Market instance with a random subset of items
     */
    public Market createRandomMarket() {
        Market market = new Market();

        List<Item> shuffled = new ArrayList<Item>(items);
        Collections.shuffle(shuffled, random);

        int numItems = 4 + random.nextInt(5);
        numItems = Math.min(numItems, shuffled.size());

        for (int i = 0; i < numItems; i++) {
            Item template = shuffled.get(i);
            market.addItem(template);
        }

        return market;
    }
}
