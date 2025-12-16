package ui.formatter;

/**
 * Utility class for rendering progress bars (HP, MP, etc.)
 * using ASCII/Unicode characters.
 */
public final class StatusBarRenderer {
    
    private static final char FILLED = '\u2588';  // █ (full block)
    private static final char EMPTY = '\u2591';   // ░ (light shade)
    
    private StatusBarRenderer() {}
    
    /**
     * Renders a progress bar like "[████████░░░░]"
     * 
     * @param current current value
     * @param max maximum value
     * @param width number of characters for the bar (excluding brackets)
     * @return rendered bar string
     */
    public static String renderBar(int current, int max, int width) {
        if (max <= 0) {
            return "[" + repeat(EMPTY, width) + "]";
        }
        
        double ratio = (double) current / max;
        if (ratio < 0) ratio = 0;
        if (ratio > 1) ratio = 1;
        
        int filled = (int) Math.round(ratio * width);
        filled = Math.min(filled, width);
        
        return "[" + repeat(FILLED, filled) + repeat(EMPTY, width - filled) + "]";
    }
    
    /**
     * Renders a progress bar with label like "HP [████████░░] 80/100"
     */
    public static String renderLabeledBar(String label, int current, int max, int barWidth) {
        return String.format("%s %s %3d/%d", label, renderBar(current, max, barWidth), current, max);
    }
    
    /**
     * Repeats a character n times.
     */
    private static String repeat(char c, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}

