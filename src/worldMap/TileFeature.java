package worldMap;

/**
 * Interface representing a feature that can be applied to a tile.
 * Uses the Decorator pattern to add behaviors to tiles.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN:
 * <pre>
 * public interface TileFeature {
 *     default void applyEffect(Hero hero) { }
 *     default void removeEffect(Hero hero) { }
 *     default String getEffectDescription() { return "No special effect"; }
 * }
 * </pre>
 * 
 * PROBLEMS:
 * 1. applyEffect/removeEffect encouraged direct stat modification
 * 2. Required tracking when hero enters/exits tiles
 * 3. Bug-prone: easy to forget to call removeEffect or call it with wrong values
 * 
 * NEW DESIGN:
 * - Provides query methods: getStrengthMultiplier(), getDexterityMultiplier(), getAgilityMultiplier()
 * - No direct hero modification - battle engine queries multipliers as needed
 * - Stateless and safe - no tracking required
 * 
 * USAGE EXAMPLE (in BattleEngine):
 * <pre>
 * Tile heroTile = world.getTile(heroRow, heroCol);
 * double strMult = heroTile.getStrengthMultiplier();
 * int effectiveStrength = (int)(hero.getStrength() * strMult);
 * </pre>
 * 
 * ===========================================================
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
