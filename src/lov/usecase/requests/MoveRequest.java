package lov.usecase.requests;

import lov.usecase.LovActionRequest;
import worldMap.enums.Direction;

public final class MoveRequest implements LovActionRequest {
    private final Direction direction;

    public MoveRequest(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}


