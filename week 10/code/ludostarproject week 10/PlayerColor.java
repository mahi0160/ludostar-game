import java.awt.Color;

public enum PlayerColor {
    RED("Red", 0, new Color(220, 36, 36), new Color(250, 120, 120)),
    GREEN("Green", 13, new Color(20, 150, 55), new Color(110, 215, 130)),
    YELLOW("Yellow", 26, new Color(240, 200, 20), new Color(255, 230, 110)),
    BLUE("Blue", 39, new Color(30, 120, 220), new Color(110, 175, 255));

    private final String displayName;
    private final int startIndex;
    private final Color boardColor;
    private final Color laneColor;

    PlayerColor(String displayName, int startIndex, Color boardColor, Color laneColor) {
        this.displayName = displayName;
        this.startIndex = startIndex;
        this.boardColor = boardColor;
        this.laneColor = laneColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public Color getBoardColor() {
        return boardColor;
    }

    public Color getLaneColor() {
        return laneColor;
    }

    public PlayerColor next() {
        PlayerColor[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
