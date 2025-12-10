package worldMap.feature;

/**
 * Cave terrain feature that provides agility bonus to heroes standing on it.
 * Agility affects dodge chance in combat.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN (Buggy):
 * - Directly modified hero.setAgility() on enter/exit
 * - Stored lastBonusApplied as instance variable (shared state bug)
 * 
 * NEW DESIGN:
 * - Only provides getAgilityMultiplier() -> returns 1.10
 * - Battle engine calculates: effectiveAgility = hero.getAgility() * multiplier
 * - Dodge chance calculation uses effective agility
 * 
 * ===========================================================
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
     * 
     * Usage in BattleEngine:
     * <pre>
     * int effectiveAgility = (int)(hero.getAgility() * tile.getAgilityMultiplier());
     * double dodgeChance = effectiveAgility * DODGE_MULTIPLIER;
     * </pre>
     * 
     * @return 1.10 for default 10% bonus (1.0 + 0.10)
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
