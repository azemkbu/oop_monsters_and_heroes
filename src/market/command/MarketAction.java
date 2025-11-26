package market.command;

/**
 * Represents the available actions {@link hero.Hero} can perform in the market
 */
public enum MarketAction {
    BUY(1, "Buy items"),
    SELL(2, "Sell items"),
    VIEW_INFO(3, "View hero info & inventory"),
    LEAVE(4, "Leave market");

    private final int code;
    private final String description;

    MarketAction(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public static MarketAction fromCode(int code) {
        for (MarketAction action : values()) {
            if (action.code == code) {
                return action;
            }
        }
        return null;
    }

    public String getActionLine() {
        return String.format("  [%d] %s", code, description);
    }
}
