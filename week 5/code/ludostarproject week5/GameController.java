import javax.swing.*;

public class GameController {

    public void startGame() {
        JFrame frame = new JFrame("Ludo Star Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        Board board = new Board();
        frame.setContentPane(board);
        frame.setVisible(true);
    }
}
