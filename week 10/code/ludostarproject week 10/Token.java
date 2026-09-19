public class Token {

    private final PlayerColor color;
    private final int index;
    /** -1 = yard, 0..56 = on board / home / finished. */
    private int stepCount = -1;

    public Token(PlayerColor color, int index) {
        this.color = color;
        this.index = index;
    }

    public PlayerColor getColor() {
        return color;
    }

    public int getIndex() {
        return index;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public boolean isInYard() {
        return stepCount == -1;
    }

    public boolean isOnPath() {
        return stepCount >= 0 && stepCount <= GameConstants.LAST_PATH_STEP;
    }

    public boolean isInHomeLane() {
        return stepCount >= GameConstants.HOME_LANE_START
                && stepCount < GameConstants.FINISH_STEP;
    }

    public boolean isFinished() {
        return stepCount == GameConstants.FINISH_STEP;
    }

    public void enterBoard() {
        stepCount = 0;
    }

    public void reset() {
        stepCount = -1;
    }

    public int displayNumber() {
        return index + 1;
    }
}
