package aoc22.day22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;

public abstract class CubeWrapper {
    private Coordinates cursor;
    private Direction direction;

    public CubeWrapper(Coordinates cursor, Direction direction) {
        this.cursor = cursor;
        this.direction = direction;
    }

    public Coordinates getCursor() {
        return cursor;
    }
    public Direction getDirection() {
        return direction;
    }
    public void setCursor(Coordinates cursor) {
        this.cursor = cursor;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public abstract void wrap();
}
