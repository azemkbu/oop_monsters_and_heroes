package ui.formatter;

import combat.RangeCalculator;
import monster.Monster;

import java.util.ArrayList;
import java.util.List;

/**
 * Formats a monster's status panel for console display.
 * Includes HP bar and combat stats.
 */
public final class MonsterStatusFormatter {
    
    private static final int BAR_WIDTH = 15;
    private static final int PANEL_WIDTH = 65;
    
    private MonsterStatusFormatter() {}
    
    /**
     * Renders a complete status panel for a monster.
     * 
     * @param monster the monster to render
     * @param index the monster's index (0-based, displayed as M1/M2/M3)
     * @return list of lines to print
     */
    public static List<String> render(Monster monster, int index) {
        List<String> lines = new ArrayList<>();
        
        int effectiveRange = RangeCalculator.getEffectiveRange(monster);
        String monsterType = monster.getClass().getSimpleName().toUpperCase();
        
        // Line 1: Name, type, level, range
        String header = String.format("[M%d] %s (%s) Lv.%d        Range: %d",
                index + 1,
                monster.getName(),
                monsterType,
                monster.getLevel(),
                effectiveRange);
        lines.add(padRight(header, PANEL_WIDTH));
        
        // Line 2: HP bar and combat stats
        String hpBar = StatusBarRenderer.renderLabeledBar("HP", monster.getHp(), monster.getMaxHp(), BAR_WIDTH);
        String statsLine = String.format("%s  DMG:%d  DEF:%d  DODGE:%.0f%%",
                hpBar,
                monster.getBaseDamage(),
                monster.getDefense(),
                monster.getDodgeChance() * 100);
        lines.add(padRight(statsLine, PANEL_WIDTH));
        
        return lines;
    }
    
    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < width) {
            sb.append(' ');
        }
        return sb.toString();
    }
}

