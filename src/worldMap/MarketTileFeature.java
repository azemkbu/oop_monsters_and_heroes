package worldMap;

import market.model.Market;

/*
*
* Handles the MarketTile
*
*/

public class MarketTileFeature implements TileFeature {

    private final Market market;

    public MarketTileFeature(Market market) {
        if (market == null) {
            throw new IllegalArgumentException("Market must not be null");
        }
        this.market = market;
    }

    public Market getMarket() {
        return market;
    }
}
