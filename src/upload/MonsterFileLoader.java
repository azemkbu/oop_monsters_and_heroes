package upload;

import monster.Dragon;
import monster.Exoskeleton;
import monster.Monster;
import monster.Spirit;
import utils.GameConstants;
import upload.base.DefaultTextFileReader;

import java.util.ArrayList;
import java.util.List;

import static upload.base.GenericFileLoader.load;


public final class MonsterFileLoader {

    private MonsterFileLoader() {
    }

    public static List<Monster> loadAllMonsters() {
        List<Monster> monsters = new ArrayList<>();
        monsters.addAll(loadDragons(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Dragons.txt"));
        monsters.addAll(loadExoskeletons(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Exoskeletons.txt"));
        monsters.addAll(loadSpirits(GameConstants.BASE_DIR_TO_UPLOAD_FILES + "/Spirits.txt"));
        return monsters;
    }

    public static List<Dragon> loadDragons(String filePath) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int level = Integer.parseInt(parts[1]);
            int damage = Integer.parseInt(parts[2]);
            int defense = Integer.parseInt(parts[3]);
            int dodgeStat = Integer.parseInt(parts[4]);
            return new Dragon(name, level, damage, defense, dodgeStat);
        });
    }

    public static List<Exoskeleton> loadExoskeletons(String filePath) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int level = Integer.parseInt(parts[1]);
            int damage = Integer.parseInt(parts[2]);
            int defense = Integer.parseInt(parts[3]);
            int dodgeStat = Integer.parseInt(parts[4]);
            return new Exoskeleton(name, level, damage, defense, dodgeStat);
        });
    }

    public static List<Spirit> loadSpirits(String filePath) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int level = Integer.parseInt(parts[1]);
            int damage = Integer.parseInt(parts[2]);
            int defense = Integer.parseInt(parts[3]);
            int dodgeStat = Integer.parseInt(parts[4]);
            return new Spirit(name, level, damage, defense, dodgeStat);
        });
    }
}
