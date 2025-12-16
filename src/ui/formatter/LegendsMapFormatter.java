package ui.formatter;

import hero.Hero;
import monster.Monster;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;
import worldMap.enums.TileType;

import java.util.ArrayList;
import java.util.List;

/**
 * Console formatter for Legends of Valor map rendering.
 * Uses block-style colored cells (4 chars wide x 2 lines tall) to match original LOV UI.
 */
public final class LegendsMapFormatter {

    // ANSI Reset
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    // Text colors
    private static final String BLACK = "\u001B[30m";
    private static final String BRIGHT_CYAN = "\u001B[96m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_RED = "\u001B[91m";

    // Background colors (256-color mode for distinct terrain types)
    private static final String BG_NEXUS_HERO = "\u001B[48;5;51m";      // Cyan (Hero Nexus)
    private static final String BG_NEXUS_MONSTER = "\u001B[48;5;202m"; // Deep Orange (Monster Nexus)
    private static final String BG_PLAIN = "\u001B[48;5;151m";         // Pale Green
    private static final String BG_BUSH = "\u001B[48;5;22m";           // Dark Green
    private static final String BG_CAVE = "\u001B[48;5;27m";           // Blue
    private static final String BG_KOULOU = "\u001B[48;5;133m";        // Purple/Magenta
    private static final String BG_WALL = "\u001B[48;5;236m";          // Dark Gray (impassable)
    private static final String BG_HERO_MARKER = "\u001B[48;5;226m";   // Yellow (Hero on map)
    private static final String BG_MONSTER_MARKER = "\u001B[48;5;196m";// Red (Monster on map)
    private static final String BG_OBSTACLE = "\u001B[48;5;94m";       // Brown

    public List<RenderedLine> render(ILegendsWorldMap map) {
        int size = map.getSize();

        List<RenderedLine> lines = new ArrayList<>();

        // Header
        lines.add(new RenderedLine(LineKind.HEADER, ""));
        lines.add(new RenderedLine(LineKind.HEADER, BOLD + BRIGHT_CYAN + "═══════ BATTLEFIELD MAP ═══════" + RESET));

        // Lane labels
        lines.add(new RenderedLine(LineKind.TITLE, ""));
        lines.add(new RenderedLine(LineKind.TITLE, buildLaneLabels()));

        // Column headers
        lines.add(new RenderedLine(LineKind.TITLE, buildColumnHeaders(size)));

        // Grid rows - each row renders 2 lines to make square cells
        for (int row = 0; row < size; row++) {
            // Line 1 of the cell (with content like H1, M)
            StringBuilder line1 = new StringBuilder();
            line1.append(String.format("  %d ", row));
            for (int col = 0; col < size; col++) {
                Tile tile = map.getTile(row, col);
                line1.append(formatCellLine1(map, row, col, tile));
            }
            // Row labels
            if (row == 0) {
                line1.append(BRIGHT_RED + "  ◄ Monster Nexus" + RESET);
            } else if (row == size - 1) {
                line1.append(BRIGHT_GREEN + "  ◄ Hero Nexus" + RESET);
            }
            lines.add(new RenderedLine(LineKind.TITLE, line1.toString()));

            // Line 2 of the cell (just background color)
            StringBuilder line2 = new StringBuilder();
            line2.append("    ");
            for (int col = 0; col < size; col++) {
                Tile tile = map.getTile(row, col);
                line2.append(formatCellLine2(map, row, col, tile));
            }
            lines.add(new RenderedLine(LineKind.TITLE, line2.toString()));
        }

        // Legend
        lines.addAll(buildLegendLines());

        return lines;
    }

    private String buildLaneLabels() {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        sb.append(BRIGHT_YELLOW).append("◄ TOP ►").append(RESET);
        sb.append("   ");
        sb.append(BRIGHT_YELLOW).append("◄ MID ►").append(RESET);
        sb.append("   ");
        sb.append(BRIGHT_YELLOW).append("◄ BOT ►").append(RESET);
        return sb.toString();
    }

    private String buildColumnHeaders(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int col = 0; col < size; col++) {
            sb.append(String.format(" %-2d ", col));
        }
        return sb.toString();
    }

    /**
     * Formats the first line of a cell (contains hero/monster symbols).
     * Each cell is 4 characters wide with background color.
     */
    private String formatCellLine1(ILegendsWorldMap map, int row, int col, Tile tile) {
        // Find hero at this position
        int heroIdx = -1;
        int idx = 0;
        for (Hero h : map.getHeroes()) {
            int[] pos = map.getHeroPosition(h);
            if (pos != null && pos[0] == row && pos[1] == col) {
                heroIdx = idx;
                break;
            }
            idx++;
        }

        // Find monster at this position
        boolean hasMonster = false;
        for (Monster m : map.getMonsters()) {
            int[] pos = map.getMonsterPosition(m);
            if (pos != null && pos[0] == row && pos[1] == col) {
                hasMonster = true;
                break;
            }
        }

        StringBuilder cellStr = new StringBuilder();

        // Every cell must be exactly 4 visible characters wide
        if (heroIdx >= 0 && hasMonster) {
            // Both hero and monster: split color
            cellStr.append(BG_HERO_MARKER).append(BOLD).append(BLACK);
            cellStr.append("H").append(heroIdx + 1);
            cellStr.append(RESET);
            cellStr.append(BG_MONSTER_MARKER).append(BOLD).append(BLACK);
            cellStr.append("M ");
            cellStr.append(RESET);
        } else if (heroIdx >= 0) {
            // Hero only: use hero marker color (Yellow)
            cellStr.append(BG_HERO_MARKER).append(BOLD).append(BLACK);
            cellStr.append(" H").append(heroIdx + 1).append(" ");
            cellStr.append(RESET);
        } else if (hasMonster) {
            // Monster only: use monster marker color (Red)
            cellStr.append(BG_MONSTER_MARKER).append(BOLD).append(BLACK);
            cellStr.append(" M  ");
            cellStr.append(RESET);
        } else {
            // Empty cell: use terrain color
            cellStr.append(getBackgroundColor(tile, row));
            cellStr.append("    ");
            cellStr.append(RESET);
        }

        return cellStr.toString();
    }

    /**
     * Formats the second line of a cell (just background color block).
     * This makes the cell appear square in terminal (4 wide x 2 tall).
     */
    private String formatCellLine2(ILegendsWorldMap map, int row, int col, Tile tile) {
        // Find hero at this position
        int heroIdx = -1;
        int idx = 0;
        for (Hero h : map.getHeroes()) {
            int[] pos = map.getHeroPosition(h);
            if (pos != null && pos[0] == row && pos[1] == col) {
                heroIdx = idx;
                break;
            }
            idx++;
        }

        // Find monster at this position
        boolean hasMonster = false;
        for (Monster m : map.getMonsters()) {
            int[] pos = map.getMonsterPosition(m);
            if (pos != null && pos[0] == row && pos[1] == col) {
                hasMonster = true;
                break;
            }
        }

        // Use marker color if hero/monster present, otherwise terrain color
        if (heroIdx >= 0 && hasMonster) {
            // Split: half hero, half monster
            return BG_HERO_MARKER + "  " + RESET + BG_MONSTER_MARKER + "  " + RESET;
        } else if (heroIdx >= 0) {
            return BG_HERO_MARKER + "    " + RESET;
        } else if (hasMonster) {
            return BG_MONSTER_MARKER + "    " + RESET;
        } else {
            return getBackgroundColor(tile, row) + "    " + RESET;
        }
    }

    /**
     * Gets background color for a cell based on tile type.
     * For Nexus, distinguishes between hero (bottom row) and monster (top row) nexus.
     */
    private String getBackgroundColor(Tile tile, int row) {
        TileType type = tile.getType();
        switch (type) {
            case NEXUS:
                // Row 0 = Monster Nexus (orange), Row 7 = Hero Nexus (cyan)
                return (row == 0) ? BG_NEXUS_MONSTER : BG_NEXUS_HERO;
            case INACCESSIBLE:
                return BG_WALL;
            case BUSH:
                return BG_BUSH;
            case CAVE:
                return BG_CAVE;
            case KOULOU:
                return BG_KOULOU;
            case OBSTACLE:
                return BG_OBSTACLE;
            case PLAIN:
            default:
                return BG_PLAIN;
        }
    }

    private List<RenderedLine> buildLegendLines() {
        List<RenderedLine> legend = new ArrayList<>();

        legend.add(new RenderedLine(LineKind.HEADER, ""));
        legend.add(new RenderedLine(LineKind.HEADER, BRIGHT_CYAN + "Legend:" + RESET));

        // Row 1: Hero Nexus & Monster Nexus
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_NEXUS_HERO + "    " + RESET + " Hero Nexus     " +
            BG_NEXUS_MONSTER + "    " + RESET + " Monster Nexus"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_NEXUS_HERO + "    " + RESET + "                " +
            BG_NEXUS_MONSTER + "    " + RESET));

        // Row 2: Plain & Bush
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_PLAIN + "    " + RESET + " Plain          " +
            BG_BUSH + "    " + RESET + " Bush (+DEX)"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_PLAIN + "    " + RESET + "                " +
            BG_BUSH + "    " + RESET));

        // Row 3: Cave & Koulou
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_CAVE + "    " + RESET + " Cave (+AGI)    " +
            BG_KOULOU + "    " + RESET + " Koulou (+STR)"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_CAVE + "    " + RESET + "                " +
            BG_KOULOU + "    " + RESET));

        // Row 4: Wall & Obstacle
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_WALL + "    " + RESET + " Wall (impassable)"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_WALL + "    " + RESET));
        
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_OBSTACLE + "    " + RESET + " Obstacle (removable)"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_OBSTACLE + "    " + RESET));

        // Row 5: Hero & Monster markers
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_HERO_MARKER + BOLD + BLACK + " H1 " + RESET + " = Hero      " +
            BG_MONSTER_MARKER + BOLD + BLACK + " M  " + RESET + " = Monster"));
        legend.add(new RenderedLine(LineKind.TITLE, 
            "  " + BG_HERO_MARKER + "    " + RESET + "             " +
            BG_MONSTER_MARKER + "    " + RESET));

        return legend;
    }
}
