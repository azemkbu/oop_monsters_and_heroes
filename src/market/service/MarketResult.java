package market.service;

/**
 * Result of a market operation. Service layer returns this instead of printing.
 */
public final class MarketResult {
    private final boolean success;
    private final String message;
    private final Severity severity;

    public enum Severity {
        SUCCESS,
        WARNING,
        FAIL
    }

    public MarketResult(boolean success, Severity severity, String message) {
        this.success = success;
        this.severity = severity;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public static MarketResult success(String message) {
        return new MarketResult(true, Severity.SUCCESS, message);
    }

    public static MarketResult warning(String message) {
        return new MarketResult(false, Severity.WARNING, message);
    }

    public static MarketResult fail(String message) {
        return new MarketResult(false, Severity.FAIL, message);
    }
}


