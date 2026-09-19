import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

/**
 * Draws the 15x15 board and maps token steps to grid cells.
 * Game rules live in {@link GameController}.
 */
public class Board extends JPanel {

    private static final Color GRID_LINE = new Color(70, 70, 70);
    private static final Color START_MARK = new Color(150, 50, 200);

    /**
     * Outer track as {column, row}, clockwise, starting at Red's entry square.
     * Converted from the classic Ludo 52-square layout (Red top-left).
     */
    private static final int[][] OUTER_PATH = {
            {1, 6}, {2, 6}, {3, 6}, {4, 6}, {5, 6},
            {6, 5}, {6, 4}, {6, 3}, {6, 2}, {6, 1}, {6, 0},
            {7, 0},
            {8, 0}, {8, 1}, {8, 2}, {8, 3}, {8, 4}, {8, 5},
            {9, 6}, {10, 6}, {11, 6}, {12, 6}, {13, 6}, {14, 6},
            {14, 7},
            {14, 8}, {13, 8}, {12, 8}, {11, 8}, {10, 8}, {9, 8},
            {8, 9}, {8, 10}, {8, 11}, {8, 12}, {8, 13}, {8, 14},
            {7, 14},
            {6, 14}, {6, 13}, {6, 12}, {6, 11}, {6, 10}, {6, 9},
            {5, 8}, {4, 8}, {3, 8}, {2, 8}, {1, 8}, {0, 8},
            {0, 7}, {0, 6}
    };

    private static final int[][] RED_HOME = {{1, 7}, {2, 7}, {3, 7}, {4, 7}, {5, 7}, {7, 7}};
    private static final int[][] GREEN_HOME = {{7, 1}, {7, 2}, {7, 3}, {7, 4}, {7, 5}, {7, 7}};
    private static final int[][] YELLOW_HOME = {{13, 7}, {12, 7}, {11, 7}, {10, 7}, {9, 7}, {7, 7}};
    private static final int[][] BLUE_HOME = {{7, 13}, {7, 12}, {7, 11}, {7, 10}, {7, 9}, {7, 7}};

    /** Extra star / safe squares (8 steps after each start). */
    private static final int[] STAR_INDEXES = {8, 21, 34, 47};

    private GameController controller;

    public Board() {
        int size = GameConstants.boardPixelSize();
        setPreferredSize(new Dimension(size, size));
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (controller != null) {
                    controller.onBoardClick(e.getX(), e.getY());
                }
            }
        });
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public Position getTokenPosition(Token token) {
        if (token.isInYard()) {
            return yardPosition(token.getColor(), token.getIndex());
        }
        int steps = token.getStepCount();
        if (steps <= GameConstants.LAST_PATH_STEP) {
            int index = (token.getColor().getStartIndex() + steps) % GameConstants.PATH_LENGTH;
            return pathPosition(index);
        }
        int homeIndex = steps - GameConstants.HOME_LANE_START;
        return homePosition(token.getColor(), homeIndex);
    }

    public Position pathPosition(int pathIndex) {
        int[] cell = OUTER_PATH[pathIndex];
        return new Position(cell[0], cell[1]);
    }

    public boolean isSafePathIndex(int pathIndex) {
        for (PlayerColor color : PlayerColor.values()) {
            if (color.getStartIndex() == pathIndex) {
                return true;
            }
        }
        for (int star : STAR_INDEXES) {
            if (star == pathIndex) {
                return true;
            }
        }
        return false;
    }

    /**
     * Global outer-track index for a token that is still on the shared path,
     * or -1 if the token is in the yard, home lane, or finished.
     */
    public int pathIndexOf(Token token) {
        if (!token.isOnPath()) {
            return -1;
        }
        return (token.getColor().getStartIndex() + token.getStepCount()) % GameConstants.PATH_LENGTH;
    }

    public Token tokenAtPixel(int x, int y, Player player) {
        Token[] tokens = player.getTokens();
        Token hit = null;
        int best = Integer.MAX_VALUE;
        for (Token token : tokens) {
            Position pos = getTokenPosition(token);
            int cx = pos.pixelX() + GameConstants.CELL_SIZE / 2;
            int cy = pos.pixelY() + GameConstants.CELL_SIZE / 2;
            int dx = x - cx;
            int dy = y - cy;
            int dist = dx * dx + dy * dy;
            int radius = GameConstants.CELL_SIZE / 2 + 4;
            if (dist <= radius * radius && dist < best) {
                best = dist;
                hit = token;
            }
        }
        return hit;
    }

    private Position yardPosition(PlayerColor color, int tokenIndex) {
        int baseCol = (color == PlayerColor.RED || color == PlayerColor.BLUE) ? 0 : 9;
        int baseRow = (color == PlayerColor.RED || color == PlayerColor.GREEN) ? 0 : 9;
        int[] dx = {1, 3, 1, 3};
        int[] dy = {1, 1, 3, 3};
        return new Position(baseCol + dx[tokenIndex], baseRow + dy[tokenIndex]);
    }

    private Position homePosition(PlayerColor color, int homeIndex) {
        int[][] lane;
        switch (color) {
            case RED:
                lane = RED_HOME;
                break;
            case GREEN:
                lane = GREEN_HOME;
                break;
            case YELLOW:
                lane = YELLOW_HOME;
                break;
            default:
                lane = BLUE_HOME;
                break;
        }
        int clamped = Math.max(0, Math.min(homeIndex, lane.length - 1));
        return new Position(lane[clamped][0], lane[clamped][1]);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g2);
        drawHomeAreas(g2);
        drawLanes(g2);
        drawCenter(g2);
        drawStartsAndStars(g2);

        if (controller != null) {
            drawTokens(g2);
        }
        g2.dispose();
    }

    private void drawGrid(Graphics2D g2) {
        for (int row = 0; row < GameConstants.GRID_SIZE; row++) {
            for (int col = 0; col < GameConstants.GRID_SIZE; col++) {
                boolean track = (col >= 6 && col <= 8) || (row >= 6 && row <= 8);
                if (!track) {
                    continue;
                }
                fillCell(g2, col, row, Color.WHITE);
            }
        }
    }

    private void drawHomeAreas(Graphics2D g2) {
        drawYardBlock(g2, 0, 0, PlayerColor.RED);
        drawYardBlock(g2, 9, 0, PlayerColor.GREEN);
        drawYardBlock(g2, 9, 9, PlayerColor.YELLOW);
        drawYardBlock(g2, 0, 9, PlayerColor.BLUE);
    }

    private void drawYardBlock(Graphics2D g2, int col, int row, PlayerColor color) {
        int x = GameConstants.BOARD_PADDING + col * GameConstants.CELL_SIZE;
        int y = GameConstants.BOARD_PADDING + row * GameConstants.CELL_SIZE;
        int size = 6 * GameConstants.CELL_SIZE;
        g2.setColor(color.getBoardColor());
        g2.fillRoundRect(x, y, size, size, 10, 10);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(x, y, size, size, 10, 10);

        int inset = 18;
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x + inset, y + inset, size - inset * 2, size - inset * 2, 12, 12);
        g2.setColor(GRID_LINE);
        g2.drawRoundRect(x + inset, y + inset, size - inset * 2, size - inset * 2, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        String label = color.getDisplayName().toUpperCase();
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(255, 255, 255, 220));
        g2.drawString(label, x + (size - fm.stringWidth(label)) / 2, y + size - 6);
    }

    private void drawLanes(Graphics2D g2) {
        for (int c = 1; c <= 5; c++) {
            fillCell(g2, c, 7, PlayerColor.RED.getLaneColor());
        }
        for (int r = 1; r <= 5; r++) {
            fillCell(g2, 7, r, PlayerColor.GREEN.getLaneColor());
        }
        for (int c = 9; c <= 13; c++) {
            fillCell(g2, c, 7, PlayerColor.YELLOW.getLaneColor());
        }
        for (int r = 9; r <= 13; r++) {
            fillCell(g2, 7, r, PlayerColor.BLUE.getLaneColor());
        }
    }

    private void drawCenter(Graphics2D g2) {
        int x = GameConstants.BOARD_PADDING + 6 * GameConstants.CELL_SIZE;
        int y = GameConstants.BOARD_PADDING + 6 * GameConstants.CELL_SIZE;
        int s = 3 * GameConstants.CELL_SIZE;
        int midX = x + s / 2;
        int midY = y + s / 2;

        Polygon red = new Polygon(new int[]{x, midX, x}, new int[]{y, midY, y + s}, 3);
        Polygon green = new Polygon(new int[]{x, x + s, midX}, new int[]{y, y, midY}, 3);
        Polygon yellow = new Polygon(new int[]{x + s, x + s, midX}, new int[]{y, y + s, midY}, 3);
        Polygon blue = new Polygon(new int[]{x, x + s, midX}, new int[]{y + s, y + s, midY}, 3);

        g2.setColor(PlayerColor.RED.getBoardColor());
        g2.fillPolygon(red);
        g2.setColor(PlayerColor.GREEN.getBoardColor());
        g2.fillPolygon(green);
        g2.setColor(PlayerColor.YELLOW.getBoardColor());
        g2.fillPolygon(yellow);
        g2.setColor(PlayerColor.BLUE.getBoardColor());
        g2.fillPolygon(blue);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, s, s);
    }

    private void drawStartsAndStars(Graphics2D g2) {
        for (PlayerColor color : PlayerColor.values()) {
            Position start = pathPosition(color.getStartIndex());
            fillCell(g2, start.getCol(), start.getRow(), color.getBoardColor());
            g2.setColor(START_MARK);
            int cx = start.pixelX() + GameConstants.CELL_SIZE / 2;
            int cy = start.pixelY() + GameConstants.CELL_SIZE / 2;
            g2.fillOval(cx - 5, cy - 5, 10, 10);
        }
        for (int star : STAR_INDEXES) {
            Position pos = pathPosition(star);
            drawStar(g2, pos);
        }
    }

    private void drawStar(Graphics2D g2, Position pos) {
        int cx = pos.pixelX() + GameConstants.CELL_SIZE / 2;
        int cy = pos.pixelY() + GameConstants.CELL_SIZE / 2;
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double angle = -Math.PI / 2 + i * Math.PI / 5;
            int r = (i % 2 == 0) ? 11 : 5;
            star.addPoint(cx + (int) (Math.cos(angle) * r), cy + (int) (Math.sin(angle) * r));
        }
        g2.setColor(new Color(90, 90, 90));
        g2.fillPolygon(star);
        g2.setColor(Color.BLACK);
        g2.drawPolygon(star);
    }

    private void drawTokens(Graphics2D g2) {
        Player current = controller.getCurrentPlayer();
        for (Player player : controller.getPlayers()) {
            for (Token token : player.getTokens()) {
                Position pos = getTokenPosition(token);
                boolean highlight = current == player && controller.isWaitingForTokenChoice()
                        && controller.canMoveToken(token, controller.getDie().getLastRoll());
                drawTokenPiece(g2, pos, player.getColor(), token.displayNumber(), highlight);
            }
        }
    }

    private void drawTokenPiece(Graphics2D g2, Position pos, PlayerColor color, int number, boolean highlight) {
        int size = 28;
        int x = pos.pixelX() + (GameConstants.CELL_SIZE - size) / 2;
        int y = pos.pixelY() + (GameConstants.CELL_SIZE - size) / 2;
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillOval(x + 3, y + 4, size, size);
        g2.setColor(color.getBoardColor());
        g2.fillOval(x, y, size, size);
        g2.setStroke(new BasicStroke(highlight ? 3f : 1.6f));
        g2.setColor(highlight ? Color.WHITE : Color.BLACK);
        g2.drawOval(x, y, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        String text = String.valueOf(number);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (size - fm.stringWidth(text)) / 2, y + 19);
    }

    private void fillCell(Graphics2D g2, int col, int row, Color color) {
        int x = GameConstants.BOARD_PADDING + col * GameConstants.CELL_SIZE;
        int y = GameConstants.BOARD_PADDING + row * GameConstants.CELL_SIZE;
        g2.setColor(color);
        g2.fillRect(x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
        g2.setColor(GRID_LINE);
        g2.drawRect(x, y, GameConstants.CELL_SIZE, GameConstants.CELL_SIZE);
    }
}
