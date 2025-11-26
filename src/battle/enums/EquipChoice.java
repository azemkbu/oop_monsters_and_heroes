package battle.enums;

/**
 * Represents the type of equipment action a {@link hero.Hero} can choose
 **/

public enum EquipChoice {
    WEAPON("Weapon"),
    ARMOR("Armor"),
    CANCEL("Cancel");

    private final String label;

    EquipChoice(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
