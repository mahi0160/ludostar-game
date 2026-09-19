import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Turn order, dice, capture, and win rules.
 */
public class GameController {

    private final SoundManager soundManager;
    private final Board board;
    private final Die die;
    private final Player[] players;
    private final GameAnimator animator;
    private GameFrame frame;

    private int currentPlayerIndex = 0;
    private int consecutiveSixes = 0;
    private boolean waitingForTokenChoice = false;
    private boolean gameOver = false;
    private boolean gameStarted = false;

    // Number of players currently in the game
    private int playerCount = 4;

    private Player[] activePlayers;

    public GameController() {
        this.board = new Board();
        this.die = new Die();
        this.players = new Player[] {
                new Player("Player 1", PlayerColor.RED),
                new Player("Player 2", PlayerColor.GREEN),
                new Player("Player 3", PlayerColor.YELLOW),
                new Player("Player 4", PlayerColor.BLUE)
        };

        activePlayers = new Player[] {
                players[0],
                players[1],
                players[2],
                players[3]
        };

        board.setController(this);
        animator = new GameAnimator(board);
        soundManager = new SoundManager();
    }

    public void startGame() {
        frame = new GameFrame(this);
        frame.showWindow();

        gameStarted = false;
        soundManager.playLobbyMusic();

        log("Welcome to Ludo Star.");
        log("Click NEW GAME to choose the number of players.");
        status("Choose 2, 3, or 4 players to start the game.");

        refresh();
    }

    public Board getBoard() {
        return board;
    }

    public Die getDie() {
        return die;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Player getCurrentPlayer() {
        return activePlayers[currentPlayerIndex];
    }

    public boolean isWaitingForTokenChoice() {
        return waitingForTokenChoice;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Player[] getActivePlayers() {
        return activePlayers;
    }

    public void setActiveColors(PlayerColor[] colors) {
        if (colors == null || colors.length < 2 || colors.length > 4) {
            return;
        }

        Player[] selectedPlayers = new Player[colors.length];

        for (int i = 0; i < colors.length; i++) {
            for (Player player : players) {
                if (player.getColor() == colors[i]) {
                    selectedPlayers[i] = player;
                    break;
                }
            }
        }

        for (Player player : selectedPlayers) {
            if (player == null) {
                return;
            }
        }

        activePlayers = selectedPlayers;
        playerCount = activePlayers.length;
        currentPlayerIndex = 0;
    }

    public void setPlayerCount(int playerCount) {
        if (playerCount >= 2 && playerCount <= 4) {
            this.playerCount = playerCount;
        }
    }

    public void rollDice() {
        if (!gameStarted || gameOver || waitingForTokenChoice) {
            return;
        }

        Player player = getCurrentPlayer();
        soundManager.playDiceRoll();
        int roll = die.roll();
        log(player + " rolled " + roll + ".");

        if (roll == 6) {
            consecutiveSixes++;
        } else {
            consecutiveSixes = 0;
        }

        if (consecutiveSixes >= GameConstants.MAX_CONSECUTIVE_SIXES) {
            log("Three sixes in a row. Turn forfeited.");
            status(player.getName() + " rolled three 6s and loses this turn.");
            consecutiveSixes = 0;
            advanceTurn();
            return;
        }

        boolean[] movable = movableMask(player, roll);
        int count = countTrue(movable);

        if (count == 0) {
            if (roll == 6) {
                status(player.getName() + " rolled 6 but no token can move. Roll again.");
                log("No legal move. Extra roll because of 6.");
                refresh();
                return;
            }
            status(player.getName() + " rolled " + roll + ". No legal move.");
            advanceTurn();
            return;
        }

        if (count == 1) {
            for (int i = 0; i < movable.length; i++) {
                if (movable[i]) {
                    moveToken(player.getToken(i));
                    return;
                }
            }
        }

        waitingForTokenChoice = true;
        status(player.getName() + " rolled " + roll + ". Choose a highlighted token.");
        refresh();
    }

    public void chooseToken(int tokenIndex) {
        if (!waitingForTokenChoice || gameOver) {
            return;
        }
        Player player = getCurrentPlayer();
        Token token = player.getToken(tokenIndex);
        if (!canMoveToken(token, die.getLastRoll())) {
            status("Token " + token.displayNumber() + " cannot move with this roll.");
            return;
        }
        moveToken(token);
    }

    public void onBoardClick(int x, int y) {
        if (!waitingForTokenChoice || gameOver) {
            return;
        }
        Token token = board.tokenAtPixel(x, y, getCurrentPlayer());
        if (token != null) {
            chooseToken(token.getIndex());
        }
    }

    public boolean canMoveToken(Token token, int roll) {
        if (token.isFinished()) {
            return false;
        }
        if (token.isInYard()) {
            return roll == 6;
        }
        return token.getStepCount() + roll <= GameConstants.FINISH_STEP;
    }

    public void newGame() {
        currentPlayerIndex = 0;
        consecutiveSixes = 0;
        waitingForTokenChoice = false;
        gameOver = false;
        gameStarted = true;

        die.reset();

        for (Player player : players) {
            player.resetTokens();
        }

        if (frame != null) {
            frame.clearLog();
        }

        // Stop lobby music.
        soundManager.stopLobbyMusic();

        // Play game start sound once.
        soundManager.playGameStartMusic();

        Player startingPlayer = getCurrentPlayer();

        log("New game started with " + playerCount + " players.");
        log(startingPlayer.getName() + " ("
                + startingPlayer.getColor().getDisplayName()
                + ") starts.");

        status(startingPlayer.getName() + " ("
                + startingPlayer.getColor().getDisplayName()
                + "): roll the dice.");

        refresh();
    }

    private void moveToken(Token token) {
        Player player = getCurrentPlayer();
        int roll = die.getLastRoll();

        waitingForTokenChoice = false;

        boolean enteringBoard = token.isInYard();
        if (enteringBoard) {
            soundManager.playEnterBoard();
        } else {
            soundManager.playMove();
        }

        int targetStep;

        if (enteringBoard) {
            targetStep = 0;
        } else {
            targetStep = token.getStepCount() + roll;
        }

        if (enteringBoard) {
            log("Token " + token.displayNumber() + " is entering the board.");
        }

        animator.animateToken(
                token,
                targetStep,
                () -> finishTokenMove(player, token, roll, enteringBoard));
    }

    private void finishTokenMove(
            Player player,
            Token token,
            int roll,
            boolean enteringBoard) {

        if (enteringBoard) {
            log("Token " + token.displayNumber() + " entered the board.");
        } else {
            log("Token " + token.displayNumber()
                    + " moved " + roll + " square(s).");
        }

        boolean captured = captureOpponents(player, token);
        if (captured) {
            soundManager.playCapture();
        }

        if (token.isFinished()) {
            soundManager.playHome();
        }
        String message = player.getName()
                + " moved token " + token.displayNumber() + ".";

        if (captured) {
            message += " Capture! Extra turn.";
            log("An opponent token was sent back to its yard.");
        }

        if (token.isFinished()) {
            message += " Token reached HOME.";
            log("Token " + token.displayNumber() + " finished.");
        }

        if (player.hasWon()) {
            soundManager.playWin();
            gameOver = true;

            status(player + " wins! All four tokens reached HOME.");
            log(player.getName() + " wins the game.");
            refresh();

            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    frame,
                    player + " wins!",
                    "Ludo Star",
                    JOptionPane.INFORMATION_MESSAGE));

            return;
        }

        if (roll == 6 || captured) {
            status(message + " Roll again.");
            refresh();
            return;
        }

        consecutiveSixes = 0;
        status(message);
        advanceTurn();
    }

    private boolean captureOpponents(Player current, Token moved) {
        int pathIndex = board.pathIndexOf(moved);
        if (pathIndex < 0 || board.isSafePathIndex(pathIndex)) {
            return false;
        }
        boolean captured = false;
        for (Player opponent : activePlayers) {
            if (opponent == current) {
                continue;
            }
            for (Token other : opponent.getTokens()) {
                if (board.pathIndexOf(other) == pathIndex) {
                    other.reset();
                    captured = true;
                }
            }
        }
        return captured;
    }

    private boolean[] movableMask(Player player, int roll) {
        boolean[] movable = new boolean[GameConstants.TOKENS_PER_PLAYER];
        Token[] tokens = player.getTokens();
        for (int i = 0; i < tokens.length; i++) {
            movable[i] = canMoveToken(tokens[i], roll);
        }
        return movable;
    }

    private int countTrue(boolean[] values) {
        int count = 0;
        for (boolean value : values) {
            if (value) {
                count++;
            }
        }
        return count;
    }

    private void advanceTurn() {
        waitingForTokenChoice = false;
        consecutiveSixes = 0;
        currentPlayerIndex = (currentPlayerIndex + 1) % playerCount;
        status(getCurrentPlayer().getName() + ": roll the dice.");
        refresh();
    }

    private void status(String text) {
        if (frame != null) {
            frame.setStatus(text);
        }
    }

    private void log(String text) {
        if (frame != null) {
            frame.appendLog(text);
        }
    }

    private void refresh() {
        if (frame != null) {
            frame.refresh();
        } else {
            board.repaint();
        }
    }
}
