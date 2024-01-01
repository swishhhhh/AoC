package aoc22.day22;

import datastructs.Coordinates;
import datastructs.Direction;

import static datastructs.Direction.*;

/**
 * Pattern:
 *     |U|R|
 *     |F|
 *   |L|D|
 *   |B|
 */
public class CubeWrapperMyInput extends CubeWrapper {
    public static final int DIMENSIONS = 50;
    public CubeWrapperMyInput(Coordinates cursor, Direction direction) {
        super(cursor, direction);
    }

    private static final Coordinates originUpSide = new Coordinates(DIMENSIONS, 0);
    private static final Coordinates originRightSide = new Coordinates(2 * DIMENSIONS, 0);
    private static final Coordinates originFrontSide = new Coordinates(DIMENSIONS, DIMENSIONS);
    private static final Coordinates originLeftSide = new Coordinates(0, 2 * DIMENSIONS);
    private static final Coordinates originDownSide = new Coordinates(DIMENSIONS, 2 * DIMENSIONS);
    private static final Coordinates originBackSide = new Coordinates(0, 3 * DIMENSIONS);

    @Override
    public void wrap() {
        int cursorRow = this.getCursor().y();
        int cursorCol = this.getCursor().x();

        //get relative row and cols in the source side
        int targetCol = -1, targetRow = -1, relativeCol, relativeRow;
        Direction newDirection = null;

        //source side = UP
        if (isInSectorBoundaries(this.getCursor(), originUpSide)) {
            relativeCol = cursorCol - originUpSide.x();
            relativeRow = cursorRow - originUpSide.y();
            switch (this.getDirection()) {
                case NORTH -> { //target side = BACK
                    targetCol = originBackSide.x(); //0 offset
                    targetRow = originBackSide.y() + relativeCol;
                    newDirection = EAST;
                }
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.x(); //0 offset
                    targetRow = originLeftSide.y() + DIMENSIONS - relativeRow - 1; //0 offset
                    newDirection = EAST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is RIGHT
        else if (isInSectorBoundaries(this.getCursor(), originRightSide)) {
            relativeCol = cursorCol - originRightSide.x();
            relativeRow = cursorRow - originRightSide.y();
            switch (this.getDirection()) {
                case NORTH -> { //target side = BACK
                    targetCol = originBackSide.x() + relativeCol;
                    targetRow = originBackSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH; //same as before
                }
                case EAST -> { //target side = DOWN
                    targetCol = originDownSide.x() + DIMENSIONS - 1;
                    targetRow = originDownSide.y() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = FRONT
                    targetCol = originFrontSide.x() + DIMENSIONS -1;
                    targetRow = originFrontSide.y() + relativeCol;
                    newDirection = WEST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is FRONT
        if (isInSectorBoundaries(this.getCursor(), originFrontSide)) {
            relativeRow = cursorRow - originFrontSide.y();
            switch (this.getDirection()) {
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.x() + relativeRow;
                    targetRow = originRightSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.x() + relativeRow;
                    targetRow = originLeftSide.y(); //0 offset
                    newDirection = SOUTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is LEFT
        else if (isInSectorBoundaries(this.getCursor(), originLeftSide)) {
            relativeCol = cursorCol - originLeftSide.x();
            relativeRow = cursorRow - originLeftSide.y();
            switch (this.getDirection()) {
                case NORTH -> { //target side = FRONT
                    targetCol = originFrontSide.x(); //0 offset
                    targetRow = originFrontSide.y() + relativeCol;
                    newDirection = EAST;
                }
                case WEST -> { //target side = UP
                    targetCol = originUpSide.x(); //0 offset
                    targetRow = originUpSide.y() + DIMENSIONS - relativeRow - 1; //invert
                    newDirection = EAST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is DOWN
        else if (isInSectorBoundaries(this.getCursor(), originDownSide)) {
            relativeCol = cursorCol - originDownSide.x();
            relativeRow = cursorRow - originDownSide.y();
            switch (this.getDirection()) {
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.x() + DIMENSIONS - 1;
                    targetRow = originRightSide.y() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.x() + DIMENSIONS -1;
                    targetRow = originBackSide.y() + relativeCol;
                    newDirection = WEST;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //Source side is BACK
        else if (isInSectorBoundaries(this.getCursor(), originBackSide)) {
            relativeCol = cursorCol - originBackSide.x();
            relativeRow = cursorRow - originBackSide.y();
            switch (this.getDirection()) {
                case EAST -> { //target side = DOWN
                    targetCol = originDownSide.x() + relativeRow;
                    targetRow = originDownSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case WEST -> { //target side = UP
                    targetCol = originUpSide.x() + relativeRow;
                    targetRow = originUpSide.y();
                    newDirection = SOUTH;
                }
                case SOUTH -> { //target side = RIGHT
                    targetCol = originRightSide.x() + relativeCol;
                    targetRow = originRightSide.y();
                    newDirection = SOUTH;
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
        return     checkCoord.y() >= targetOrigin.y()
                && checkCoord.y() < (targetOrigin.y() + DIMENSIONS)
                && checkCoord.x() >= targetOrigin.x()
                && checkCoord.x() < (targetOrigin.x() + DIMENSIONS);
    }
}
