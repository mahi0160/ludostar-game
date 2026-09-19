public class Main {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("LUDO STAR GAME USING JAVA");
        System.out.println("=================================");

        Board board = new Board();
        board.displayBoard();

        Player player1 = new Player("Player 1", "Red");
        Player player2 = new Player("Player 2", "Blue");

        System.out.println("\nPlayers Created:");
        System.out.println(player1.getName() + " - " + player1.getColor());
        System.out.println(player2.getName() + " - " + player2.getColor());

        Token token1 = new Token();
        Token token2 = new Token();

        System.out.println("\nTokens Initialized:");
        System.out.println("Token 1 Position: " + token1.getPosition());
        System.out.println("Token 2 Position: " + token2.getPosition());

        Dice dice = new Dice();
        int diceValue = dice.rollDice();

        System.out.println("\nDice Rolled: " + diceValue);

        GameController game = new GameController();
        game.startGame();

        System.out.println("\nWeek 1 Project Setup Complete.");
    }
}