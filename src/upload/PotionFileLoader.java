package upload;

import market.model.item.Potion;
import market.model.item.StatType;
import upload.base.DefaultTextFileReader;

import java.util.List;

import static upload.base.GenericFileLoader.load;

public final class PotionFileLoader {

    private PotionFileLoader() {
    }

    public static List<Potion> loadPotions(String filePath) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int price = Integer.parseInt(parts[1]);
            int requiredLevel = Integer.parseInt(parts[2]);
            int effectAmount = Integer.parseInt(parts[3]);
            String attr = parts[4].toUpperCase();

            StatType type = mapPotionType(attr);
            return new Potion(name, price, requiredLevel, effectAmount, type);
        });
    }

    private static StatType mapPotionType(String attr) {
        if (attr == null) {
            throw new IllegalArgumentException("Potion type cannot be null");
        }

        String key = attr.toUpperCase();

        switch (key) {
            case "HEALTH":
            case "HP":
                return StatType.HP;

            case "MANA":
            case "MP":
                return StatType.MP;

            case "STRENGTH":
                return StatType.STRENGTH;

            case "DEXTERITY":
                return StatType.DEXTERITY;

            case "AGILITY":
                return StatType.AGILITY;

            default:
                throw new IllegalArgumentException("Unknown potion type: " + attr);
        }
    }

}
