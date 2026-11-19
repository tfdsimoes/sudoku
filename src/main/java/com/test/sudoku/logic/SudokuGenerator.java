package com.test.sudoku.logic;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import java.util.Objects;
import java.util.Random;

/**
 * Generates randomized Sudoku puzzles with a unique solution.
 */
public final class SudokuGenerator {

    private static final int SIZE = Board.SIZE;
    private static final int ALL_DIGITS = 0b1111111110;
    private static final int MAX_ATTEMPTS = 50;

    /** Creates a generator. */
    public SudokuGenerator() {
    }

    /**
     * Generates a puzzle at the requested difficulty using a fresh random seed.
     *
     * @param difficulty the target difficulty
     * @return a puzzle with a unique solution and a given count in the difficulty range
     * @throws NullPointerException if difficulty is null
     * @throws IllegalStateException if no puzzle in the requested range is found
     */
    public Board generate(Difficulty difficulty) {
        return generate(difficulty, new Random());
    }

    /**
     * Generates a reproducible puzzle at the requested difficulty.
     *
     * @param difficulty the target difficulty
     * @param seed the random seed
     * @return a puzzle with a unique solution and a given count in the difficulty range
     * @throws NullPointerException if difficulty is null
     * @throws IllegalStateException if no puzzle in the requested range is found
     */
    public Board generate(Difficulty difficulty, long seed) {
        return generate(difficulty, new Random(seed));
    }

    private Board generate(Difficulty difficulty, Random random) {
        Objects.requireNonNull(difficulty, "difficulty");
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            int[][] solution = new int[SIZE][SIZE];
            if (!fillRandomly(solution, random)) {
                continue;
            }

            int targetGivens = difficulty.minGivens()
                    + random.nextInt(difficulty.maxGivens() - difficulty.minGivens() + 1);
            int[][] puzzle = copy(solution);
            int givens = SIZE * SIZE;
            int[] positions = shuffledPositions(random);
            for (int position : positions) {
                int row = position / SIZE;
                int col = position % SIZE;
                int value = puzzle[row][col];
                puzzle[row][col] = 0;
                if (SudokuSolver.hasUniqueSolution(new Board(puzzle))) {
                    givens--;
                    if (givens == targetGivens) {
                        return new Board(puzzle);
                    }
                } else {
                    puzzle[row][col] = value;
                }
            }
        }
        throw new IllegalStateException("Could not generate a unique puzzle for " + difficulty);
    }

    private static boolean fillRandomly(int[][] grid, Random random) {
        int bestRow = -1;
        int bestCol = -1;
        int[] bestCandidates = new int[SIZE];
        int bestCount = SIZE + 1;
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (grid[row][col] != 0) {
                    continue;
                }
                int mask = candidates(grid, row, col);
                int count = Integer.bitCount(mask);
                if (count < bestCount) {
                    bestRow = row;
                    bestCol = col;
                    bestCount = count;
                    bestCandidates = digitsFromMask(mask);
                    if (count <= 1) {
                        break;
                    }
                }
            }
            if (bestCount <= 1) {
                break;
            }
        }
        if (bestRow == -1) {
            return true;
        }
        if (bestCount == 0) {
            return false;
        }

        shuffle(bestCandidates, random);
        for (int value : bestCandidates) {
            if (value == 0) {
                break;
            }
            grid[bestRow][bestCol] = value;
            if (fillRandomly(grid, random)) {
                return true;
            }
            grid[bestRow][bestCol] = 0;
        }
        return false;
    }

    private static int candidates(int[][] grid, int row, int col) {
        int used = 0;
        for (int i = 0; i < SIZE; i++) {
            used |= 1 << grid[row][i];
            used |= 1 << grid[i][col];
        }
        int boxRow = row - row % Board.BOX_SIZE;
        int boxCol = col - col % Board.BOX_SIZE;
        for (int r = boxRow; r < boxRow + Board.BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + Board.BOX_SIZE; c++) {
                used |= 1 << grid[r][c];
            }
        }
        return ALL_DIGITS & ~used;
    }

    private static int[] digitsFromMask(int mask) {
        int[] digits = new int[Integer.bitCount(mask)];
        int index = 0;
        for (int digit = 1; digit <= SIZE; digit++) {
            if ((mask & (1 << digit)) != 0) {
                digits[index++] = digit;
            }
        }
        return digits;
    }

    private static void shuffle(int[] values, Random random) {
        for (int i = values.length - 1; i > 0; i--) {
            int other = random.nextInt(i + 1);
            int value = values[i];
            values[i] = values[other];
            values[other] = value;
        }
    }

    private static int[] shuffledPositions(Random random) {
        int[] positions = new int[SIZE * SIZE];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = i;
        }
        shuffle(positions, random);
        return positions;
    }

    private static int[][] copy(int[][] grid) {
        int[][] result = new int[SIZE][];
        for (int row = 0; row < SIZE; row++) {
            result[row] = grid[row].clone();
        }
        return result;
    }
}
