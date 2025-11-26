package market.model.item;

/**
 * Concrete subclass of {@link Item} representing a spell
 */
public class Spell extends Item {
    private final int damage;
    private final int manaCost;
    private final SpellType type;

    public Spell(String name, int price, int level, int damage, int manaCost, SpellType type) {
        super(name, price, level, 1);
        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    public int getDamage() { return damage; }
    public int getManaCost() { return manaCost; }
    public SpellType getType() { return type; }

    @Override
    public String getItemType() { return ItemType.SPELL.name(); }
}
