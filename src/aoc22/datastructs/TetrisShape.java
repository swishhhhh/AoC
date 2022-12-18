package aoc22.datastructs;

public class TetrisShape {
    public enum Type {
        PLUS, VERTICAL_LINE, HORIZONTAL_LINE, L, SQUARE
    }

    private final Type type;
    private final int width;
    private final int height;
    private final int[] leftProfile;   //series of offsets (from left edge = 0) for each square along the left edge
    private final int[] rightProfile;  //series of offsets (from left edge = 0) +1 for each square along the right edge
    private final int[] bottomProfile; //series of offsets (from bottom edge = 0) for each square along the bottom edge

    public static TetrisShape newPlus() {
        return new TetrisShape(Type.PLUS, 3, 3,
                new int[]{1, 0, 1}, new int[]{2, 3, 2}, new int[]{1, 0, 1});
    }

    public static TetrisShape newVerticalLine() {
        return new TetrisShape(Type.VERTICAL_LINE, 1, 4,
                new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1}, new int[]{0});
    }

    public static TetrisShape newHorizontalLine() {
        return new TetrisShape(Type.HORIZONTAL_LINE, 4, 1,
                new int[]{0}, new int[]{4}, new int[]{0, 0, 0, 0});
    }

    public static TetrisShape newL() {
        return new TetrisShape(Type.L, 3, 3,
                new int[]{2, 2, 0}, new int[]{3, 3, 3}, new int[]{0, 0, 0});
    }

    public static TetrisShape newSquare() {
        return new TetrisShape(Type.SQUARE, 2, 2,
                new int[]{0, 0}, new int[]{2, 2}, new int[]{0, 0});
    }

    private TetrisShape(Type type, int width, int height, int[] leftProfile, int[] rightProfile, int[] bottomProfile) {
        if (width != bottomProfile.length) {
            throw new IllegalArgumentException(String.format("Width (%s) != BottomProfile (%s)", width, bottomProfile.length));
        }
        if (height != rightProfile.length) {
            throw new IllegalArgumentException(String.format("Height (%s) != RightProfile (%s)", height, rightProfile.length));
        }
        if (height != leftProfile.length) {
            throw new IllegalArgumentException(String.format("Height (%s) != LeftProfile (%s)", height, leftProfile.length));
        }

        this.type = type;
        this.width = width;
        this.height = height;
        this.leftProfile = leftProfile.clone();
        this.rightProfile = rightProfile.clone();
        this.bottomProfile = bottomProfile.clone();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getLeftProfile() {
        return leftProfile.clone();
    }

    public int[] getRightProfile() {
        return rightProfile.clone();
    }

    public int[] getBottomProfile() {
        return bottomProfile.clone();
    }

    @Override
    public TetrisShape clone() {
        return new TetrisShape(type, width, height, leftProfile, rightProfile, bottomProfile);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TetrisShape that = (TetrisShape) o;

        return type == that.type;
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public String toString() {
        return "TetrisShape{" + "type=" + type + '}';
    }

    public String toStringVisual() {
        char[][] bitmap = toBitmap();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < bitmap.length; i++) {
            for (int j = 0; j < bitmap[i].length; j++) {
                sb.append(bitmap[i][j]);
            }
            if (i < bitmap.length - 1) { //don't print new-line after last line
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    public char[][] toBitmap() {
        char[][] bitmap = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                bitmap[i][j] = j < leftProfile[i] || j >= rightProfile[i] ? '.' : '#';
            }
        }
        return bitmap;
    }
}
