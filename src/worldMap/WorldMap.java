package worldMap;

import hero.Party;
import market.model.Market;
import market.service.MarketFactory;
import utils.IOUtils;
import utils.GameConstants;
import worldMap.enums.Direction;
import worldMap.enums.TileType;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static utils.ConsoleColors.*;

/**
 * Represents the square world map
 */
public class WorldMap {

    private final int size;
    private final Tile[][] grid;
    private final Random random = new SecureRandom();
    private final MarketFactory marketFactory;
    private final IOUtils ioUtils;

    public WorldMap(int size, MarketFactory marketFactory, IOUtils ioUtils) {
        this.size = size;
        this.grid = new Tile[size][size];
        this.marketFactory = marketFactory;
        this.ioUtils = ioUtils;
        generateRandomLayout();
    }

    public Tile getTile(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IndexOutOfBoundsException("Invalid tile coordinates: (" + row + ", " + col + ')');
        }
        return grid[row][col];
    }

    public boolean checkBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public boolean isAccessible(int row, int col) {
        return checkBounds(row, col) && grid[row][col].isAccessible();
    }


    public boolean moveParty(Party party, Direction direction) {
        int currentRow = party.getRow();
        int currentCol = party.getCol();

        int newRow = currentRow + direction.getRow();
        int newCol = currentCol + direction.getCol();

        if (!isAccessible(newRow, newCol)) {
            return false;
        }

        party.setPosition(newRow, newCol);
        return true;
    }


    public Tile getPartyTile(Party party) {
        return getTile(party.getRow(), party.getCol());
    }

    public void printMap(Party party) {
        ioUtils.printlnHeader(GREEN + "=======WORLD MAP======");
        for (int row = 0; row < size; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < size; col++) {

                boolean isParty = (row == party.getRow() && col == party.getCol());
                Tile tile = grid[row][col];

                if (isParty) {
                    line.append(BG_CYAN).append(BOLD).append(BLACK).append(" P ").append(RESET);
                } else {
                    switch (tile.getType()) {
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
            ioUtils.printlnTitle(String.valueOf(line));
        }
        printMapLegend();
    }
    private void printMapLegend() {
        ioUtils.printlnHeader("Legend:");
        ioUtils.printlnTitle("  " + BG_CYAN   + "   " + RESET + "  = Party position");
        ioUtils.printlnTitle("  " + BG_GREEN  + "   " + RESET + "  = Market tile");
        ioUtils.printlnTitle("  " + BG_RED    + "   " + RESET + "  = Inaccessible tile");
        ioUtils.printlnTitle("  " + BG_YELLOW + "   " + RESET + "  = Common tile (possible battles)");
    }


    private void generateRandomLayout() {
        int totalTiles = size * size;
        int numInaccessible = (int) Math.round(totalTiles * GameConstants.WORLD_MAP_INACCESSIBLE_RATIO);
        int numMarket = (int) Math.round(totalTiles * GameConstants.WORLD_MAP_MARKET_RATIO);
        int numCommon = (int) Math.round(totalTiles * GameConstants.WORLD_MAP_COMMON_RATIO);

        List<TileType> types = new ArrayList<>(totalTiles);
        addTileByType(types, TileType.INACCESSIBLE, numInaccessible);
        addTileByType(types, TileType.MARKET, numMarket);
        addTileByType(types, TileType.COMMON, numCommon);

        Collections.shuffle(types, random);

        int index = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                TileType tileType = types.get(index++);
                TileFeature feature = null;

                if (tileType == TileType.MARKET) {
                    Market market = marketFactory.createRandomMarket();
                    feature = new MarketTileFeature(market);
                }

                grid[row][col] = new Tile(tileType, feature);
            }
        }

        grid[0][0] = new Tile(TileType.COMMON, null);
    }

    private void addTileByType(List<TileType> list, TileType type, int count) {
        for (int i = 0; i < count; i++) {
            list.add(type);
        }
    }
}
