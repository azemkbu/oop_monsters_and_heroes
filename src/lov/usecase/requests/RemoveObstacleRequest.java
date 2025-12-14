package lov.usecase.requests;

import lov.usecase.LovActionRequest;
import worldMap.enums.Direction;

public final class RemoveObstacleRequest implements LovActionRequest {
    private final Direction direction;

    public RemoveObstacleRequest(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}


