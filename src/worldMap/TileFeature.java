package worldMap;

/**
 * Interface representing a feature that can be applied to a tile.
 * Provides stat multipliers for terrain bonuses (Decorator pattern).
 */
public interface TileFeature {

    /**
     * Gets the strength multiplier provided by this feature.
     * @return multiplier (1.0 = no bonus, 1.10 = +10% bonus)
     */
    default double getStrengthMultiplier() {
        return 1.0;
    }

    /**
     * Gets the dexterity multiplier provided by this feature.
     * @return multiplier (1.0 = no bonus, 1.10 = +10% bonus)
     */
    default double getDexterityMultiplier() {
        return 1.0;
    }

    /**
     * Gets the agility multiplier provided by this feature.
     * @return multiplier (1.0 = no bonus, 1.10 = +10% bonus)
     */
    default double getAgilityMultiplier() {
        return 1.0;
    }

    /**
     * Gets the description of this feature's effect.
     * @return the description string
     */
    default String getEffectDescription() {
        return "No special effect";
    }
}
