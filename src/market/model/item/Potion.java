package market.model.item;

/**
 * Concrete subclass of {@link Item} representing a potion
 */

public class Potion extends Item {

    private final int effectAmount;
    private final StatType statType;

    public Potion(String name, int price, int level, int effectAmount, StatType statType) {
        super(name, price, level, 1);
        this.effectAmount = effectAmount;
        this.statType = statType;
    }

    public int getEffectAmount() { return effectAmount; }
    public StatType getStatType() { return statType; }

    @Override
    public String getItemType() { return ItemType.POTION.name(); }
}
