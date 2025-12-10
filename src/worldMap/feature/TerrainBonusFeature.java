package worldMap.feature;

import worldMap.TileFeature;

/**
 * Abstract base class for terrain features that provide stat bonuses to heroes.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN (Problematic):
 * - Used applyEffect(Hero) / removeEffect(Hero) to directly modify hero stats
 * - Each feature stored "lastBonusApplied" as instance variable
 * - Example: hero.setDexterity(hero.getDexterity() + bonus)
 * 
 * PROBLEMS WITH PREVIOUS DESIGN:
 * 1. Instance variable bug: If two heroes stood on same tile (even briefly),
 *    "lastBonusApplied" would be overwritten, causing incorrect stat restoration
 * 2. State pollution: Hero's base stats were modified, making it hard to track
 *    original values vs. buffed values
 * 3. Lifecycle complexity: Had to carefully manage when to apply/remove effects
 * 
 * NEW DESIGN (Query-based, following LOV reference project):
 * - Provides getXxxMultiplier() methods that return bonus multipliers
 * - Does NOT modify hero stats directly
 * - Battle engine queries the multiplier when calculating damage/defense
 * - Hero's base stats remain unchanged
 * 
 * BENEFITS OF NEW DESIGN:
 * 1. No state tracking needed - stateless and thread-safe
 * 2. Clean separation - terrain knows its bonus, battle calculates with it
 * 3. Easy to query - just ask "what's the multiplier?" anytime
 * 4. Follows Open/Closed Principle - easy to add new terrain types
 * 
 * ===========================================================
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
