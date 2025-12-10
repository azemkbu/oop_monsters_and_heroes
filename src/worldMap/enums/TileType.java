package worldMap.enums;

/**
 * Represents types of tiles on the {@link worldMap.WorldMap}
 */
public enum TileType {
    // Original game types
    INACCESSIBLE("I", "Inaccessible - Cannot enter"),
    MARKET("M", "Market - Buy and sell items"),
    COMMON("C", "Common - Possible battle encounters"),

    // Legends of Valor types
    NEXUS("N", "Nexus - Spawn point and market for heroes"),
    PLAIN("P", "Plain - No special effects"),
    BUSH("B", "Bush - Increases dexterity by 10%"),
    CAVE("C", "Cave - Increases agility by 10%"),
    KOULOU("K", "Koulou - Increases strength by 10%"),
    OBSTACLE("O", "Obstacle - Can be removed by hero");

    private final String symbol;
    private final String description;

    TileType(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }
}
