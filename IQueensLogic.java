/**
 * Interface with methods for an interactive configurator of the n-queen problem.
 * 
 * @author Mai Ajspur
 * @version 16.02.2018
 *
 */
public interface IQueensLogic {

	/**
	 * Initializes the quadratic board with the given size and initializes the board according to the rules
	 * of the n-queen problem.
	 * @param size The size of the board ( i.e. size = #rows = #columns)
	 */
	public void initializeBoard(int size);
	
	/**
	 * Return a representation of the board where each entry [c][r] is either 
	 *  1 : a queen is or must be present in column c and row r,
	 * -1 : a queen cannot be present in column c and row r
	 *  0 : a queen has not yet been placed in column c and row r. It does not have to
	 *      be there but it is allowed to be there.
	 * Columns are counted from left to right (starting with 0), 
	 * and rows are counted from top to bottom (counting from 0).   
	 */
	public int[][] getBoard();
	
	/**
	 * Inserts a queen at the specified position and updates the rest of the board accordingly,
	 * that is afterwards the board specifies where there _must_ be queens and where there _cannot_ be queens.  
	 */
	public void insertQueen(int column, int row);
}
