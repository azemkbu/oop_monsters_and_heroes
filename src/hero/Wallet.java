package hero;

/**
 * Represents the wallet/gold amount of each {@link Hero}
 */
public final class Wallet {

    private int gold;

    public Wallet(int initialGold) {
        if (initialGold < 0) {
            throw new IllegalArgumentException("Initial gold must not be negative");
        }
        this.gold = initialGold;
    }

    public int getGold() {
        return gold;
    }

    public void addGold(int amount) {
        if (amount <= 0) {
            return;
        }
        gold += amount;
    }

    public void spendGold(int amount) {
        if (amount <= 0) {
            return;
        }
        if (gold < amount) {
            return;
        }
        gold -= amount;
    }
}
