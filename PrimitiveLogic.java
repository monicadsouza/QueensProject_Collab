/**
 * This class implements a basic logic for the n-queens problem to get you started. 
 * Actually, when inserting a queen, it only puts the queen where requested
 * and does not keep track of which other positions are made illegal by this move.
 * 
 * @author Mai Ajspur
 * @version 16.02.2018
 */

public class PrimitiveLogic implements IQueensLogic{
    private int size;		// Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board;	// Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    
    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
    }
   
    public int[][] getBoard() {

        return board;
    }

    public void insertQueen(int column, int row) {

        board[column][row] = 1;
    }    
}
