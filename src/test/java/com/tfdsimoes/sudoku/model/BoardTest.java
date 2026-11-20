package com.tfdsimoes.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoardTest {

    private static int[][] emptyGrid() {
        return new int[9][9];
    }

    @Test
    void newBoardIsEmptyWithNoGivens() {
        Board board = new Board();

        assertEquals(0, board.countGivens());
        assertTrue(board.isEmpty(0, 0));
        assertFalse(board.isGiven(4, 4));
    }

    @Test
    void setValueStoresValueInNonGivenCell() {
        Board board = new Board();

        board.setValue(2, 7, 5);

        assertEquals(5, board.getValue(2, 7));
        assertFalse(board.isEmpty(2, 7));
    }

    @Test
    void setValueZeroClearsCell() {
        Board board = new Board();
        board.setValue(0, 0, 9);

        board.setValue(0, 0, 0);

        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void nonZeroCellsInGridBecomeGivens() {
        int[][] grid = emptyGrid();
        grid[0][0] = 5;
        grid[8][8] = 1;

        Board board = new Board(grid);

        assertTrue(board.isGiven(0, 0));
        assertTrue(board.isGiven(8, 8));
        assertFalse(board.isGiven(0, 1));
        assertEquals(2, board.countGivens());
    }

    @Test
    void givenCellCannotBeChanged() {
        int[][] grid = emptyGrid();
        grid[3][3] = 7;
        Board board = new Board(grid);

        assertThrows(IllegalStateException.class, () -> board.setValue(3, 3, 2));
        assertEquals(7, board.getValue(3, 3));
    }

    @Test
    void valueOutOfRangeIsRejected() {
        Board board = new Board();

        assertThrows(IllegalArgumentException.class, () -> board.setValue(0, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> board.setValue(0, 0, -1));
    }

    @Test
    void cellOutOfRangeIsRejected() {
        Board board = new Board();

        assertThrows(IndexOutOfBoundsException.class, () -> board.getValue(9, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setValue(0, -1, 1));
    }

    @Test
    void gridWithWrongShapeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Board(new int[8][9]));
        assertThrows(IllegalArgumentException.class, () -> new Board(new int[9][8]));
        assertThrows(IllegalArgumentException.class, () -> new Board(null));
    }

    @Test
    void gridWithInvalidValueIsRejected() {
        int[][] grid = emptyGrid();
        grid[0][0] = 12;

        assertThrows(IllegalArgumentException.class, () -> new Board(grid));
    }

    @Test
    void constructorCopiesInputGrid() {
        int[][] grid = emptyGrid();
        Board board = new Board(grid);

        grid[0][0] = 4;

        assertTrue(board.isEmpty(0, 0));
    }

    @Test
    void copyIsIndependentOfOriginal() {
        int[][] grid = emptyGrid();
        grid[1][1] = 6;
        Board original = new Board(grid);
        Board copy = original.copy();

        copy.setValue(0, 0, 3);

        assertTrue(original.isEmpty(0, 0));
        assertEquals(copy.countGivens(), original.countGivens());
        assertTrue(copy.isGiven(1, 1));
    }

    @Test
    void equalBoardsAreEqual() {
        int[][] grid = emptyGrid();
        grid[4][4] = 2;

        Board a = new Board(grid);
        Board b = new Board(grid);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void boardsWithDifferentValuesAreNotEqual() {
        Board a = new Board();
        Board b = new Board();
        b.setValue(0, 0, 1);

        assertNotEquals(a, b);
    }

    @Test
    void toArrayReturnsDetachedValues() {
        Board board = new Board();
        board.setValue(0, 0, 8);

        int[][] values = board.toArray();
        values[0][0] = 1;

        assertEquals(8, board.getValue(0, 0));
        assertArrayEquals(new int[]{8, 0, 0, 0, 0, 0, 0, 0, 0}, board.toArray()[0]);
    }
}
