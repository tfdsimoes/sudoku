package com.test.sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.model.Board;
import org.junit.jupiter.api.Test;

class SudokuSolverTest {

    private static final int[][] PUZZLE = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    private static final int[][] SOLUTION = {
        {5, 3, 4, 6, 7, 8, 9, 1, 2},
        {6, 7, 2, 1, 9, 5, 3, 4, 8},
        {1, 9, 8, 3, 4, 2, 5, 6, 7},
        {8, 5, 9, 7, 6, 1, 4, 2, 3},
        {4, 2, 6, 8, 5, 3, 7, 9, 1},
        {7, 1, 3, 9, 2, 4, 8, 5, 6},
        {9, 6, 1, 5, 3, 7, 2, 8, 4},
        {2, 8, 7, 4, 1, 9, 6, 3, 5},
        {3, 4, 5, 2, 8, 6, 1, 7, 9}
    };

    @Test
    void solvesKnownPuzzle() {
        Board solved = SudokuSolver.solve(new Board(PUZZLE)).orElseThrow();

        assertArrayEquals(SOLUTION, solved.toArray());
    }

    @Test
    void solutionKeepsGivensAsGivens() {
        Board puzzle = new Board(PUZZLE);

        Board solved = SudokuSolver.solve(puzzle).orElseThrow();

        assertTrue(solved.isGiven(0, 0));
        assertFalse(solved.isGiven(0, 2));
        assertTrue(puzzle.isEmpty(0, 2));
    }

    @Test
    void knownPuzzleHasUniqueSolution() {
        assertTrue(SudokuSolver.hasUniqueSolution(new Board(PUZZLE)));
    }

    @Test
    void emptyBoardHasManySolutions() {
        assertEquals(2, SudokuSolver.countSolutions(new Board(), 2));
        assertFalse(SudokuSolver.hasUniqueSolution(new Board()));
    }

    @Test
    void countStopsAtLimit() {
        assertEquals(3, SudokuSolver.countSolutions(new Board(), 3));
    }

    @Test
    void countWithZeroLimitIsZero() {
        assertEquals(0, SudokuSolver.countSolutions(new Board(), 0));
    }

    @Test
    void unsolvableBoardHasNoSolution() {
        int[][] grid = new int[9][9];
        for (int col = 1; col < 9; col++) {
            grid[0][col] = col;
        }
        grid[1][0] = 9;
        Board board = new Board(grid);

        assertTrue(SudokuSolver.solve(board).isEmpty());
        assertEquals(0, SudokuSolver.countSolutions(board, 2));
    }

    @Test
    void conflictingGivensHaveNoSolution() {
        int[][] grid = new int[9][9];
        grid[0][0] = 5;
        grid[0][8] = 5;
        Board board = new Board(grid);

        assertTrue(SudokuSolver.solve(board).isEmpty());
        assertEquals(0, SudokuSolver.countSolutions(board, 2));
    }

    @Test
    void solvedBoardSolvesToItself() {
        Board solved = new Board(SOLUTION);

        assertArrayEquals(SOLUTION, SudokuSolver.solve(solved).orElseThrow().toArray());
    }
}
