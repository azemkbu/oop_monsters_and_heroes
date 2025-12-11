package worldMap.feature;

/**
 * Bush terrain feature that provides +10% dexterity bonus to heroes standing on it.
 */
public class BushFeature extends TerrainBonusFeature {

    /**
     * Creates a Bush feature with the default 10% dexterity bonus.
     */
    public BushFeature() {
        super();
    }

    /**
     * Creates a Bush feature with a custom dexterity bonus.
     * @param bonusMultiplier the bonus multiplier (e.g., 0.10 for 10%)
     */
    public BushFeature(double bonusMultiplier) {
        super(bonusMultiplier);
    }

    /**
     * Gets the dexterity multiplier for heroes on this tile.
     * @return 1.10 for default 10% bonus
     */
    @Override
    public double getDexterityMultiplier() {
        return 1.0 + bonusMultiplier;
    }

    @Override
    public String getEffectDescription() {
        return String.format("Bush: +%.0f%% Dexterity", bonusMultiplier * 100);
    }

    @Override
    public String getTerrainName() {
        return "Bush";
    }

    @Override
    public String getAffectedStatName() {
        return "Dexterity";
    }
}
