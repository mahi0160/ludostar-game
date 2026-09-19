import javax.swing.*;

public class GameController {

    public void startGame() {

        JFrame frame = new JFrame("Ludo Star Game");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        Board board = new Board();

        frame.add(board);

        frame.setVisible(true);
    }
}