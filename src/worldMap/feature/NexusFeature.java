package worldMap.feature;

import market.model.Market;
import worldMap.TileFeature;

/**
 * Nexus terrain feature that serves as a spawn point and market for heroes.
 * In Legends of Valor, the hero's Nexus provides market functionality.
 * 
 * Note: Nexus does not provide stat bonuses, so all multiplier methods
 * return 1.0 (inherited default from TileFeature interface).
 */
public class NexusFeature implements TileFeature {

    private final Market market;
    private final boolean isHeroNexus;
    private final int laneIndex;

    /**
     * Creates a Nexus feature.
     * @param market the market available at this nexus (null for monster nexus)
     * @param isHeroNexus true if this is a hero nexus, false for monster nexus
     * @param laneIndex the lane index (0=top, 1=mid, 2=bot)
     */
    public NexusFeature(Market market, boolean isHeroNexus, int laneIndex) {
        this.market = market;
        this.isHeroNexus = isHeroNexus;
        this.laneIndex = laneIndex;
    }

    /**
     * Gets the market at this nexus.
     * @return the market, or null if not available
     */
    public Market getMarket() {
        return market;
    }

    /**
     * Checks if this is a hero nexus.
     * @return true if hero nexus, false if monster nexus
     */
    public boolean isHeroNexus() {
        return isHeroNexus;
    }

    /**
     * Checks if this is a monster nexus.
     * @return true if monster nexus, false if hero nexus
     */
    public boolean isMonsterNexus() {
        return !isHeroNexus;
    }

    /**
     * Gets the lane index for this nexus.
     * @return the lane index (0=top, 1=mid, 2=bot)
     */
    public int getLaneIndex() {
        return laneIndex;
    }

    @Override
    public String getEffectDescription() {
        if (isHeroNexus) {
            return "Hero Nexus: Spawn point and Market";
        } else {
            return "Monster Nexus: Monster spawn point";
        }
    }
}
