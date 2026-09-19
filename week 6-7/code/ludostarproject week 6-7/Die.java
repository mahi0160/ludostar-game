import java.util.Random;

public class Die {

    private final Random random = new Random();
    private int lastRoll = 0;

    public int roll() {
        lastRoll = random.nextInt(GameConstants.DIE_FACES) + 1;
        return lastRoll;
    }

    public int getLastRoll() {
        return lastRoll;
    }

    public void reset() {
        lastRoll = 0;
    }
}
