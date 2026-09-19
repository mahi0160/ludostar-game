import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class GameFrame extends JFrame {

    private static final Color NAVY = new Color(18, 48, 94);
    private static final Color PANEL = new Color(232, 241, 255);

    private final GameController controller;
    private final JLabel turnLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel diceLabel = new JLabel("-", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    private final JTextArea logArea = new JTextArea();
    private final JButton rollButton = new JButton("ROLL DICE");
    private final JButton[] tokenButtons = new JButton[GameConstants.TOKENS_PER_PLAYER];
    private final JPanel tokenPanel = new JPanel(new GridLayout(2, 2, 6, 6));
    private final JPanel playersList = new JPanel(new GridLayout(4, 1));

    public GameFrame(GameController controller) {
        super("Ludo Star Game");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        root.setBackground(Color.WHITE);

        JLabel title = new JLabel("LUDO STAR GAME", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(NAVY);
        title.setOpaque(true);
        title.setBackground(new Color(239, 246, 255));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        root.add(title, BorderLayout.NORTH);
        root.add(controller.getBoard(), BorderLayout.CENTER);
        root.add(createRightPanel(), BorderLayout.EAST);

        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        statusLabel.setForeground(NAVY);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(PANEL);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(140, 165, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        statusLabel.setText("Roll the dice to start your turn.");
        root.add(statusLabel, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
    }

    public void showWindow() {
        setVisible(true);
    }

    public void setStatus(String text) {
        statusLabel.setText(text);
    }

    public void appendLog(String message) {
        if (logArea.getText().isEmpty()) {
            logArea.setText(message);
        } else {
            logArea.append("\n" + message);
        }
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public void clearLog() {
        logArea.setText("");
    }

    public void refresh() {
        Player player = controller.getCurrentPlayer();
        int playerCount = controller.getPlayerCount();

        playersList.removeAll();

        if (playerCount >= 2) {
            playersList.add(playerRow(PlayerColor.RED, "Player 1 (Red)"));
            playersList.add(playerRow(PlayerColor.GREEN, "Player 2 (Green)"));
        }

        if (playerCount >= 3) {
            playersList.add(playerRow(PlayerColor.YELLOW, "Player 3 (Yellow)"));
        }

        if (playerCount >= 4) {
            playersList.add(playerRow(PlayerColor.BLUE, "Player 4 (Blue)"));
        }

        playersList.revalidate();
        playersList.repaint();
        turnLabel.setText(player.getName() + " (" + player.getColor().getDisplayName() + ")");
        turnLabel.setForeground(player.getColor().getBoardColor());

        int roll = controller.getDie().getLastRoll();
        diceLabel.setText(roll == 0 ? "-" : String.valueOf(roll));

        boolean choosing = controller.isWaitingForTokenChoice();

        rollButton.setEnabled(
                controller.isGameStarted()
                        && !controller.isGameOver()
                        && !choosing);
        tokenPanel.setVisible(choosing);

        for (int i = 0; i < tokenButtons.length; i++) {
            Token token = player.getToken(i);
            String state;
            if (token.isFinished()) {
                state = "HOME";
            } else if (token.isInYard()) {
                state = "YARD";
            } else {
                state = "Step " + token.getStepCount();
            }
            tokenButtons[i].setText("Token " + token.displayNumber() + " · " + state);
            tokenButtons[i].setEnabled(choosing && controller.canMoveToken(token, roll));
        }

        controller.getBoard().repaint();
        revalidate();
        repaint();
    }

    private JPanel createRightPanel() {
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setPreferredSize(new Dimension(250, GameConstants.boardPixelSize()));
        right.setBackground(Color.WHITE);

        JPanel turnBox = section("PLAYER TURN");
        turnLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        turnBox.add(turnLabel, BorderLayout.CENTER);
        right.add(turnBox);
        right.add(Box.createVerticalStrut(10));

        JPanel diceBox = section("ROLL DICE");
        diceLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        diceLabel.setPreferredSize(new Dimension(100, 70));
        diceLabel.setOpaque(true);
        diceLabel.setBackground(Color.WHITE);
        diceLabel.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 2));
        diceBox.add(diceLabel, BorderLayout.CENTER);
        rollButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        rollButton.addActionListener(e -> controller.rollDice());
        diceBox.add(rollButton, BorderLayout.SOUTH);
        right.add(diceBox);
        right.add(Box.createVerticalStrut(10));

        JPanel choiceBox = section("CHOOSE TOKEN");
        tokenPanel.setBackground(Color.WHITE);
        for (int i = 0; i < tokenButtons.length; i++) {
            final int index = i;
            tokenButtons[i] = new JButton("Token " + (i + 1));
            tokenButtons[i].setFont(new Font("SansSerif", Font.BOLD, 11));
            tokenButtons[i].addActionListener(e -> controller.chooseToken(index));
            tokenPanel.add(tokenButtons[i]);
        }
        tokenPanel.setVisible(false);
        choiceBox.add(tokenPanel, BorderLayout.CENTER);
        right.add(choiceBox);
        right.add(Box.createVerticalStrut(10));

        JPanel logBox = section("GAME LOG");
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setPreferredSize(new Dimension(220, 150));
        logBox.add(scroll, BorderLayout.CENTER);
        right.add(logBox);
        right.add(Box.createVerticalStrut(10));

        JPanel playersBox = section("PLAYERS");

        playersList.setBackground(Color.WHITE);
        playersList.add(playerRow(PlayerColor.RED, "Player 1 (Red)"));
        playersList.add(playerRow(PlayerColor.GREEN, "Player 2 (Green)"));
        playersList.add(playerRow(PlayerColor.YELLOW, "Player 3 (Yellow)"));
        playersList.add(playerRow(PlayerColor.BLUE, "Player 4 (Blue)"));

        playersBox.add(playersList, BorderLayout.CENTER);
        right.add(playersBox);
        right.add(Box.createVerticalGlue());

        JPanel controls = new JPanel(new GridLayout(1, 2, 8, 0));
        controls.setBackground(Color.WHITE);
        JButton newGame = new JButton("NEW GAME");
        JButton exit = new JButton("EXIT");
        newGame.setFont(new Font("SansSerif", Font.BOLD, 12));
        exit.setFont(new Font("SansSerif", Font.BOLD, 12));
        newGame.addActionListener(e -> {
            String[] options = { "2 Players", "3 Players", "4 Players" };

            int choice = javax.swing.JOptionPane.showOptionDialog(
                    this,
                    "Choose the number of players:",
                    "New Game",
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[2]);

            if (choice >= 0) {
                controller.setPlayerCount(choice + 2);
                controller.newGame();
            }
        });
        exit.addActionListener(e -> System.exit(0));
        controls.add(newGame);
        controls.add(exit);
        right.add(controls);

        return right;
    }

    private JPanel section(String title) {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(40, 70, 115), 2),
                BorderFactory.createEmptyBorder(7, 7, 7, 7)));
        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(NAVY);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        panel.add(header, BorderLayout.NORTH);
        return panel;
    }

    private JPanel playerRow(PlayerColor color, String text) {
        JPanel row = new JPanel();
        row.setBackground(Color.WHITE);
        JLabel dot = new JLabel("•");
        dot.setFont(new Font("SansSerif", Font.BOLD, 18));
        dot.setForeground(color.getBoardColor());
        row.add(dot);
        row.add(new JLabel(text));
        return row;
    }
}
