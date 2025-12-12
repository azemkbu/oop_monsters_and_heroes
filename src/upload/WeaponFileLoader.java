package upload;

import java.util.List;
import market.model.item.Weapon;
import static upload.base.GenericFileLoader.load;



/*
*
*  Handles loading weapons from .txt
*
*
*/

public final class WeaponFileLoader {

    private WeaponFileLoader() {
    }

    public static List<Weapon> loadWeapons(String filePath) {
        return load(filePath, parts -> {
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int level = Integer.parseInt(parts[2]);
            int damage = Integer.parseInt(parts[3]);
            int handsRequired = Integer.parseInt(parts[4]);
            return new Weapon(name, price, level, damage, handsRequired, 0);
        });
    }
}