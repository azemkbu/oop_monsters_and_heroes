package worldMap;

import market.model.Market;
import worldMap.enums.TileType;

/**
 * Represents a single tile on {@link WorldMap}
 */
public class Tile {

    private final TileType type;
    private final TileFeature feature;

    public Tile(TileType type, TileFeature feature) {
        if (type == null) {
            throw new IllegalArgumentException("Tile type must not be null");
        }
        this.type = type;
        this.feature = feature;
    }

    public TileType getType() {
        return type;
    }

    public boolean isAccessible() {
        return type != TileType.INACCESSIBLE;
    }

    public Market getMarket() {
        if (feature instanceof MarketTileFeature) {
            return ((MarketTileFeature) feature).getMarket();
        }
        return null;
    }
}
