package ui.formatter;

import combat.RangeCalculator;
import hero.Hero;
import market.model.item.Armor;
import market.model.item.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Formats a hero's status panel for console display.
 * Includes HP bar, MP bar, equipment, and stats.
 */
public final class HeroStatusFormatter {
    
    private static final int BAR_WIDTH = 15;
    
    private HeroStatusFormatter() {}
    
    /**
     * Renders a complete status panel for a hero.
     * 
     * @param hero the hero to render
     * @param index the hero's index (0-based, displayed as H1/H2/H3)
     * @return list of lines to print
     */
    public static List<String> render(Hero hero, int index) {
        List<String> lines = new ArrayList<>();
        
        int effectiveRange = RangeCalculator.getEffectiveRange(hero);
        
        // Line 1: Name, class, level, range
        String header = String.format("[H%d] %s (%s) Lv.%d | Range: %d",
                index + 1,
                hero.getName(),
                hero.getHeroClassName(),
                hero.getLevel(),
                effectiveRange);
        lines.add(header);
        
        // Line 2: HP and MP bars
        String hpBar = StatusBarRenderer.renderLabeledBar("HP", hero.getHp(), hero.getMaxHp(), BAR_WIDTH);
        String mpBar = StatusBarRenderer.renderLabeledBar("MP", hero.getMp(), hero.getMaxMp(), BAR_WIDTH);
        String barsLine = hpBar + "  " + mpBar;
        lines.add(barsLine);
        
        // Line 3: Equipment and gold
        String weaponStr = formatWeapon(hero.getEquippedWeapon());
        String armorStr = formatArmor(hero.getEquippedArmor());
        String equipLine = String.format("  Weapon: %s | Armor: %s | Gold: %dg",
                weaponStr, armorStr, hero.getGold());
        lines.add(equipLine);
        
        return lines;
    }
    
    private static String formatWeapon(Weapon w) {
        if (w == null) return "None";
        String rangeBonus = w.getRangeBonus() > 0 ? ", +" + w.getRangeBonus() + "range" : "";
        return String.format("%s (%ddmg%s)", w.getName(), w.getDamage(), rangeBonus);
    }
    
    private static String formatArmor(Armor a) {
        if (a == null) return "None";
        return String.format("%s (%ddef)", a.getName(), a.getDamageReduction());
    }
}

