package aoc23.datastructs;

public enum Direction {
    NORTH("N"), SOUTH("S"), EAST("E"), WEST("W"),
    NE("NE"), SE("SE"), NW("NW"), SW("SW");

    private final String symbol;

    public String getSymbol() {
        return symbol;
    }

    Direction(String symbol) {
        this.symbol = symbol;
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

    @Override
    public String toString() {
        return "Direction{" +
                "symbol='" + symbol + '\'' +
                '}';
    }
}
