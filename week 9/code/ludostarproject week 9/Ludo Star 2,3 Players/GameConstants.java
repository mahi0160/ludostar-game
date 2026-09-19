/**
 * Shared sizes and Ludo movement limits. No packages — keep these in the same folder.
 */
public final class GameConstants {

    public static final int GRID_SIZE = 15;
    public static final int CELL_SIZE = 42;
    public static final int BOARD_PADDING = 12;

    /** Shared outer track. */
    public static final int PATH_LENGTH = 52;

    /**
     * After leaving the yard, step 0 is the colored start square.
     * Steps 0..50 stay on the outer track. Steps 51..55 are the home
     * lane. Step 56 is the center (finished).
     */
    public static final int LAST_PATH_STEP = 50;
    public static final int HOME_LANE_START = 51;
    public static final int FINISH_STEP = 56;

    public static final int TOKENS_PER_PLAYER = 4;
    public static final int DIE_FACES = 6;
    public static final int MAX_CONSECUTIVE_SIXES = 3;

    private GameConstants() {
    }

    public static int boardPixelSize() {
        return BOARD_PADDING * 2 + GRID_SIZE * CELL_SIZE;
    }
}
