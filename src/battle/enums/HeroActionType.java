package battle.enums;

/**
 * Represents a type of action a {@link hero.Hero} can perform during battle
 **/

public enum HeroActionType {
    ATTACK("Attack"),
    CAST_SPELL("Cast Spell"),
    USE_POTION("Use Potion"),
    EQUIP("Equip Weapon/Armor"),
    SKIP("Skip turn"),
    MOVE("Move"),
    TELEPORT("Teleport"),
    RECALL("Recall"),
    REMOVE_OBSTACLE("Remove Obstacle");

    private final String label;

    HeroActionType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
