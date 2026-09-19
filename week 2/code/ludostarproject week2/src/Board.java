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

                g.setColor(Color.RED);
                g.fillRect(50, 50, 150, 150);

                g.setColor(Color.GREEN);
                g.fillRect(300, 50, 150, 150);

                g.setColor(Color.BLUE);
                g.fillRect(50, 250, 150, 150);

                g.setColor(Color.YELLOW);
                g.fillRect(300, 250, 150, 150);
            }
        };

        centerPanel.setPreferredSize(new Dimension(500, 450));

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        rollDiceButton = new JButton("Roll Dice");
        newGameButton = new JButton("New Game");
        exitButton = new JButton("Exit");

        rollDiceButton.addActionListener(this);
        newGameButton.addActionListener(this);
        exitButton.addActionListener(this);

        bottomPanel.add(rollDiceButton);
        bottomPanel.add(newGameButton);
        bottomPanel.add(exitButton);

        statusLabel = new JLabel("Click Roll Dice to Start");

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(bottomPanel, BorderLayout.SOUTH);

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