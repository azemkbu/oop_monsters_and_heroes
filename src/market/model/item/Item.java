package market.model.item;


/**
 * Abstract class representing an item that can be purchased, used or equipped by heroes in the game
 */
public abstract class Item {
    protected String name;
    protected int price;
    protected int level;
    protected int usesRemaining;

    public Item(String name, int price, int level, int uses) {
        this.name = name;
        this.price = price;
        this.level = level;
        this.usesRemaining = uses;
    }

    public abstract String getItemType();

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getLevel() { return level; }
    public int getUsesRemaining() { return usesRemaining; }

    public boolean isUsable() { return usesRemaining > 0; }

    public void consumeUse() {
        usesRemaining--;
        if (usesRemaining < 0) usesRemaining = 0;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Price: %d | Level: %d | Uses left: %d",
                name, getItemType(), price, level, usesRemaining);
    }
}
