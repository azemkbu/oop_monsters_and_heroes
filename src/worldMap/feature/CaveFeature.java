package worldMap.feature;

/**
 * Cave terrain feature that provides +10% agility bonus to heroes standing on it.
 */
public class CaveFeature extends TerrainBonusFeature {

    /**
     * Creates a Cave feature with the default 10% agility bonus.
     */
    public CaveFeature() {
        super();
    }

    /**
     * Creates a Cave feature with a custom agility bonus.
     * @param bonusMultiplier the bonus multiplier (e.g., 0.10 for 10%)
     */
    public CaveFeature(double bonusMultiplier) {
        super(bonusMultiplier);
    }

    /**
     * Gets the agility multiplier for heroes on this tile.
     * @return 1.10 for default 10% bonus
     */
    @Override
    public double getAgilityMultiplier() {
        return 1.0 + bonusMultiplier;
    }

    @Override
    public String getEffectDescription() {
        return String.format("Cave: +%.0f%% Agility", bonusMultiplier * 100);
    }

    @Override
    public String getTerrainName() {
        return "Cave";
    }

    @Override
    public String getAffectedStatName() {
        return "Agility";
    }
}
