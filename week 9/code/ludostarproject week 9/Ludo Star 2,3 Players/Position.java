/**
 * A cell on the 15x15 Ludo grid. Column grows right, row grows down.
 */
public final class Position {

    private final int col;
    private final int row;

    public Position(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int pixelX() {
        return GameConstants.BOARD_PADDING + col * GameConstants.CELL_SIZE;
    }

    public int pixelY() {
        return GameConstants.BOARD_PADDING + row * GameConstants.CELL_SIZE;
    }

    public boolean sameCell(Position other) {
        return other != null && col == other.col && row == other.row;
    }
}
