package lov.usecase.requests;

import hero.Hero;
import lov.usecase.LovActionRequest;

public final class TeleportRequest implements LovActionRequest {
    private final Hero targetHero;

    public TeleportRequest(Hero targetHero) {
        this.targetHero = targetHero;
    }

    public Hero getTargetHero() {
        return targetHero;
    }
}


