import net.sf.javabdd.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class implements the logic behind the interface IQueensLogic and the
 * BDD package for the n-queens problem. It functions as an interactive configurator
 * that supports a user in solving the problem, after placing a queen it keeps
 * track of which other positions are made illegal by this move and guide the
 * user to the next possible legal moves, if exist.
 *
 * @author Anna-Katharina, Malene Vognsen, Monica Souza, Soo Jung Yoo
 */
public class Queen22 implements IQueensLogic {
    private int size; //Size of quadratic game board (i.e. size = #rows = #columns)
    private int[][] board; //Content of the board. Possible values: 0 (empty), 1 (queen), -1 (no queen allowed)
    private BDDFactory bddFactory; //A BDDFactory is used to produce the variables and constants (True and False) that we are going to use.
    private BDD totalBDD;
    private BDD upRestriction;
    private int nVars;  //The number of variables to be used.
    private HashSet<Integer> queens;

    public Queen22() {
        this.bddFactory = JFactory.init(2000000, 200000); //The two numbers represents the node number and the cache size.
        this.totalBDD = bddFactory.one();
        this.upRestriction = bddFactory.one();
        queens = new HashSet<>();
    }

    /**
     * Initialize the board given a size.
     * @param size The size of the board ( i.e. size = #rows = #columns).
     */
    public void initializeBoard(int size) {
        this.size = size;
        this.board = new int[size][size];
        this.nVars = size * size;
        buildBDD();
    }

    /**
     * @return The game board
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * This method creates a BDD that represents a conjunction of all
     * the rules and constrains (all sub BDDs) and store result in the
     * variable totalBDD.
     */
    private void buildBDD() {
        bddFactory.setVarNum(nVars); //Set board, one variable per field
        //rules which cover the variables position
        for (int var = 0; var < nVars; var++) { //for each variable join/and/add the rule to totalBDD -> field which are excluded!
            List<Integer> excludedVar = excludedVar(var); //get excluded vars given i
            totalBDD.andWith(makeRestriction(var, excludedVar)); //merges to the total BDDs, conjunct
        }
        totalBDD.andWith(rowColumnRestriction());  //conjoin result with the total BDD
    }

    /**
     * Creates a BDD that represents all the excluded variables from the list of indices.
     * @param var Index that represents a queen positioned on the board.
     * @param excluded List of all indices that represents all the illegal moves after a move.
     * @return A BDD that represents illegal moves (False variables) after a move.
     */
    private BDD makeRestriction(int var, List<Integer> excluded) { //Restrict the given excluded variables
        BDD b = bddFactory.one();
        for (int ex : excluded) {
            BDD not = bddFactory.nithVar(ex);//Returns a BDD representing the negation of the variable
            b.andWith(not); //Join all BDDs of negated variables
        }
        return bddFactory.ithVar(var).imp(b);  //imp
    }

    /**
     * A method that supports the rule that each rows must have one queen.
     * @return A BDD that represents True variables for each row that has one and only one queen.
     */
    private BDD rowColumnRestriction() {
        BDD b = bddFactory.one();
        for (int row = 0; row < size; row++) {
            BDD helper = bddFactory.zero(); //Set it to false, disjunction this row OR that
            for (int col = 0; col < size; col++) {
                int position = row * size + col; //For row 0 for example: position 0, 1, 2, 3, 4 if size=5
                helper.orWith(bddFactory.ithVar(position)); //Disjoint all these position because for all rows and columns
            }
            b.andWith(helper); //Conjoin the result for each row with the true helper BDD
        }
        return b;
    }

    /**
     * List of indices that should be mark as representing illegal moves after placing a queen.
     * @param var Index that represents a queen positioned on the board.
     * @return List of all indices that represents all the illegal moves after placing a queen.
     */
    private List<Integer> excludedVar(int var) {
        List<Integer> excluded = new ArrayList<>(); //Initialise excluded variables list

        //Exclude all rows moves aligned to the placed queen
        for(int col = 0; col < size; col++) { //loop through the length of the board
            int position = col + size * (var / size); //calculates position
            if (position == var) continue; //skip the given position
            excluded.add(position); //adds the aligned row positions
        }
        //Exclude all columns moves aligned to the placed queen
        int module = var % size;
        for(int row = 0; row < size; row++) { //loop through the length of the board
            int position = module + row * size; //calculate position
            if(position == var) continue; //skip the given position
            excluded.add(position); //adds the aligned column positions
        }

        //Excluded all diagonals moves aligned to the placed queen
        int[][] posDirections = {{-1, -1}, {-1, 1}, {1, 1}, {1, -1}}; //initialise the four different diagonal positions
        for (int[] vector : posDirections) { //loop through each diagonal direction
            int row = var / size + vector[0]; //the row value for the diagonal position
            int col = var % size + vector[1]; //the column value for the diagonal position
            while (row >= 0 && row < size && col >= 0 && col < size) { //loop through each position in that diagonal position
                int position = row * size + col;
                excluded.add(position); // add the diagonal position to excluded variables
                row += vector[0]; //increment the row value for the diagonal position
                col += vector[1]; //increment the column value for the diagonal position
            }
        }
        return excluded; // return list of excluded variables
    }

    /**
     * Update the list of indices representing placed queens and the restriction BDD after the move.
     * @param position Index of a position on the board after placing a queen.
     */
    private void updateRestriction(int position){
        BDD b = bddFactory.one();
        queens.add(position);
        for(int q : queens){
            b.andWith(bddFactory.ithVar(q));
        }
        upRestriction = totalBDD.restrict(b);
    }

    /**
     * Loop through all fields on the board and mark all positions that are suppose to be illegal as illegal.
     */
    private void invalidPlace() {
        for(int r = 0; r < size; r++) {
            for(int c = 0; c < size; c++) {
                int position = (r * size + c);
                if(board[c][r] == 1) continue;
                if(upRestriction.restrict(bddFactory.ithVar(position)).isZero()) {
                    board[c][r] = -1;
                } else if (upRestriction.pathCount() == 1) {
                    board[c][r] = 1;
                    updateRestriction(position);
                } else {
                    board[c][r] = 0;
                }
            }
        }
    }

    /**
     * Place a queen on the board and update the methods that evaluate the rules.
     * @param column Index that represents a column on the board.
     * @param row Index that represents a row on the board.
     */
    public void insertQueen(int column, int row) {
        int position = row * size + column;
        if (board[column][row] == -1) { //Illegal move
            board[column][row]= -1;
        }
        else if (board[column][row] == 1) { //Already a queen placed
            board[column][row] = 1;
        }
        else if (board[column][row] == 0) { //Possible move
            board[column][row] = 1; //Set a queen
            updateRestriction(position);
        }
        invalidPlace(); //Update invalid places
    }

}
