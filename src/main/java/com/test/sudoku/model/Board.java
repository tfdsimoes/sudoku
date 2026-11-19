package com.test.sudoku.model;

import java.util.Arrays;

/**
 * A 9x9 Sudoku grid. Values are 0 (empty) or 1-9. Cells filled at construction
 * time are marked as givens and cannot be changed through {@link #setValue}.
 */
public final class Board {

    /** Number of rows and columns in a Sudoku board. */
    public static final int SIZE = 9;

    /** Number of rows and columns in each box. */
    public static final int BOX_SIZE = 3;

    private final int[][] values;
    private final boolean[][] givens;

    /** Creates an empty board with no givens. */
    public Board() {
        this.values = new int[SIZE][SIZE];
        this.givens = new boolean[SIZE][SIZE];
    }

    /**
     * Creates a board from a grid. Non-zero cells become givens.
     *
     * @param grid a 9x9 array with values 0-9
     * @throws IllegalArgumentException if the grid shape or any value is invalid
     */
    public Board(int[][] grid) {
        this();
        validateShape(grid);
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                checkValue(value);
                values[row][col] = value;
                givens[row][col] = value != 0;
            }
        }
    }

    private Board(int[][] values, boolean[][] givens) {
        this.values = values;
        this.givens = givens;
    }

    /**
     * Returns the value at a position, 0 when empty.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return the cell value
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    public int getValue(int row, int col) {
        checkIndex(row, col);
        return values[row][col];
    }

    /**
     * Sets a non-given cell.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @param value 0 to clear the cell, or 1-9
     * @throws IndexOutOfBoundsException if the cell is outside the board
     * @throws IllegalArgumentException if value is outside 0-9
     * @throws IllegalStateException if the cell is a given
     */
    public void setValue(int row, int col, int value) {
        checkIndex(row, col);
        checkValue(value);
        if (givens[row][col]) {
            throw new IllegalStateException("Cell (" + row + ", " + col + ") is a given");
        }
        values[row][col] = value;
    }

    /**
     * Returns true when the cell was a given at construction time.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return whether the cell is a fixed clue
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    public boolean isGiven(int row, int col) {
        checkIndex(row, col);
        return givens[row][col];
    }

    /**
     * Returns true when the cell has no value.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return whether the cell is empty
     */
    public boolean isEmpty(int row, int col) {
        return getValue(row, col) == 0;
    }

    /**
     * Returns the number of fixed clues on the board.
     *
     * @return the number of non-empty cells marked as givens
     */
    public int countGivens() {
        int count = 0;
        for (boolean[] rowGivens : givens) {
            for (boolean given : rowGivens) {
                if (given) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Returns a deep copy of this board, including its givens.
     *
     * @return an independent board copy
     */
    public Board copy() {
        int[][] valuesCopy = new int[SIZE][];
        boolean[][] givensCopy = new boolean[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            valuesCopy[row] = values[row].clone();
            givensCopy[row] = givens[row].clone();
        }
        return new Board(valuesCopy, givensCopy);
    }

    /**
     * Returns a deep copy of the values as a new array.
     *
     * @return an independent 9x9 grid
     */
    public int[][] toArray() {
        int[][] result = new int[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            result[row] = values[row].clone();
        }
        return result;
    }

    /** Compares both cell values and which cells are fixed clues. */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Board board)) {
            return false;
        }
        return Arrays.deepEquals(values, board.values) && Arrays.deepEquals(givens, board.givens);
    }

    /** Returns a hash consistent with {@link #equals(Object)}. */
    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(values) + Arrays.deepHashCode(givens);
    }

    private static void validateShape(int[][] grid) {
        if (grid == null || grid.length != SIZE) {
            throw new IllegalArgumentException("Grid must have " + SIZE + " rows");
        }
        for (int[] row : grid) {
            if (row == null || row.length != SIZE) {
                throw new IllegalArgumentException("Each row must have " + SIZE + " columns");
            }
        }
    }

    private static void checkValue(int value) {
        if (value < 0 || value > SIZE) {
            throw new IllegalArgumentException("Value must be between 0 and " + SIZE + ": " + value);
        }
    }

    private static void checkIndex(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IndexOutOfBoundsException("Cell out of range: (" + row + ", " + col + ")");
        }
    }
}
