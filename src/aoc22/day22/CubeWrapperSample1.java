package aoc22.day22;

import static aoc22.datastructs.Direction.*;
import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;

/**
 * Pattern:
 *       |U|
 *   |B|L|F|
 *       |D|R|
 */
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
                    targetCol = originBackSide.x() + DIMENSIONS - relativeCol - 1; //invert
                    targetRow = originUpSide.y(); //0 offset
                    newDirection = SOUTH;
                }
                case WEST -> { //target side = LEFT
                    //swap relative rows and cols
                    targetCol = originLeftSide.x() + relativeRow;
                    targetRow = originLeftSide.y(); //0 offset
                    newDirection = SOUTH;
                }
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.x() + DIMENSIONS - 1;
                    targetRow = originRightSide.y() + DIMENSIONS - relativeRow - 1;
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
                case NORTH -> { //target side = UP
                    targetCol = originUpSide.x() + DIMENSIONS - relativeCol - 1; //invert
                    targetRow = originUpSide.y(); //0 offset
                    newDirection = SOUTH;
                }
                case WEST -> { //target side = RIGHT
                    targetCol = originRightSide.x() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originRightSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case SOUTH -> { //target side = DOWN
                    targetCol = originDownSide.x() + DIMENSIONS - relativeCol -1; //invert
                    targetRow = originDownSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is LEFT
        else if (isInSectorBoundaries(this.getCursor(), originLeftSide)) {
            relativeCol = cursorCol - originLeftSide.x();
            switch (this.getDirection()) {
                case NORTH -> { //target side = UP
                    targetCol = originUpSide.x(); //0 offset
                    targetRow = originUpSide.y() + relativeCol;
                    newDirection = EAST;
                }
                case SOUTH -> { //target side = DOWN
                    targetCol = originDownSide.x(); //0 offset
                    targetRow = originDownSide.y() + DIMENSIONS - relativeCol -1; //invert
                    newDirection = EAST;
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
                    targetCol = originRightSide.x() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originRightSide.y(); //0 offset
                    newDirection = SOUTH;
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
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.x() + DIMENSIONS - relativeRow - 1; //invert
                    targetRow = originLeftSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.x() + DIMENSIONS - relativeCol -1; //invert
                    targetRow = originBackSide.y() + DIMENSIONS - 1;
                    newDirection = NORTH;
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
                case NORTH -> { //target side = FRONT
                    targetCol = originFrontSide.x() + DIMENSIONS - 1;
                    targetRow = originFrontSide.y() + DIMENSIONS - relativeCol - 1; //invert
                    newDirection = WEST;
                }
                case EAST -> { //target side = UP
                    targetCol = originUpSide.x() + DIMENSIONS - 1;
                    targetRow = originUpSide.y() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.x(); //0 offset
                    targetRow = originBackSide.y() + DIMENSIONS - relativeCol - 1;
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
        return     checkCoord.y() >= targetOrigin.y()
                && checkCoord.y() < (targetOrigin.y() + DIMENSIONS)
                && checkCoord.x() >= targetOrigin.x()
                && checkCoord.x() < (targetOrigin.x() + DIMENSIONS);
    }
}
