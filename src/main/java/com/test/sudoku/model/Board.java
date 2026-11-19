package com.test.sudoku.model;

import java.util.Arrays;

/**
 * A 9x9 Sudoku grid. Values are 0 (empty) or 1-9. Cells filled at construction
 * time are marked as givens and cannot be changed through {@link #setValue}.
 */
public final class Board {

    public static final int SIZE = 9;
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

    /** Returns the value at a position, 0 when empty. */
    public int getValue(int row, int col) {
        checkIndex(row, col);
        return values[row][col];
    }

    /**
     * Sets a non-given cell.
     *
     * @param value 0 to clear the cell, or 1-9
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

    /** Returns true when the cell was a given at construction time. */
    public boolean isGiven(int row, int col) {
        checkIndex(row, col);
        return givens[row][col];
    }

    /** Returns true when the cell has no value. */
    public boolean isEmpty(int row, int col) {
        return getValue(row, col) == 0;
    }

    /** Returns the number of givens on the board. */
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

    /** Returns a deep copy of this board, including its givens. */
    public Board copy() {
        int[][] valuesCopy = new int[SIZE][];
        boolean[][] givensCopy = new boolean[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            valuesCopy[row] = values[row].clone();
            givensCopy[row] = givens[row].clone();
        }
        return new Board(valuesCopy, givensCopy);
    }

    /** Returns a deep copy of the values as a new array. */
    public int[][] toArray() {
        int[][] result = new int[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            result[row] = values[row].clone();
        }
        return result;
    }

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
