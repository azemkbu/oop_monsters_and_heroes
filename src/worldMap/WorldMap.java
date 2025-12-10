package worldMap;

import market.service.MarketFactory;
import utils.IOUtils;

/**
 * Backward-compatible WorldMap class that extends MonstersAndHeroesWorldMap.
 * This class is kept for compatibility with existing code that uses WorldMap directly.
 * 
 * @deprecated Use {@link MonstersAndHeroesWorldMap} or {@link LegendsOfValorWorldMap} directly
 *             based on the game type.
 */
@Deprecated
public class WorldMap extends MonstersAndHeroesWorldMap {

    /**
     * Creates a new WorldMap (delegates to MonstersAndHeroesWorldMap)
     * @param size the size of the map
     * @param marketFactory the factory for creating markets
     * @param ioUtils the I/O utilities
     */
    public WorldMap(int size, MarketFactory marketFactory, IOUtils ioUtils) {
        super(size, marketFactory, ioUtils);
    }
}
