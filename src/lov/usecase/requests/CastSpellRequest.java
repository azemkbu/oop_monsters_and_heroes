package lov.usecase.requests;

import lov.usecase.LovActionRequest;
import market.model.item.Spell;
import monster.Monster;

public final class CastSpellRequest implements LovActionRequest {
    private final Spell spell;
    private final Monster target;

    public CastSpellRequest(Spell spell, Monster target) {
        this.spell = spell;
        this.target = target;
    }

    public Spell getSpell() {
        return spell;
    }

    public Monster getTarget() {
        return target;
    }
}


