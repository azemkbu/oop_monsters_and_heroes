package market.model.item;

/**
 * Concrete subclass of {@link Item} representing a weapon
 */
public class Weapon extends Item {
    private final int damage;
    private final int handsRequired;
    private final int rangeBonus;

    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses, int rangeBonus) {
        super(name, price, level, uses);
        this.damage = damage;
        this.handsRequired = handsRequired;
        this.rangeBonus = rangeBonus;
    }

    // Backward-compatible constructor (defaults rangeBonus to 0)
    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses) {
        this(name, price, level, damage, handsRequired, uses, 0);
    }

    public int getDamage() { return damage; }
    public int getHandsRequired() { return handsRequired; }
    public int getRangeBonus() { return rangeBonus; }

    @Override
    public String getItemType() { return ItemType.WEAPON.name(); }
}
