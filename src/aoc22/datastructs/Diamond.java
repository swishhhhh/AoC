package aoc22.datastructs;

/**
 *   For a diamond with a radius of 5 the center coordinate is at 4,4
 *        e.g.: Diamond d = new Diamond(4, 4, 5)
 * <p>
 *      0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
 *   |---------------------------------------
 *  0|                 4,0
 *  1|             3,1|4,1|5,1
 *  2|         2,2|3,2|4,2|5,2|6,2
 *  3|     1,3|2,3|3,3|4,3|5,3|6,3|7,3
 *  4| 0,4|1,4|2,4|3,4|4x4|5,4|6,4|7,4|8,4
 *  5|     1,5|2,5|3,5|4,5|5,5|6,5|7,5|
 *  6|         2,6|3,6|4,6|5,6|6,6
 *  7|             3,7|4,7|5,7
 *  8|                 4,8
 *  ------------------------------------------
 * <p>
 *  To calculate whether a point on a plane falls within the boundaries
 *  of the diamond, the following formula should evaluate to true:
 *      centerX - radius + abs(centerY - y) < x < centerX + radius - abs(centerY - y)
 *        AND
 *      centerY - radius + abs(centerX - x) < y < centerY + radius - abs(centerX - x)
 * <p>
 *  where x and y are the coordinates of the point evaluated.
 */
public class Diamond {
    private final int centerX; //x coordinate of the center square
    private final int centerY; //y coordinate of the center square
    private final int radius;

    public Diamond(int centerX, int centerY, int radius) {
        if (radius < 1) {
            throw new IllegalArgumentException(String.format("radius %s must be >= 1", radius));
        }
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public boolean containsPoint(int x, int y) {
        return
            x > centerX - radius + Math.abs(centerY - y) &&
            x < centerX + radius - Math.abs(centerY - y) &&
            y > centerY - radius + Math.abs(centerX - x) &&
            y < centerY + radius - Math.abs(centerX - x);
    }

    /**
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return number of steps to the right of x (at height y) that you can move a
     *    and still stay in the diamond. If the coordinate (x, y) is already outside
     *    the boundaries of the diamond (before any steps were taken), return -1
     */
    public int getNumStepsToRightStillInbound(int x, int y) {
        if (!this.containsPoint(x, y)) {
            return -1;
        }

        //figure out the rightX (last/rightmost position of x) of the diamond at this level of y..
        int rightX = centerX + radius - 1 - (Math.abs(centerY - y));
        return x > rightX ? -1 : rightX - x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Diamond diamond = (Diamond) o;

        if (centerX != diamond.centerX) return false;
        if (centerY != diamond.centerY) return false;
        return radius == diamond.radius;
    }

    @Override
    public int hashCode() {
        int result = centerX;
        result = 31 * result + centerY;
        result = 31 * result + radius;
        return result;
    }

    @Override
    public String toString() {
        return "Diamond{" +
                "centerX=" + centerX +
                ", centerY=" + centerY +
                ", radius=" + radius +
                '}';
    }
}
