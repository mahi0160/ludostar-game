public class Player {

    private final String name;
    private final PlayerColor color;
    private final Token[] tokens;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.tokens = new Token[GameConstants.TOKENS_PER_PLAYER];
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = new Token(color, i);
        }
    }

    public String getName() {
        return name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public Token[] getTokens() {
        return tokens;
    }

    public Token getToken(int index) {
        return tokens[index];
    }

    public int getStartPosition() {
        return color.getStartIndex();
    }

    public void resetTokens() {
        for (Token token : tokens) {
            token.reset();
        }
    }

    public boolean hasWon() {
        for (Token token : tokens) {
            if (!token.isFinished()) {
                return false;
            }
        }
        return true;
    }

    public int finishedCount() {
        int count = 0;
        for (Token token : tokens) {
            if (token.isFinished()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return name + " (" + color.getDisplayName() + ")";
    }
}
