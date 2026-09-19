import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {

    private static final int PATH_SIZE = 48;

    private final JLabel statusLabel;
    private final JButton rollDiceButton;
    private final JButton newGameButton;
    private final JButton exitButton;

    private final Player[] players;
    private final Dice dice;

    private int currentPlayerIndex = 0;
    private int currentTokenIndex = 0;
    private int lastDiceValue = 0;

    private JPanel boardPanel;

    public Board() {
        players = new Player[] {
                // Each color has its own designated starting square on the path.
                new Player("Player 1", "Red", 42),
                new Player("Player 2", "Green", 6),
                new Player("Player 3", "Blue", 30),
                new Player("Player 4", "Yellow", 18)
        };

        dice = new Dice();

        setLayout(new BorderLayout());

        JLabel title = new JLabel("LUDO STAR GAME", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        boardPanel = createBoardPanel();
        boardPanel.setPreferredSize(new Dimension(550, 560));
        add(boardPanel, BorderLayout.CENTER);

        rollDiceButton = new JButton("Roll Dice");
        newGameButton = new JButton("New Game");
        exitButton = new JButton("Exit");

        rollDiceButton.addActionListener(this);
        newGameButton.addActionListener(this);
        exitButton.addActionListener(this);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rollDiceButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);

        statusLabel = new JLabel("Current Turn: Player 1");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createBoardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                drawBoard(g2);
                drawAllTokens(g2);
            }
        };
    }

    private void drawBoard(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Four home areas
        drawHome(g2, 40, 40, Color.RED);
        drawHome(g2, 320, 40, Color.GREEN);
        drawHome(g2, 40, 320, Color.BLUE);
        drawHome(g2, 320, 320, Color.YELLOW);

        // Center home
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(220, 220, 100, 100);
        g2.setColor(Color.BLACK);
        g2.drawRect(220, 220, 100, 100);
        g2.drawString("HOME", 250, 275);

        // Movement path
        for (int i = 0; i < 6; i++) {
            drawPathSquare(g2, 220, 40 + i * 30);
            drawPathSquare(g2, 290, 40 + i * 30);

            drawPathSquare(g2, 220, 320 + i * 30);
            drawPathSquare(g2, 290, 320 + i * 30);

            drawPathSquare(g2, 40 + i * 30, 220);
            drawPathSquare(g2, 40 + i * 30, 290);

            drawPathSquare(g2, 320 + i * 30, 220);
            drawPathSquare(g2, 320 + i * 30, 290);
        }

        g2.setColor(Color.BLACK);
        g2.drawString("RED", 110, 30);
        g2.drawString("GREEN", 385, 30);
        g2.drawString("BLUE", 110, 530);
        g2.drawString("YELLOW", 380, 530);

        g2.drawString("48-position movement path", 205, 555);
    }

    private void drawHome(Graphics2D g2, int x, int y, Color color) {
        g2.setColor(color);
        g2.fillRect(x, y, 180, 180);

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, 180, 180);

        // Four token starting spaces
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 30, y + 30, 30, 30);
        g2.fillOval(x + 120, y + 30, 30, 30);
        g2.fillOval(x + 30, y + 120, 30, 30);
        g2.fillOval(x + 120, y + 120, 30, 30);

        g2.setColor(Color.BLACK);
        g2.drawOval(x + 30, y + 30, 30, 30);
        g2.drawOval(x + 120, y + 30, 30, 30);
        g2.drawOval(x + 30, y + 120, 30, 30);
        g2.drawOval(x + 120, y + 120, 30, 30);
    }

    private void drawPathSquare(Graphics2D g2, int x, int y) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, 30, 30);

        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, 30, 30);
    }

    private void drawAllTokens(Graphics2D g2) {
        for (int playerIndex = 0; playerIndex < players.length; playerIndex++) {
            Color color = getPlayerColor(playerIndex);

            for (int tokenIndex = 0; tokenIndex < players[playerIndex].getTokens().length; tokenIndex++) {
                Token token = players[playerIndex].getTokens()[tokenIndex];

                if (token.getPosition() >= 0) {
                    Point point = getPathPoint(token.getPosition());
                    drawToken(g2, point.x, point.y, color, tokenIndex);
                } else {
                    // Keep tokens visible in their own colored home area until they move.
                    Point point = getHomeTokenPoint(playerIndex, tokenIndex);
                    drawToken(g2, point.x, point.y, color, tokenIndex);
                }
            }
        }
    }

    private Point getHomeTokenPoint(int playerIndex, int tokenIndex) {
        int homeX;
        int homeY;

        switch (playerIndex) {
            case 0: // Red - top left
                homeX = 40;
                homeY = 40;
                break;
            case 1: // Green - top right
                homeX = 320;
                homeY = 40;
                break;
            case 2: // Blue - bottom left
                homeX = 40;
                homeY = 320;
                break;
            default: // Yellow - bottom right
                homeX = 320;
                homeY = 320;
                break;
        }

        int column = tokenIndex % 2;
        int row = tokenIndex / 2;
        return new Point(homeX + 30 + column * 90, homeY + 30 + row * 90);
    }

    private void drawToken(Graphics2D g2, int x, int y, Color color, int tokenIndex) {
        int offsetX = (tokenIndex % 2) * 9;
        int offsetY = (tokenIndex / 2) * 9;

        g2.setColor(color);
        g2.fillOval(x + offsetX + 3, y + offsetY + 3, 21, 21);

        g2.setColor(Color.BLACK);
        g2.drawOval(x + offsetX + 3, y + offsetY + 3, 21, 21);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString(String.valueOf(tokenIndex + 1),
                x + offsetX + 10,
                y + offsetY + 18);
    }

    /*
     * Converts a path position (0-47) into one of the visible path squares.
     * The exact Ludo rules such as safe zones and home paths are reserved
     * for Week 6.
     */
    private Point getPathPoint(int position) {
        int p = position % PATH_SIZE;

        // Top side: 0-11
        if (p < 12) {
            if (p < 6) {
                return new Point(220, 40 + p * 30);
            }
            return new Point(290, 40 + (p - 6) * 30);
        }

        // Right side: 12-23
        if (p < 24) {
            int i = p - 12;
            if (i < 6) {
                return new Point(320 + i * 30, 220);
            }
            return new Point(320 + (i - 6) * 30, 290);
        }

        // Bottom side: 24-35
        if (p < 36) {
            int i = p - 24;
            if (i < 6) {
                return new Point(290, 320 + i * 30);
            }
            return new Point(220, 320 + (i - 6) * 30);
        }

        // Left side: 36-47
        int i = p - 36;
        if (i < 6) {
            return new Point(40 + i * 30, 290);
        }
        return new Point(40 + (i - 6) * 30, 220);
    }

    private Color getPlayerColor(int index) {
        switch (index) {
            case 0:
                return Color.RED;
            case 1:
                return Color.GREEN;
            case 2:
                return Color.BLUE;
            default:
                return Color.YELLOW;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rollDiceButton) {
            rollDiceAndMove();
        } else if (e.getSource() == newGameButton) {
            resetGame();
        } else if (e.getSource() == exitButton) {
            System.exit(0);
        }
    }

    private void rollDiceAndMove() {
        int diceValue = dice.rollDice();
        lastDiceValue = diceValue;

        Player currentPlayer = players[currentPlayerIndex];
        Token[] tokens = currentPlayer.getTokens();

        // Week 5 uses one token at a time so movement can be demonstrated
        // clearly. Later weeks can add token selection and Ludo restrictions.
        Token token = tokens[currentTokenIndex];

        int oldPosition = token.getPosition();
        token.move(diceValue, currentPlayer.getStartPosition());
        int newPosition = token.getPosition();

        String movementText;
        if (oldPosition == -1) {
            movementText = "Token " + (currentTokenIndex + 1)
                    + " entered the path at position " + (newPosition + 1);
        } else {
            movementText = "Token " + (currentTokenIndex + 1)
                    + " moved from position " + (oldPosition + 1)
                    + " to position " + (newPosition + 1);
        }

        statusLabel.setText(
                currentPlayer.getName()
                        + " rolled " + diceValue
                        + " | " + movementText
        );

        boardPanel.repaint();

        // Move to the next player after a completed move.
        currentPlayerIndex++;
        currentTokenIndex = 0;

        if (currentPlayerIndex >= players.length) {
            currentPlayerIndex = 0;
        }

        statusLabel.setText(
                statusLabel.getText()
                        + " | Next: " + players[currentPlayerIndex].getName()
        );
    }

    private void resetGame() {
        currentPlayerIndex = 0;
        currentTokenIndex = 0;
        lastDiceValue = 0;

        for (Player player : players) {
            player.resetTokens();
        }

        statusLabel.setText("Current Turn: Player 1");
        boardPanel.repaint();
    }
}
