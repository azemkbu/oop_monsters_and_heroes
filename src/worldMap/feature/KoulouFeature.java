package worldMap.feature;

/**
 * Koulou terrain feature that provides strength bonus to heroes standing on it.
 * Strength affects physical attack damage in combat.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN (Buggy):
 * - Directly modified hero.setStrength() on enter/exit
 * - Stored lastBonusApplied as instance variable (shared state bug)
 * 
 * NEW DESIGN:
 * - Only provides getStrengthMultiplier() -> returns 1.10
 * - Battle engine calculates: effectiveStrength = hero.getStrength() * multiplier
 * - Attack damage calculation uses effective strength
 * 
 * ===========================================================
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
     * 
     * Usage in BattleEngine:
     * <pre>
     * int effectiveStrength = (int)(hero.getStrength() * tile.getStrengthMultiplier());
     * int damage = (int)(effectiveStrength * ATTACK_MULTIPLIER);
     * </pre>
     * 
     * @return 1.10 for default 10% bonus (1.0 + 0.10)
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
