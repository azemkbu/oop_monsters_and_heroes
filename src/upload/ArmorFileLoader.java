package upload;

import market.model.item.Armor;

import java.util.List;

import static upload.base.GenericFileLoader.load;


public final class ArmorFileLoader {

    private ArmorFileLoader() {
    }

    public static List<Armor> loadArmors(String filePath) {
        return load(filePath, parts -> {
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int requiredLevel = Integer.parseInt(parts[2]);
            int damageReduction = Integer.parseInt(parts[3]);
            return new Armor(name, price, requiredLevel, damageReduction, 0);
        });
    }
}
