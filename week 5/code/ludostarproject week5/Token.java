public class Token {

    // -1 means the token is still in its home area.
    // 0-47 are positions on the shared movement path.
    private int position = -1;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Moves the token using the player's own starting point on the board.
     * The first move starts from the player's designated path square.
     */
    public void move(int steps, int startPosition) {
        if (steps < 1) {
            return;
        }

        if (position == -1) {
            position = (startPosition + steps - 1) % 48;
        } else {
            position = (position + steps) % 48;
        }
    }

    public void reset() {
        position = -1;
    }
}
