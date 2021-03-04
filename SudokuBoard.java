package sudoku;

import java.util.ArrayList;
import java.util.Random;

public class SudokuBoard {

    private ArrayList<ArrayList<Integer>> numberPool;
    private boolean[][] forbiddenMove;
    private Random rand;
    private int[][] board;
    private int[][] originalBoard;

    /*
	Constructor for the class. If user wants a prefilled board then true
	is passed in otherwise it is expected that the user will provide their
	own board.
     */
    public SudokuBoard(boolean preFill) {
        //To be used with each column, columns will contain a number from
        //1-9 only once. Rows and Squares must be checked.
        numberPool = new ArrayList<ArrayList<Integer>>();

        //Row and Col are the keys, Should hold the original input so they can't be moved
        forbiddenMove = new boolean[9][9];

        rand = new Random();

        for (int i = 0; i < 9; i++) {
            numberPool.add(new ArrayList<Integer>());

            for (int j = 1; j < 10; j++) {
                numberPool.get(i).add(new Integer(j));
            }
        }

        if (preFill) {
            defaultBoard();
        } else {
            board = new int[9][9];
        }
    }

    /*
	Second constructor that will initialize itself based on the info
	of an existing Sudoku board.
     */
    public SudokuBoard(SudokuBoard sb) {
        board = new int[9][9];
        originalBoard = sb.getOriginalBoard();
        copySudokuBoard(sb);
    }

    /*
	Method to be used to copy a passed Sudoku Board
     */
    public void copySudokuBoard(SudokuBoard sb) {
        int[][] arr = sb.getBoard();

        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                this.board[i][j] = arr[i][j];
            }
        }

        this.forbiddenMove = sb.getForbiddenMove();
    }

    /*
	Method that will initialize the Sudoku board with a default
     */
    private void defaultBoard() {
        board = new int[][]{
        {9, 0, 0, 0, 0, 0, 4, 8, 6},
        {0, 0, 0, 0, 9, 0, 1, 0, 0},
        {2, 0, 0, 5, 0, 6, 0, 0, 0},
        {0, 1, 6, 0, 3, 5, 0, 4, 0},
        {0, 2, 0, 0, 0, 0, 0, 6, 1},
        {0, 9, 0, 6, 8, 0, 2, 3, 0},
        {0, 0, 0, 9, 0, 8, 0, 0, 4},
        {0, 0, 2, 0, 5, 0, 0, 0, 0},
        {6, 4, 9, 0, 0, 0, 0, 0, 8}};
        
        /*board = new int[][]{
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9} };*/

        originalBoard = board;

        //Set forbidden-pool and update number pool         
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] != 0) {
                    //temp.put(new Integer(j), new Boolean(true));

                    //Set the position as unchangable
                    forbiddenMove[i][j] = true;

                    //Remove value from number pool
                    numberPool.get(j).remove(new Integer(board[i][j]));
                }
            }
        }
        fillBlanks();
    }

    /*
	Method used to fill in blanks in columns based on the valid remaining inputs
     */
    public void fillBlanks() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (board[i][j] == 0) {
                    board[i][j] = numberPool.get(j).remove(rand.nextInt(numberPool.get(j).size()));
                }
            }
        }
    }

    /*
	Method to swap to squares in a column. First the method checks to see if the swaps are
	forbidden, if so return false else make the swap and return true
     */
    public boolean swap(int col, int r1, int r2) {
        int temp;

        if (isForbidden(r1, col)) {
            return false;
        }

        if (isForbidden(r2, col)) {
            return false;
        }

        temp = board[r1][col];
        board[r1][col] = board[r2][col];
        board[r2][col] = temp;

        return true;
    }

    /*
	Method that returns if a square is a forbidden move ie pre-set by
	the original puzzle
     */
    public boolean isForbidden(int row, int col) {
        return forbiddenMove[row][col];
    }

    /*
	Method to return the 2-D array board
     */
    public int[][] getBoard() {
        return board;
    }

    /*
	Method that will maintain the original board for
	reference
     */
    public int[][] getOriginalBoard() {
        return originalBoard;
    }

    /*
	Method to replace a value at the given row and column.
	It will bypass any checks in place.
     */
    public void changeValueAt(int row, int col, int value) {
        board[row][col] = value;
    }

    /*
	Method to return the value at the desired square
     */
    public int getValueAt(int row, int col) {
        return board[row][col];
    }

    /*
	Method that returns the forbidden move hash
     */
    public boolean[][] getForbiddenMove() {
        return forbiddenMove;
    }

    /*
	Method to print out the remaining useable numbers from the number
	pool for each column
     */
    public void printNumberPool() {
        for (int i = 0; i < numberPool.size(); i++) {
            for (int j = 0; j < numberPool.get(i).size(); j++) {
                System.out.println("I: " + i + " J: " + j + " Value: " + numberPool.get(i).get(j));
            }
        }
    }

    /*
	Overloaded method to print out the board
     */
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < board.length; i++) {
            if (i == 3 || i == 6) {
                out.append("-------------------\n");
            }

            for (int j = 0; j < board[i].length; j++) {
                if (j == 3 || j == 6) {
                    out.append("| ");
                }

                if (board[i][j] > 0) {
                    out.append(board[i][j]);
                } else {
                    out.append('_');
                }

                out.append(' ');
            }
            out.append('\n');
        }
        return out.toString();
    }
}
