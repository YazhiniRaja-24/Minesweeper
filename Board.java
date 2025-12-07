// Board.java

import java.util.Random;

public class Board {
    private Cell[][] grid;
    private final int ROWS;
    private final int COLS;
    private final int TOTAL_MINES;

    // Constructor
    public Board(int rows, int cols, int totalMines) {
        this.ROWS = rows;
        this.COLS = cols;
        this.TOTAL_MINES = totalMines;
        this.grid = new Cell[rows][cols];
        initializeBoard();
    }

    // --- Core Setup ---

    private void initializeBoard() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new Cell();
            }
        }
    }

    // Places mines randomly, avoiding the first clicked spot (startRow, startCol)
    public void placeMines(int startRow, int startCol) {
        Random random = new Random();
        int minesPlaced = 0;

        while (minesPlaced < TOTAL_MINES) {
            int r = random.nextInt(ROWS);
            int c = random.nextInt(COLS);

            if (!grid[r][c].isMine() && (r != startRow || c != startCol)) {
                grid[r][c].setMine(true);
                minesPlaced++;
            }
        }
        calculateNeighborCounts(); // Calculate numbers immediately after placing mines
    }

    // Calculates and sets the neighborMines count for every non-mine cell
    public void calculateNeighborCounts() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (grid[r][c].isMine()) {
                    continue;
                }
                
                int mineCount = 0;
                
                // Check all 8 neighbors
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue; // Skip self
                        
                        int neighborR = r + dr;
                        int neighborC = c + dc;
                        
                        if (isSafe(neighborR, neighborC)) {
                            if (grid[neighborR][neighborC].isMine()) {
                                mineCount++;
                            }
                        }
                    }
                }
                grid[r][c].setNeighborMines(mineCount);
            }
        }
    }
    
    // --- Game Logic ---
    
    // The core recursive function: Reveals the cell and recursively sweeps if it's blank (0)
    // Returns true if a mine was hit (Game Over)
    public boolean reveal(int r, int c) {
        // Stop conditions: Out of bounds, already revealed, or flagged
        if (!isSafe(r, c) || grid[r][c].isRevealed() || grid[r][c].isFlagged()) {
            return false;
        }

        grid[r][c].setRevealed(true);

        if (grid[r][c].isMine()) {
            return true; // Game Lost!
        }

        // Recursive Sweep: Only sweep if the cell has 0 neighbor mines
        if (grid[r][c].getNeighborMines() == 0) {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr != 0 || dc != 0) {
                        reveal(r + dr, c + dc); // Recursive call
                    }
                }
            }
        }
        return false;
    }

    public void flag(int r, int c) {
        if (isSafe(r, c) && !grid[r][c].isRevealed()) {
            grid[r][c].setFlagged(true);
        }
    }

    public void unflag(int r, int c) {
        if (isSafe(r, c)) {
            grid[r][c].setFlagged(false);
        }
    }

    public boolean checkWin() {
        int unrevealedSafeCells = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                // Count cells that are NOT mines AND NOT revealed
                if (!grid[r][c].isMine() && !grid[r][c].isRevealed()) {
                    unrevealedSafeCells++;
                }
            }
        }
        return unrevealedSafeCells == 0;
    }
    
    // --- Display and Helpers ---

    private boolean isSafe(int r, int c) {
        return r >= 0 && r < ROWS && c >= 0 && c < COLS;
    }
    
    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    // Prints the board to the console
    public void displayBoard(boolean showAll) {
        System.out.print("  ");
        for (int c = 0; c < COLS; c++) {
            System.out.printf("%2d", c + 1); // Print column numbers (1-based)
        }
        System.out.println();
        
        for (int r = 0; r < ROWS; r++) {
            System.out.printf("%2d", r + 1); // Print row number (1-based)
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid[r][c];
                String displayChar = " ."; // Default: covered square

                if (showAll && cell.isMine()) {
                    displayChar = " *"; // Show mine when game is over
                } else if (cell.isFlagged()) {
                    displayChar = " F";
                } else if (cell.isRevealed()) {
                    if (cell.isMine()) {
                        displayChar = " X"; // The mine that was hit
                    } else if (cell.getNeighborMines() > 0) {
                        displayChar = " " + cell.getNeighborMines(); // Show number
                    } else {
                        displayChar = " -"; // Show blank (cleared) space
                    }
                }
                
                System.out.print(displayChar);
            }
            System.out.println();
        }
    }
}