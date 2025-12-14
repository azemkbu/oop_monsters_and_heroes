package lov.usecase.requests;

import lov.usecase.LovActionRequest;
import market.model.item.Potion;

public final class UsePotionRequest implements LovActionRequest {
    private final Potion potion;

    public UsePotionRequest(Potion potion) {
        this.potion = potion;
    }

    public Potion getPotion() {
        return potion;
    }
}


