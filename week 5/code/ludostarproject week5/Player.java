public class Player {

    private final String name;
    private final String color;
    private final Token[] tokens;
    private final int startPosition;

    public Player(String name, String color, int startPosition) {
        this.name = name;
        this.color = color;
        this.startPosition = startPosition;
        this.tokens = new Token[4];

        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = new Token();
        }
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Token[] getTokens() {
        return tokens;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void resetTokens() {
        for (Token token : tokens) {
            token.reset();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
