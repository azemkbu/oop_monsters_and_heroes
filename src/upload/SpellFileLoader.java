package upload;

import java.util.ArrayList;
import java.util.List;
import market.model.item.Spell;
import market.model.item.SpellType;
import upload.base.DefaultTextFileReader;
import static upload.base.GenericFileLoader.load;

/*
*
*  Handles loading spells from .txt file
*
*
*/

public final class SpellFileLoader {

    private SpellFileLoader() {
    }

    public static List<Spell> loadAllSpells(String baseDir) {
        List<Spell> spells = new ArrayList<>();
        spells.addAll(loadIceSpells(baseDir + "/IceSpells.txt"));
        spells.addAll(loadFireSpells(baseDir + "/FireSpells.txt"));
        spells.addAll(loadLightningSpells(baseDir + "/LightningSpells.txt"));
        return spells;
    }

    public static List<Spell> loadIceSpells(String filePath) {
        return loadSpellsOfType(filePath, SpellType.ICE);
    }

    public static List<Spell> loadFireSpells(String filePath) {
        return loadSpellsOfType(filePath, SpellType.FIRE);
    }

    public static List<Spell> loadLightningSpells(String filePath) {
        return loadSpellsOfType(filePath, SpellType.LIGHTNING);
    }

    private static List<Spell> loadSpellsOfType(String filePath, SpellType type) {
        return load(DefaultTextFileReader.get(), filePath, parts -> {
            String name = parts[0];
            int cost = Integer.parseInt(parts[1]);
            int requiredLevel = Integer.parseInt(parts[2]);
            int damage = Integer.parseInt(parts[3]);
            int manaCost = Integer.parseInt(parts[4]);
            return new Spell(name, cost, requiredLevel, damage, manaCost, type);
        });
    }
}
