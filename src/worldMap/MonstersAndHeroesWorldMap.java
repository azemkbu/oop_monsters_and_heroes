package worldMap;

import hero.Party;
import market.model.Market;
import market.service.MarketFactory;
import utils.GameConstants;
import worldMap.enums.Direction;
import worldMap.enums.TileType;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Implementation of {@link IWorldMap} for the original Monsters and Heroes game.
 * Represents a square world map with randomly distributed tiles (Common, Market, Inaccessible).
 */
public class MonstersAndHeroesWorldMap implements IWorldMap {

    private final int size;
    private final Tile[][] grid;
    private final Random random = new SecureRandom();
    private final MarketFactory marketFactory;

    public MonstersAndHeroesWorldMap(int size, MarketFactory marketFactory) {
        this.size = size;
        this.grid = new Tile[size][size];
        this.marketFactory = marketFactory;
        generateRandomLayout();
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public Tile getTile(int row, int col) {
        if (!checkBounds(row, col)) {
            throw new IndexOutOfBoundsException("Invalid tile coordinates: (" + row + ", " + col + ')');
        }
        return grid[row][col];
    }

    @Override
    public boolean checkBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    @Override
    public boolean isAccessible(int row, int col) {
        return checkBounds(row, col) && grid[row][col].isAccessible();
    }

    @Override
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

    @Override
    public Tile getPartyTile(Party party) {
        return getTile(party.getRow(), party.getCol());
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

        // Ensure starting position is accessible
        grid[0][0] = new Tile(TileType.COMMON, null);
    }

    private void addTileByType(List<TileType> list, TileType type, int count) {
        for (int i = 0; i < count; i++) {
            list.add(type);
        }
    }
}

