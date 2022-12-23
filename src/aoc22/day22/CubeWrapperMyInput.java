package aoc22.day22;

import aoc22.datastructs.Coordinates;
import aoc22.datastructs.Direction;

import static aoc22.datastructs.Direction.*;

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
                    targetCol = originBackSide.getX(); //0 offset
                    targetRow = originBackSide.getY() + relativeCol;
                    newDirection = EAST;
                }
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.getX(); //0 offset
                    targetRow = originLeftSide.getY() + DIMENSIONS - relativeRow - 1; //0 offset
                    newDirection = EAST;
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
                case NORTH -> { //target side = BACK
                    targetCol = originBackSide.getX() + relativeCol;
                    targetRow = originBackSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH; //same as before
                }
                case EAST -> { //target side = DOWN
                    targetCol = originDownSide.getX() + DIMENSIONS - 1;
                    targetRow = originDownSide.getY() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = FRONT
                    targetCol = originFrontSide.getX() + DIMENSIONS -1;
                    targetRow = originFrontSide.getY() + relativeCol;
                    newDirection = WEST;
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
                    targetCol = originRightSide.getX() + relativeRow;
                    targetRow = originRightSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case WEST -> { //target side = LEFT
                    targetCol = originLeftSide.getX() + relativeRow;
                    targetRow = originLeftSide.getY(); //0 offset
                    newDirection = SOUTH;
                }
            }
            if (newDirection == null) {
                throw new IllegalStateException("Houston..., not expecting direction " + this.getDirection());
            }
        }

        //source side is LEFT
        else if (isInSectorBoundaries(this.getCursor(), originLeftSide)) {
            relativeCol = cursorCol - originLeftSide.getX();
            relativeRow = cursorRow - originLeftSide.getY();
            switch (this.getDirection()) {
                case NORTH -> { //target side = FRONT
                    targetCol = originFrontSide.getX(); //0 offset
                    targetRow = originFrontSide.getY() + relativeCol;
                    newDirection = EAST;
                }
                case WEST -> { //target side = UP
                    targetCol = originUpSide.getX(); //0 offset
                    targetRow = originUpSide.getY() + DIMENSIONS - relativeRow - 1; //invert
                    newDirection = EAST;
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
                case EAST -> { //target side = RIGHT
                    targetCol = originRightSide.getX() + DIMENSIONS - 1;
                    targetRow = originRightSide.getY() + DIMENSIONS - relativeRow - 1;
                    newDirection = WEST;
                }
                case SOUTH -> { //target side = BACK
                    targetCol = originBackSide.getX() + DIMENSIONS -1;
                    targetRow = originBackSide.getY() + relativeCol;
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
                case EAST -> { //target side = DOWN
                    targetCol = originDownSide.getX() + relativeRow;
                    targetRow = originDownSide.getY() + DIMENSIONS - 1;
                    newDirection = NORTH;
                }
                case WEST -> { //target side = UP
                    targetCol = originUpSide.getX() + relativeRow;
                    targetRow = originUpSide.getY();
                    newDirection = SOUTH;
                }
                case SOUTH -> { //target side = RIGHT
                    targetCol = originRightSide.getX() + relativeCol;
                    targetRow = originRightSide.getY();
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
        return     checkCoord.getY() >= targetOrigin.getY()
                && checkCoord.getY() < (targetOrigin.getY() + DIMENSIONS)
                && checkCoord.getX() >= targetOrigin.getX()
                && checkCoord.getX() < (targetOrigin.getX() + DIMENSIONS);
    }
}
