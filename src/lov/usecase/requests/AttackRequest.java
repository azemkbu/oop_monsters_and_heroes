package lov.usecase.requests;

import lov.usecase.LovActionRequest;
import monster.Monster;

/**
 * ATTACK request. Hands choice is only relevant for one-handed weapons.
 */
public final class AttackRequest implements LovActionRequest {
    private final Monster target;
    private final Integer handsForOneHandedWeapon;

    public AttackRequest(Monster target, Integer handsForOneHandedWeapon) {
        this.target = target;
        this.handsForOneHandedWeapon = handsForOneHandedWeapon;
    }

    public Monster getTarget() {
        return target;
    }

    public Integer getHandsForOneHandedWeapon() {
        return handsForOneHandedWeapon;
    }
}


