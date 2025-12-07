// MinesweeperGame.java

import java.util.Scanner;

public class MinesweeperGame {

    private static final int DEFAULT_ROWS = 9;
    private static final int DEFAULT_COLS = 9;
    private static final int DEFAULT_MINES = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Board board = null;
        boolean firstMove = true;
        boolean gameLost = false;
        boolean gameWon = false;

        // --- Setup ---
        System.out.println("Welcome to Console Minesweeper!");
        System.out.printf("Starting game with default settings: %dx%d board and %d mines.\n", 
                            DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_MINES);
        
        board = new Board(DEFAULT_ROWS, DEFAULT_COLS, DEFAULT_MINES);
        
        // --- Main Game Loop ---
        while (!gameLost && !gameWon) {
            board.displayBoard(gameLost); // Display board (showAll = false unless gameLost)
            
            System.out.println("\nEnter command (e.g., R 3 5 for Reveal, F 1 2 for Flag, U 1 2 for Unflag):");
            String inputLine = scanner.nextLine().toUpperCase();
            String[] parts = inputLine.split(" ");
            
            if (parts.length != 3) {
                System.out.println("Invalid command format. Please use: [R/F/U] [Row] [Col]");
                continue;
            }

            try {
                char command = parts[0].charAt(0);
                // Convert 1-based user input to 0-based array index
                int r = Integer.parseInt(parts[1]) - 1; 
                int c = Integer.parseInt(parts[2]) - 1;

                // Input Validation
                if (r < 0 || r >= board.getROWS() || c < 0 || c >= board.getCOLS()) {
                    System.out.println("Coordinates are out of bounds. Please use valid numbers.");
                    continue;
                }

                if (command == 'R') { // REVEAL
                    if (firstMove) {
                        // Place mines AFTER the first move to guarantee a safe start
                        board.placeMines(r, c);
                        firstMove = false;
                    }
                    
                    if (board.reveal(r, c)) {
                        gameLost = true; // Mine was hit
                    }
                } else if (command == 'F') { // FLAG
                    board.flag(r, c);
                } else if (command == 'U') { // UNFLAG
                    board.unflag(r, c);
                } else {
                    System.out.println("Unknown command: " + command);
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid coordinate numbers. Please ensure row and column are integers.");
            }
            
            if (!gameLost && !firstMove) {
                gameWon = board.checkWin();
            }
        }

        // --- Game End ---
        board.displayBoard(true); // Display the final board, showing all mines
        if (gameLost) {
            System.out.println("\n💥 GAME OVER! You hit a mine. Better luck next time.");
        } else if (gameWon) {
            System.out.println("\n🎉 CONGRATULATIONS! You cleared the board and won the game!");
        }
        
        scanner.close();
    }
}