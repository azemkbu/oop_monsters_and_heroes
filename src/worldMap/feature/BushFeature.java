package worldMap.feature;

/**
 * Bush terrain feature that provides dexterity bonus to heroes standing on it.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN (Buggy):
 * <pre>
 * public class BushFeature extends TerrainBonusFeature {
 *     private int lastBonusApplied = 0;  // BUG: Instance variable shared across calls!
 * 
 *     public void applyEffect(Hero hero) {
 *         lastBonusApplied = (int)(hero.getDexterity() * bonusMultiplier);
 *         hero.setDexterity(hero.getDexterity() + lastBonusApplied); // Modifies hero!
 *     }
 * 
 *     public void removeEffect(Hero hero) {
 *         hero.setDexterity(hero.getDexterity() - lastBonusApplied); // Uses stale value!
 *     }
 * }
 * </pre>
 * 
 * BUG SCENARIO:
 * 1. Hero A enters bush, lastBonusApplied = 50
 * 2. Hero B enters same bush, lastBonusApplied = 80 (overwritten!)
 * 3. Hero A leaves bush, removes 80 instead of 50 -> WRONG STATS!
 * 
 * NEW DESIGN:
 * - Only provides getDexterityMultiplier() -> returns 1.10
 * - Battle engine uses: effectiveDex = hero.getDexterity() * tile.getDexterityMultiplier()
 * - Hero's base dexterity is NEVER modified
 * 
 * ===========================================================
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
     * 
     * Usage in BattleEngine:
     * <pre>
     * int effectiveDexterity = (int)(hero.getDexterity() * tile.getDexterityMultiplier());
     * </pre>
     * 
     * @return 1.10 for default 10% bonus (1.0 + 0.10)
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
