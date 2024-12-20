package datastructs;

public enum Direction {
    NORTH("N", -1, 0), SOUTH("S", 1, 0), EAST("E", 0, 1), WEST("W", 0, -1),
    NE("NE", -1, 1), SE("SE", 1, 1), NW("NW", -1, -1), SW("SW", 1, -1);

    private final String symbol;
    private final int deltaRow;
    private final int deltaCol;

    public String getSymbol() {
        return symbol;
    }
    public int getDeltaRow() {
        return deltaRow;
    }
    public int getDeltaCol() {
        return deltaCol;
    }

    Direction(String symbol, int deltaRow, int deltaCol) {
        this.symbol = symbol;
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
    }

    public boolean isReverseOf(Direction otherDirection) {
        boolean answer = false;
        switch (otherDirection) {
            case NORTH -> answer = this.equals(SOUTH);
            case SOUTH -> answer = this.equals(NORTH);
            case EAST -> answer = this.equals(WEST);
            case WEST -> answer = this.equals(EAST);
            case NE -> answer = this.equals(SW);
            case SW -> answer = this.equals(NE);
            case NW -> answer = this.equals(SE);
            case SE -> answer = this.equals(NW);
        }
        return answer;
    }

    public Direction turn180() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case EAST -> WEST;
            case WEST -> EAST;
            case NE -> SW;
            case SW -> NE;
            case NW -> SE;
            case SE -> NW;
        };
    }

    @Override
    public String toString() {
        return "Direction{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
