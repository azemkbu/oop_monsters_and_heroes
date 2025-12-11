package worldMap.feature;

import worldMap.TileFeature;

/**
 * Abstract base class for terrain features that provide stat bonuses to heroes.
 * Subclasses override specific multiplier methods (e.g., getStrengthMultiplier).
 */
public abstract class TerrainBonusFeature implements TileFeature {

    /** The default bonus multiplier (10% increase as per game requirements) */
    public static final double DEFAULT_BONUS_MULTIPLIER = 0.10;

    protected final double bonusMultiplier;

    /**
     * Creates a terrain bonus feature with the default bonus multiplier (10%).
     */
    protected TerrainBonusFeature() {
        this.bonusMultiplier = DEFAULT_BONUS_MULTIPLIER;
    }

    /**
     * Creates a terrain bonus feature with a custom bonus multiplier.
     * @param bonusMultiplier the bonus multiplier (e.g., 0.10 for 10%)
     */
    protected TerrainBonusFeature(double bonusMultiplier) {
        this.bonusMultiplier = bonusMultiplier;
    }

    /**
     * Gets the raw bonus percentage (e.g., 0.10 for 10%).
     * @return the bonus percentage
     */
    public double getBonusPercentage() {
        return bonusMultiplier;
    }

    /**
     * Gets the strength multiplier for this terrain.
     * Override in subclasses that provide strength bonus.
     * 
     * @return multiplier (e.g., 1.10 for +10% strength), default is 1.0 (no bonus)
     */
    public double getStrengthMultiplier() {
        return 1.0;
    }

    /**
     * Gets the dexterity multiplier for this terrain.
     * Override in subclasses that provide dexterity bonus.
     * 
     * @return multiplier (e.g., 1.10 for +10% dexterity), default is 1.0 (no bonus)
     */
    public double getDexterityMultiplier() {
        return 1.0;
    }

    /**
     * Gets the agility multiplier for this terrain.
     * Override in subclasses that provide agility bonus.
     * 
     * @return multiplier (e.g., 1.10 for +10% agility), default is 1.0 (no bonus)
     */
    public double getAgilityMultiplier() {
        return 1.0;
    }

    /**
     * Gets the name of this terrain type.
     * @return the terrain name
     */
    public abstract String getTerrainName();

    /**
     * Gets the name of the stat affected by this terrain.
     * @return the stat name
     */
    public abstract String getAffectedStatName();
}
