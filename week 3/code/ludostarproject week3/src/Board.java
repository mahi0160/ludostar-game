import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Board extends JPanel implements ActionListener {

    private JLabel statusLabel;
    private JButton rollDiceButton;
    private JButton newGameButton;
    private JButton exitButton;

    public Board() {

        setLayout(new BorderLayout());

        JLabel title = new JLabel("LUDO STAR GAME", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                // Background
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // ===== HOME AREAS =====

                g2.setColor(Color.RED);
                g2.fillRect(40, 40, 180, 180);

                g2.setColor(Color.GREEN);
                g2.fillRect(320, 40, 180, 180);

                g2.setColor(Color.BLUE);
                g2.fillRect(40, 320, 180, 180);

                g2.setColor(Color.YELLOW);
                g2.fillRect(320, 320, 180, 180);

                // Borders
                g2.setColor(Color.BLACK);
                g2.drawRect(40, 40, 180, 180);
                g2.drawRect(320, 40, 180, 180);
                g2.drawRect(40, 320, 180, 180);
                g2.drawRect(320, 320, 180, 180);

                // ===== CENTER HOME =====

                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(220, 220, 100, 100);

                g2.setColor(Color.BLACK);
                g2.drawRect(220, 220, 100, 100);

                g2.drawString("HOME", 250, 275);

                // ===== PATH SQUARES =====

                for (int i = 0; i < 6; i++) {

                    g2.drawRect(220, 40 + (i * 30), 30, 30);
                    g2.drawRect(290, 40 + (i * 30), 30, 30);

                    g2.drawRect(220, 320 + (i * 30), 30, 30);
                    g2.drawRect(290, 320 + (i * 30), 30, 30);

                    g2.drawRect(40 + (i * 30), 220, 30, 30);
                    g2.drawRect(40 + (i * 30), 290, 30, 30);

                    g2.drawRect(320 + (i * 30), 220, 30, 30);
                    g2.drawRect(320 + (i * 30), 290, 30, 30);
                }

                // ===== TOKENS =====

                drawTokens(g2, 70, 70);
                drawTokens(g2, 350, 70);
                drawTokens(g2, 70, 350);
                drawTokens(g2, 350, 350);

                // Labels

                g2.setColor(Color.BLACK);

                g2.drawString("RED", 110, 30);
                g2.drawString("GREEN", 385, 30);
                g2.drawString("BLUE", 110, 530);
                g2.drawString("YELLOW", 380, 530);
            }

            private void drawTokens(Graphics2D g2, int x, int y) {

                g2.setColor(Color.WHITE);

                g2.fillOval(x, y, 30, 30);
                g2.fillOval(x + 60, y, 30, 30);

                g2.fillOval(x, y + 60, 30, 30);
                g2.fillOval(x + 60, y + 60, 30, 30);

                g2.setColor(Color.BLACK);

                g2.drawOval(x, y, 30, 30);
                g2.drawOval(x + 60, y, 30, 30);

                g2.drawOval(x, y + 60, 30, 30);
                g2.drawOval(x + 60, y + 60, 30, 30);
            }
        };

        centerPanel.setPreferredSize(new Dimension(550, 560));

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        rollDiceButton = new JButton("Roll Dice");
        newGameButton = new JButton("New Game");
        exitButton = new JButton("Exit");

        rollDiceButton.addActionListener(this);
        newGameButton.addActionListener(this);
        exitButton.addActionListener(this);

        buttonPanel.add(rollDiceButton);
        buttonPanel.add(newGameButton);
        buttonPanel.add(exitButton);

        statusLabel = new JLabel("Week 3: Ludo Board and Player Pieces Added");

        JPanel southPanel = new JPanel(new BorderLayout());

        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == rollDiceButton) {

            Random random = new Random();
            int diceValue = random.nextInt(6) + 1;

            statusLabel.setText("Dice Rolled: " + diceValue);
        }

        else if (e.getSource() == newGameButton) {

            statusLabel.setText("New Game Started");
        }

        else if (e.getSource() == exitButton) {

            System.exit(0);
        }
    }
}