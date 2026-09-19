import javax.swing.Timer;

/**
 * Handles simple time-based animations for the Ludo game.
 * Game rules remain inside GameController.
 */
public class GameAnimator {

    private final Board board;

    public GameAnimator(Board board) {
        this.board = board;
    }

    /**
     * Animates a token from its current step to its destination.
     *
     * @param token      token to animate
     * @param targetStep final step
     * @param onFinished code to run after animation completes
     */
    public void animateToken(Token token, int targetStep, Runnable onFinished) {

        int startStep = token.getStepCount();

        // Token is entering the board from the yard.
        if (startStep < 0) {
            token.setStepCount(0);
            board.repaint();

            Timer timer = new Timer(120, null);
            timer.setRepeats(false);
            timer.addActionListener(e -> {
                timer.stop();

                if (onFinished != null) {
                    onFinished.run();
                }
            });
            timer.start();

            return;
        }

        // Nothing to animate.
        if (startStep >= targetStep) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        final int[] currentStep = {startStep};

        Timer timer = new Timer(100, null);

        timer.addActionListener(e -> {
            currentStep[0]++;

            token.setStepCount(currentStep[0]);
            board.repaint();

            if (currentStep[0] >= targetStep) {
                timer.stop();

                if (onFinished != null) {
                    onFinished.run();
                }
            }
        });

        timer.start();
    }
}