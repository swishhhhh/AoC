package aoc22.day22;

import static aoc22.datastructs.Direction.*;
import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;

public class CubeWrapperSample1 extends CubeWrapper {
    public static final int DIMENSIONS = 4;
    public CubeWrapperSample1(Coordinates cursor, Direction direction) {
        super(cursor, direction);
    }

    private static final Coordinates originUpSide = new Coordinates(2 * DIMENSIONS, 0);
    private static final Coordinates originBackSide = new Coordinates(0, DIMENSIONS);
    private static final Coordinates originLeftSide = new Coordinates(DIMENSIONS, DIMENSIONS);
    private static final Coordinates originFrontSide = new Coordinates(2 * DIMENSIONS, DIMENSIONS);
    private static final Coordinates originDownSide = new Coordinates(2 * DIMENSIONS, 2 * DIMENSIONS);
    private static final Coordinates originRightSide = new Coordinates(3 * DIMENSIONS, 2 * DIMENSIONS);

    @Override
    public void wrap() {
        int cursorRow = this.getCursor().getY();
        int cursorCol = this.getCursor().getX();

        //get relative row and cols in the source side
        int targetCol = -1, targetRow = -1, relativeCol, relativeRow;
        Direction newDirection = null;

        //source side = UP
        if (isInSectorBoundaries(this.getCursor(), originUpSide)) {
            relativeCol = cursorCol - originUpSide.getX();
            relativeRow = cursorRow - originUpSide.getY();
            switch (this.getDirection()) {
                case NORTH -> { //target side = BACK
                    targetCol = originBackSide.getX() + DIMENSIONS - relativeCol - 1; //invert
                    targetRow = originUpSide.getY(); //0 offset
                    newDirection = SOUTH;
                }
                case WEST -> { //target side = LEFT
                    //swap relative rows and cols
                    targetCol = originLeftSide.getX() + relativeRow;
                    targetRow = originLeftSide.getY(); //0 offset
                    newDirection = SOUTH;
                }
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.getX() + DIMENSIONS - 1;
                    targetRow = originRightSide.getY() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is BACK
        else if (isInSectorBoundaries(this.getCursor(), originBackSide)) {
            relativeCol = cursorCol - originBackSide.getX();
            relativeRow = cursorRow - originBackSide.getY();
            switch (this.getDirection()) {
                case NORTH -> { //target side = UP
                    targetCol = originUpSide.getX() + DIMENSIONS - relativeCol - 1; //invert
                    targetRow = originUpSide.getY(); //0 offset
                    newDirection = SOUTH;
                }
                case WEST -> { //target side = RIGHT
                    targetCol = originRightSide.getX() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originRightSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case SOUTH -> { //target side = DOWN
                    targetCol = originDownSide.getX() + DIMENSIONS - relativeCol -1; //invert
                    targetRow = originDownSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is LEFT
        else if (isInSectorBoundaries(this.getCursor(), originLeftSide)) {
            relativeCol = cursorCol - originLeftSide.getX();
            switch (this.getDirection()) {
                case NORTH -> { //target side = UP
                    targetCol = originUpSide.getX(); //0 offset
                    targetRow = originUpSide.getY() + relativeCol;
                    newDirection = EAST;
                }
                case SOUTH -> { //target side = DOWN
                    targetCol = originDownSide.getX(); //0 offset
                    targetRow = originDownSide.getY() + DIMENSIONS - relativeCol -1; //invert
                    newDirection = EAST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is FRONT
        if (isInSectorBoundaries(this.getCursor(), originFrontSide)) {
            relativeRow = cursorRow - originFrontSide.getY();
            switch (this.getDirection()) {
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.getX() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originRightSide.getY(); //0 offset
                    newDirection = SOUTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is DOWN
        else if (isInSectorBoundaries(this.getCursor(), originDownSide)) {
            relativeCol = cursorCol - originDownSide.getX();
            relativeRow = cursorRow - originDownSide.getY();
            switch (this.getDirection()) {
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.getX() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originLeftSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.getX() + DIMENSIONS - relativeCol -1; //invert
                    targetRow = originBackSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is RIGHT
        else if (isInSectorBoundaries(this.getCursor(), originRightSide)) {
            relativeCol = cursorCol - originRightSide.getX();
            relativeRow = cursorRow - originRightSide.getY();
            switch (this.getDirection()) {
                case NORTH -> { //target side = FRONT
                    targetCol = originFrontSide.getX() + DIMENSIONS - 1;
                    targetRow = originFrontSide.getY() + DIMENSIONS - relativeCol - 1; //invert
                    newDirection = WEST;
                }
                case EAST -> { //target side = UP
                    targetCol = originUpSide.getX() + DIMENSIONS - 1;
                    targetRow = originUpSide.getY() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.getX(); //0 offset
                    targetRow = originBackSide.getY() + DIMENSIONS - relativeCol - 1;
                    newDirection = EAST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        this.setCursor(new Coordinates(targetCol, targetRow));
        this.setDirection(newDirection);
    }

    private boolean isInSectorBoundaries(Coordinates checkCoord, Coordinates targetOrigin) {
        return     checkCoord.getY() >= targetOrigin.getY()
                && checkCoord.getY() < (targetOrigin.getY() + DIMENSIONS)
                && checkCoord.getX() >= targetOrigin.getX()
                && checkCoord.getX() < (targetOrigin.getX() + DIMENSIONS);
    }
}
