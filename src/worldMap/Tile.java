package worldMap;

import market.model.Market;
import worldMap.enums.TileType;
import worldMap.feature.NexusFeature;

/**
 * Represents a single tile on the world map.
 * Supports both original game types and Legends of Valor terrain types.
 * 
 * ==================== DESIGN CHANGE LOG ====================
 * 
 * PREVIOUS DESIGN:
 * - Had applyTerrainEffect(Hero) / removeTerrainEffect(Hero) methods
 * - These called feature.applyEffect() / feature.removeEffect()
 * - Required tracking hero enter/exit events
 * 
 * PROBLEMS:
 * 1. Caller had to remember to call removeTerrainEffect when hero left
 * 2. If forgotten, hero stats would be permanently modified
 * 3. Instance variable bug in features (see TerrainBonusFeature)
 * 
 * NEW DESIGN:
 * - Provides getStrengthMultiplier(), getDexterityMultiplier(), getAgilityMultiplier()
 * - These delegate to the feature if present, otherwise return 1.0 (no bonus)
 * - Battle engine queries these when calculating effective stats
 * - No state modification, no tracking needed
 * 
 * USAGE EXAMPLE (in BattleEngine):
 * <pre>
 * Tile heroTile = world.getTile(heroRow, heroCol);
 * int effectiveStrength = (int)(hero.getStrength() * heroTile.getStrengthMultiplier());
 * int effectiveDexterity = (int)(hero.getDexterity() * heroTile.getDexterityMultiplier());
 * int effectiveAgility = (int)(hero.getAgility() * heroTile.getAgilityMultiplier());
 * </pre>
 * 
 * ===========================================================
 */
public class Tile {

    private TileType type;
    private TileFeature feature;

    public Tile(TileType type, TileFeature feature) {
        if (type == null) {
            throw new IllegalArgumentException("Tile type must not be null");
        }
        this.type = type;
        this.feature = feature;
    }

    public TileType getType() {
        return type;
    }

    /**
     * Sets the tile type (used for removing obstacles).
     * @param type the new tile type
     */
    public void setType(TileType type) {
        this.type = type;
    }

    /**
     * Gets the feature associated with this tile.
     * @return the tile feature, or null if none
     */
    public TileFeature getFeature() {
        return feature;
    }

    /**
     * Sets the feature for this tile.
     * @param feature the new feature
     */
    public void setFeature(TileFeature feature) {
        this.feature = feature;
    }

    /**
     * Checks if this tile is accessible (can be entered).
     * @return true if accessible, false otherwise
     */
    public boolean isAccessible() {
        return type != TileType.INACCESSIBLE && type != TileType.OBSTACLE;
    }

    /**
     * Checks if this tile is an obstacle that can be removed.
     * @return true if this is an obstacle
     */
    public boolean isObstacle() {
        return type == TileType.OBSTACLE;
    }

    /**
     * Removes an obstacle from this tile, converting it to a plain tile.
     * @return true if the obstacle was removed, false if not an obstacle
     */
    public boolean removeObstacle() {
        if (type == TileType.OBSTACLE) {
            type = TileType.PLAIN;
            feature = null;
            return true;
        }
        return false;
    }

    /**
     * Checks if this tile is a Nexus.
     * @return true if this is a nexus tile
     */
    public boolean isNexus() {
        return type == TileType.NEXUS;
    }

    /**
     * Checks if this is a hero's Nexus.
     * @return true if this is a hero nexus
     */
    public boolean isHeroNexus() {
        if (feature instanceof NexusFeature) {
            return ((NexusFeature) feature).isHeroNexus();
        }
        return false;
    }

    /**
     * Checks if this is a monster's Nexus.
     * @return true if this is a monster nexus
     */
    public boolean isMonsterNexus() {
        if (feature instanceof NexusFeature) {
            return ((NexusFeature) feature).isMonsterNexus();
        }
        return false;
    }

    /**
     * Gets the market on this tile (if any).
     * Works for both original MARKET tiles and NEXUS tiles with markets.
     * @return the market, or null if none
     */
    public Market getMarket() {
        if (feature instanceof MarketTileFeature) {
            return ((MarketTileFeature) feature).getMarket();
        }
        if (feature instanceof NexusFeature) {
            return ((NexusFeature) feature).getMarket();
        }
        return null;
    }

    // ==================== TERRAIN BONUS QUERY METHODS ====================
    // These replace the old applyTerrainEffect/removeTerrainEffect methods
    // Now we just query the multiplier when needed, no state modification

    /**
     * Gets the strength multiplier for heroes on this tile.
     * 
     * Query-based design: Instead of modifying hero stats, we return the multiplier
     * and let the battle engine calculate effective strength.
     * 
     * @return strength multiplier (1.0 = no bonus, 1.10 = +10%)
     */
    public double getStrengthMultiplier() {
        if (feature != null) {
            return feature.getStrengthMultiplier();
        }
        return 1.0;
    }

    /**
     * Gets the dexterity multiplier for heroes on this tile.
     * 
     * Query-based design: Instead of modifying hero stats, we return the multiplier
     * and let the battle engine calculate effective dexterity.
     * 
     * @return dexterity multiplier (1.0 = no bonus, 1.10 = +10%)
     */
    public double getDexterityMultiplier() {
        if (feature != null) {
            return feature.getDexterityMultiplier();
        }
        return 1.0;
    }

    /**
     * Gets the agility multiplier for heroes on this tile.
     * 
     * Query-based design: Instead of modifying hero stats, we return the multiplier
     * and let the battle engine calculate effective agility (for dodge chance).
     * 
     * @return agility multiplier (1.0 = no bonus, 1.10 = +10%)
     */
    public double getAgilityMultiplier() {
        if (feature != null) {
            return feature.getAgilityMultiplier();
        }
        return 1.0;
    }

    /**
     * Checks if this tile provides a terrain bonus.
     * @return true if the tile has a terrain bonus feature
     */
    public boolean hasTerrainBonus() {
        return type == TileType.BUSH || type == TileType.CAVE || type == TileType.KOULOU;
    }

    /**
     * Gets a description of this tile's effect.
     * @return the effect description
     */
    public String getEffectDescription() {
        if (feature != null) {
            return feature.getEffectDescription();
        }
        return type.getDescription();
    }

    @Override
    public String toString() {
        return type.getSymbol();
    }
}
