package market.model.item;

/**
 * Concrete subclass of {@link Item} representing a weapon
 */
public class Weapon extends Item {
    private final int damage;
    private final int handsRequired;

    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses) {
        super(name, price, level, uses);
        this.damage = damage;
        this.handsRequired = handsRequired;
    }

    public int getDamage() { return damage; }
    public int getHandsRequired() { return handsRequired; }

    @Override
    public String getItemType() { return ItemType.WEAPON.name(); }
}
