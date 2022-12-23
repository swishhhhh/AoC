package aoc22.datastructs;

public enum Direction {
    NORTH("N"), SOUTH("S"), EAST("E"), WEST("W");

    private final String symbol;

    Direction(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "Direction{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
