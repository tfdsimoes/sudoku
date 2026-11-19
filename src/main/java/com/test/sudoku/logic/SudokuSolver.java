package com.test.sudoku.logic;

import com.test.sudoku.model.Board;
import java.util.Optional;

/**
 * Backtracking solver. Always fills the empty cell with the fewest candidates first,
 * which keeps search fast enough for generating puzzles.
 */
public final class SudokuSolver {

    private static final int SIZE = Board.SIZE;
    private static final int BOX = Board.BOX_SIZE;
    private static final int ALL_DIGITS = 0b1111111110;

    private SudokuSolver() {
    }

    /**
     * Returns a solved copy of the board that keeps its givens, or empty when the board
     * has no solution.
     *
     * @param board board to solve
     * @return a solved copy, or empty if no solution exists
     */
    public static Optional<Board> solve(Board board) {
        int[][] first = firstSolution(board.toArray());
        if (first == null) {
            return Optional.empty();
        }
        Board solved = board.copy();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    solved.setValue(row, col, first[row][col]);
                }
            }
        }
        return Optional.of(solved);
    }

    /**
     * Counts solutions, stopping as soon as {@code limit} is reached.
     * Use a limit of 2 to test for uniqueness.
     *
     * @param board board to solve
     * @param limit maximum number of solutions to count
     * @return the solution count, capped at limit
     */
    public static int countSolutions(Board board, int limit) {
        if (limit <= 0) {
            return 0;
        }
        return count(board.toArray(), limit);
    }

    /**
     * Returns true when the board has exactly one solution.
     *
     * @param board board to check
     * @return whether exactly one solution exists
     */
    public static boolean hasUniqueSolution(Board board) {
        return countSolutions(board, 2) == 1;
    }

    private static int[][] firstSolution(int[][] grid) {
        if (!hasNoGivenConflicts(grid)) {
            return null;
        }
        int[][][] result = new int[1][][];
        search(grid, 1, new int[1], result);
        return result[0];
    }

    private static int count(int[][] grid, int limit) {
        if (!hasNoGivenConflicts(grid)) {
            return 0;
        }
        int[] found = new int[1];
        search(grid, limit, found, new int[1][][]);
        return found[0];
    }

    private static void search(int[][] grid, int limit, int[] found, int[][][] firstOut) {
        if (found[0] >= limit) {
            return;
        }
        int bestRow = -1;
        int bestCol = -1;
        int bestMask = 0;
        int bestCount = SIZE + 1;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    continue;
                }
                int mask = candidates(grid, row, col);
                int n = Integer.bitCount(mask);
                if (n < bestCount) {
                    bestCount = n;
                    bestRow = row;
                    bestCol = col;
                    bestMask = mask;
                    if (n == 0) {
                        return;
                    }
                }
            }
        }
        if (bestRow == -1) {
            found[0]++;
            if (firstOut[0] == null) {
                firstOut[0] = copy(grid);
            }
            return;
        }
        for (int digit = 1; digit <= SIZE; digit++) {
            if ((bestMask & (1 << digit)) == 0) {
                continue;
            }
            grid[bestRow][bestCol] = digit;
            search(grid, limit, found, firstOut);
            grid[bestRow][bestCol] = 0;
            if (found[0] >= limit) {
                return;
            }
        }
    }

    /** Bit d is set when digit d can go at (row, col). */
    private static int candidates(int[][] grid, int row, int col) {
        int used = 0;
        for (int i = 0; i < SIZE; i++) {
            used |= 1 << grid[row][i];
            used |= 1 << grid[i][col];
        }
        int boxRow = row - row % BOX;
        int boxCol = col - col % BOX;
        for (int r = boxRow; r < boxRow + BOX; r++) {
            for (int c = boxCol; c < boxCol + BOX; c++) {
                used |= 1 << grid[r][c];
            }
        }
        return ALL_DIGITS & ~used;
    }

    private static boolean hasNoGivenConflicts(int[][] grid) {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                int value = grid[row][col];
                if (value == 0) {
                    continue;
                }
                grid[row][col] = 0;
                int mask = candidates(grid, row, col);
                grid[row][col] = value;
                if ((mask & (1 << value)) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int[][] copy(int[][] grid) {
        int[][] result = new int[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            result[row] = grid[row].clone();
        }
        return result;
    }
}
