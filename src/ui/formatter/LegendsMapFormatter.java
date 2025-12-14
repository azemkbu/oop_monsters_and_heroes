package ui.formatter;

import hero.Hero;
import monster.Monster;
import worldMap.ILegendsWorldMap;
import worldMap.Tile;
import worldMap.enums.TileType;

import java.util.ArrayList;
import java.util.List;

import static utils.ConsoleColors.*;

/**
 * Console formatter for Legends of Valor map rendering.
 * This class contains view/presentation logic only (string building, colors, legend).
 */
public final class LegendsMapFormatter {

    public List<RenderedLine> render(ILegendsWorldMap map) {
        int size = map.getSize();

        List<RenderedLine> lines = new ArrayList<>();

        lines.add(new RenderedLine(LineKind.HEADER, CYAN + "========== LEGENDS OF VALOR =========="));

        // Column headers
        StringBuilder header = new StringBuilder("    ");
        for (int col = 0; col < size; col++) {
            header.append(String.format(" %d  ", col));
        }
        lines.add(new RenderedLine(LineKind.TITLE, header.toString()));

        // Top border
        lines.add(new RenderedLine(LineKind.TITLE, buildRowBorder(size)));

        for (int row = 0; row < size; row++) {
            StringBuilder line = new StringBuilder();
            line.append(String.format(" %d |", row));

            for (int col = 0; col < size; col++) {
                Tile tile = map.getTile(row, col);
                String cellContent = getCellContent(map, row, col, tile);
                line.append(cellContent).append("|");
            }

            lines.add(new RenderedLine(LineKind.TITLE, line.toString()));

            if (row < size - 1) {
                lines.add(new RenderedLine(LineKind.TITLE, buildRowBorder(size)));
            }
        }

        // Bottom border + legend
        lines.add(new RenderedLine(LineKind.TITLE, buildRowBorder(size)));
        lines.addAll(buildLegendLines());

        return lines;
    }

    private String getCellContent(ILegendsWorldMap map, int row, int col, Tile tile) {
        // Check for hero at this position
        String heroChar = " ";
        int heroIndex = 0;
        for (Hero h : map.getHeroes()) {
            int[] pos = map.getHeroPosition(h);
            if (pos != null && pos[0] == row && pos[1] == col) {
                heroChar = "H" + (heroIndex + 1);
                break;
            }
            heroIndex++;
        }

        // Check for monster at this position
        String monsterChar = " ";
        int monsterIndex = 0;
        for (Monster m : map.getMonsters()) {
            int[] pos = map.getMonsterPosition(m);
            if (pos != null && pos[0] == row && pos[1] == col) {
                monsterChar = "M" + (monsterIndex + 1);
                break;
            }
            monsterIndex++;
        }

        // Format: "XTY" where X=hero, T=tile type, Y=monster
        String tileSymbol = tile.getType().getSymbol();
        String colorCode = getTileColor(tile.getType());

        StringBuilder content = new StringBuilder();
        content.append(colorCode);

        if (!heroChar.equals(" ") || !monsterChar.equals(" ")) {
            content.append(heroChar.equals(" ") ? " " : heroChar.charAt(0));
            content.append(tileSymbol);
            content.append(monsterChar.equals(" ") ? " " : monsterChar.charAt(0));
        } else {
            content.append(" ").append(tileSymbol).append(" ");
        }

        content.append(RESET);
        return content.toString();
    }

    private String getTileColor(TileType type) {
        switch (type) {
            case NEXUS:
                return BG_CYAN + BLACK;
            case INACCESSIBLE:
                return BG_RED + WHITE;
            case BUSH:
                return BG_GREEN + BLACK;
            case CAVE:
                return BG_BLUE + WHITE;
            case KOULOU:
                return BG_YELLOW + BLACK;
            case OBSTACLE:
                return BG_WHITE + BLACK;
            case PLAIN:
            default:
                return BG_BLACK + WHITE;
        }
    }

    private String buildRowBorder(int size) {
        StringBuilder border = new StringBuilder("   +");
        for (int col = 0; col < size; col++) {
            border.append("---+");
        }
        return border.toString();
    }

    private List<RenderedLine> buildLegendLines() {
        List<RenderedLine> legend = new ArrayList<>();

        legend.add(new RenderedLine(LineKind.HEADER, "Legend:"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_CYAN + BLACK + " N " + RESET + " = Nexus (spawn/market)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_RED + WHITE + " I " + RESET + " = Inaccessible (wall)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_GREEN + BLACK + " B " + RESET + " = Bush (+10% Dexterity)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_BLUE + WHITE + " V " + RESET + " = Cave (+10% Agility)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_YELLOW + BLACK + " K " + RESET + " = Koulou (+10% Strength)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_WHITE + BLACK + " O " + RESET + " = Obstacle (removable)"));
        legend.add(new RenderedLine(LineKind.TITLE, "  " + BG_BLACK + WHITE + " P " + RESET + " = Plain (no effect)"));
        legend.add(new RenderedLine(LineKind.TITLE, ""));
        legend.add(new RenderedLine(LineKind.TITLE, "  H1, H2, H3 = Heroes | M1, M2, M3 = Monsters"));

        return legend;
    }
}


