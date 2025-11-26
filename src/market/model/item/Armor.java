package market.model.item;

/**
 * Concrete subclass of {@link Item} representing an armor
 */
public class Armor extends Item {
    private final int damageReduction;

    public Armor(String name, int price, int level, int damageReduction, int uses) {
        super(name, price, level, uses);
        this.damageReduction = damageReduction;
    }

    public int getDamageReduction() { return damageReduction; }

    @Override
    public String getItemType() {
        return ItemType.ARMOR.name();
    }
}
