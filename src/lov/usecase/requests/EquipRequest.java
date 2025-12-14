package lov.usecase.requests;

import battle.enums.EquipChoice;
import lov.usecase.LovActionRequest;
import market.model.item.Armor;
import market.model.item.Weapon;

public final class EquipRequest implements LovActionRequest {
    private final EquipChoice choice;
    private final Weapon weapon;
    private final Armor armor;

    public EquipRequest(EquipChoice choice, Weapon weapon, Armor armor) {
        this.choice = choice;
        this.weapon = weapon;
        this.armor = armor;
    }

    public EquipChoice getChoice() {
        return choice;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Armor getArmor() {
        return armor;
    }
}


