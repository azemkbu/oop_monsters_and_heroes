package ui.formatter;

import hero.Party;
import worldMap.IWorldMap;
import worldMap.Tile;
import worldMap.enums.TileType;

import java.util.ArrayList;
import java.util.List;

import static utils.ConsoleColors.*;

/**
 * Console formatter for Monsters and Heroes map rendering.
 * View-only: builds colored strings, does not print.
 */
public final class MhMapFormatter {

    public List<RenderedLine> render(IWorldMap map, Party party) {
        int size = map.getSize();
        List<RenderedLine> lines = new ArrayList<>();

        lines.add(new RenderedLine(LineKind.HEADER, GREEN + "=======WORLD MAP======"));

        for (int row = 0; row < size; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < size; col++) {
                boolean isParty = (row == party.getRow() && col == party.getCol());
                Tile tile = map.getTile(row, col);

                if (isParty) {
                    line.append(BG_CYAN).append(BOLD).append(BLACK).append(" P ").append(RESET);
                } else {
                    TileType type = tile.getType();
                    switch (type) {
                        case INACCESSIBLE:
                            line.append(BG_RED).append("   ").append(RESET);
                            break;
                        case MARKET:
                            line.append(BG_GREEN).append("   ").append(RESET);
                            break;
                        case COMMON:
                        default:
                            line.append(BG_YELLOW).append("   ").append(RESET);
                            break;
                    }
                }
            }
            lines.add(new RenderedLine(LineKind.TITLE, line.toString()));
        }

        lines.add(new RenderedLine(LineKind.HEADER, "Legend:"));
        lines.add(new RenderedLine(LineKind.TITLE, "  " + BG_CYAN   + "   " + RESET + "  = Party position"));
        lines.add(new RenderedLine(LineKind.TITLE, "  " + BG_GREEN  + "   " + RESET + "  = Market tile"));
        lines.add(new RenderedLine(LineKind.TITLE, "  " + BG_RED    + "   " + RESET + "  = Inaccessible tile"));
        lines.add(new RenderedLine(LineKind.TITLE, "  " + BG_YELLOW + "   " + RESET + "  = Common tile (possible battles)"));

        return lines;
    }
}


