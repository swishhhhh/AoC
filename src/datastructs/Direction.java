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
        return this.turn180().equals(otherDirection);
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
