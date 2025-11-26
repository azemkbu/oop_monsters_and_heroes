package upload;

import hero.*;
import utils.GameConstants;

import java.util.ArrayList;
import java.util.List;

import static upload.base.GenericFileLoader.load;


public final class HeroFileLoader {

    private HeroFileLoader() {
    }

    public static List<Hero> loadAllHeroes() {
        List<Hero> heroes = new ArrayList<>();
        heroes.addAll(loadWarriors(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Warriors.txt"));
        heroes.addAll(loadSorcerers(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Sorcerers.txt"));
        heroes.addAll(loadPaladins(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Paladins.txt"));
        return heroes;
    }

    public static List<Warrior> loadWarriors(String filePath) {
        return load(filePath, parts -> {
            String name = parts[0];
            int mana = Integer.parseInt(parts[1]);
            int strength = Integer.parseInt(parts[2]);
            int agility = Integer.parseInt(parts[3]);
            int dexterity = Integer.parseInt(parts[4]);
            int money = Integer.parseInt(parts[5]);
            int exp = Integer.parseInt(parts[6]);

            Warrior warrior = new Warrior(name, 1, strength, dexterity, agility, mana, new Wallet(money), exp);
            return warrior;
        });
    }

    public static List<Sorcerer> loadSorcerers(String filePath) {
        return load(filePath, parts -> {
            String name = parts[0];
            int mana = Integer.parseInt(parts[1]);
            int strength = Integer.parseInt(parts[2]);
            int agility = Integer.parseInt(parts[3]);
            int dexterity = Integer.parseInt(parts[4]);
            int money = Integer.parseInt(parts[5]);
            int exp = Integer.parseInt(parts[6]);

            Sorcerer sorcerer = new Sorcerer(name, 1, strength, dexterity, agility, mana, new Wallet(money), exp);
            return sorcerer;
        });
    }

    public static List<Paladin> loadPaladins(String filePath) {
        return load(filePath, parts -> {
            String name = parts[0];
            int mana = Integer.parseInt(parts[1]);
            int strength = Integer.parseInt(parts[2]);
            int agility = Integer.parseInt(parts[3]);
            int dexterity = Integer.parseInt(parts[4]);
            int money = Integer.parseInt(parts[5]);
            int exp = Integer.parseInt(parts[6]);

            Paladin paladin = new Paladin(name, 1, strength, dexterity, agility, mana, new Wallet(money), exp);
            return paladin;
        });
    }
}
