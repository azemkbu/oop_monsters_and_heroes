package worldMap.feature;

/**
 * Koulou terrain feature that provides +10% strength bonus to heroes standing on it.
 */
public class KoulouFeature extends TerrainBonusFeature {

    /**
     * Creates a Koulou feature with the default 10% strength bonus.
     */
    public KoulouFeature() {
        super();
    }

    /**
     * Creates a Koulou feature with a custom strength bonus.
     * @param bonusMultiplier the bonus multiplier (e.g., 0.10 for 10%)
     */
    public KoulouFeature(double bonusMultiplier) {
        super(bonusMultiplier);
    }

    /**
     * Gets the strength multiplier for heroes on this tile.
     * @return 1.10 for default 10% bonus
     */
    @Override
    public double getStrengthMultiplier() {
        return 1.0 + bonusMultiplier;
    }

    @Override
    public String getEffectDescription() {
        return String.format("Koulou: +%.0f%% Strength", bonusMultiplier * 100);
    }

    @Override
    public String getTerrainName() {
        return "Koulou";
    }

    @Override
    public String getAffectedStatName() {
        return "Strength";
    }
}
