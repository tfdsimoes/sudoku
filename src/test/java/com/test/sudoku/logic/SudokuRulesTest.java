package com.test.sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Position;
import org.junit.jupiter.api.Test;

class SudokuRulesTest {

    private static final int[][] SOLVED = {
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
    void emptyBoardAcceptsAnyDigit() {
        Board board = new Board();

        for (int value = 1; value <= 9; value++) {
            assertTrue(SudokuRules.isValidPlacement(board, 4, 4, value));
        }
    }

    @Test
    void rejectsDigitAlreadyInRow() {
        Board board = new Board();
        board.setValue(0, 8, 5);

        assertFalse(SudokuRules.isValidPlacement(board, 0, 0, 5));
        assertTrue(SudokuRules.isValidPlacement(board, 1, 0, 5));
    }

    @Test
    void rejectsDigitAlreadyInColumn() {
        Board board = new Board();
        board.setValue(7, 3, 6);

        assertFalse(SudokuRules.isValidPlacement(board, 2, 3, 6));
        assertTrue(SudokuRules.isValidPlacement(board, 2, 4, 6));
    }

    @Test
    void rejectsDigitAlreadyInBox() {
        Board board = new Board();
        board.setValue(2, 2, 9);

        assertFalse(SudokuRules.isValidPlacement(board, 0, 0, 9));
        assertFalse(SudokuRules.isValidPlacement(board, 1, 1, 9));
        assertTrue(SudokuRules.isValidPlacement(board, 3, 3, 9));
    }

    @Test
    void ignoresCellsOutsideRowColumnAndBox() {
        Board board = new Board();
        board.setValue(0, 0, 4);

        assertTrue(SudokuRules.isValidPlacement(board, 4, 4, 4));
    }

    @Test
    void currentCellContentIsIgnored() {
        Board board = new Board();
        board.setValue(4, 4, 7);

        assertTrue(SudokuRules.isValidPlacement(board, 4, 4, 7));
    }

    @Test
    void rejectsInvalidDigits() {
        Board board = new Board();

        assertFalse(SudokuRules.isValidPlacement(board, 0, 0, 0));
        assertFalse(SudokuRules.isValidPlacement(board, 0, 0, 10));
    }

    @Test
    void conflictsListsEachConflictingCellOnce() {
        Board board = new Board();
        board.setValue(0, 1, 3);
        board.setValue(1, 0, 3);
        board.setValue(1, 1, 3);

        var conflicts = SudokuRules.conflictsFor(board, 0, 0, 3);

        assertEquals(3, conflicts.size());
        assertTrue(conflicts.contains(new Position(0, 1)));
        assertTrue(conflicts.contains(new Position(1, 0)));
        assertTrue(conflicts.contains(new Position(1, 1)));
    }

    @Test
    void solvedBoardIsSolved() {
        assertTrue(SudokuRules.isSolved(new Board(SOLVED)));
    }

    @Test
    void boardWithEmptyCellIsNotSolved() {
        int[][] grid = copyOfSolved();
        grid[0][0] = 0;

        assertFalse(SudokuRules.isSolved(new Board(grid)));
    }

    @Test
    void boardWithDuplicateInRowIsNotSolved() {
        int[][] grid = copyOfSolved();
        grid[0][0] = grid[0][1];

        assertFalse(SudokuRules.isSolved(new Board(grid)));
    }

    @Test
    void boardWithDuplicateInBoxIsNotSolved() {
        int[][] grid = copyOfSolved();
        grid[1][1] = grid[0][0];

        assertFalse(SudokuRules.isSolved(new Board(grid)));
    }

    private static int[][] copyOfSolved() {
        int[][] copy = new int[9][];
        for (int i = 0; i < 9; i++) {
            copy[i] = SOLVED[i].clone();
        }
        return copy;
    }
}
