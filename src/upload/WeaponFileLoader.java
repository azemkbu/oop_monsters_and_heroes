package upload;

import java.util.List;
import market.model.item.Weapon;
import upload.base.DefaultTextFileReader;
import static upload.base.GenericFileLoader.load;


/**
 * Handles loading weapons from .txt files.
 * Supports optional range bonus field (6th column).
 */
public final class WeaponFileLoader {

    private WeaponFileLoader() {
    }

    public static List<Weapon> loadWeapons(String filePath) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int damage = Integer.parseInt(parts[3]);
            int handsRequired = Integer.parseInt(parts[4]);
            
            // Optional 6th field: range bonus (default 0 if not present)
            int rangeBonus = 0;
            if (parts.length > 5) {
                rangeBonus = Integer.parseInt(parts[5]);
            }
            
            return new Weapon(name, price, level, damage, handsRequired, 0, rangeBonus);
        });
    }
}
