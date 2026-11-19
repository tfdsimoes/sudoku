package com.test.sudoku.logic;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard Sudoku rules: each row, column and 3x3 box holds the digits 1-9 at most once.
 */
public final class SudokuRules {

    private SudokuRules() {
    }

    /**
     * Returns true when {@code value} can be placed at the cell without breaking a rule.
     * The cell's current content is ignored.
     */
    public static boolean isValidPlacement(Board board, int row, int col, int value) {
        return isDigit(value) && conflictsFor(board, row, col, value).isEmpty();
    }

    /**
     * Returns the other cells in the same row, column or box that already hold {@code value}.
     * The cell itself is never included.
     */
    public static List<Position> conflictsFor(Board board, int row, int col, int value) {
        List<Position> conflicts = new ArrayList<>();
        if (!isDigit(value)) {
            return conflicts;
        }
        int boxRowStart = row - row % Board.BOX_SIZE;
        int boxColStart = col - col % Board.BOX_SIZE;
        for (int i = 0; i < Board.SIZE; i++) {
            addIfConflict(board, conflicts, row, i, value, row, col);
            addIfConflict(board, conflicts, i, col, value, row, col);
        }
        for (int r = boxRowStart; r < boxRowStart + Board.BOX_SIZE; r++) {
            for (int c = boxColStart; c < boxColStart + Board.BOX_SIZE; c++) {
                addIfConflict(board, conflicts, r, c, value, row, col);
            }
        }
        return conflicts;
    }

    /** Returns true when every cell is filled and no rule is broken. */
    public static boolean isSolved(Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                int value = board.getValue(row, col);
                if (!isDigit(value) || !conflictsFor(board, row, col, value).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void addIfConflict(Board board, List<Position> conflicts,
                                      int r, int c, int value, int row, int col) {
        if ((r != row || c != col) && board.getValue(r, c) == value) {
            Position position = new Position(r, c);
            if (!conflicts.contains(position)) {
                conflicts.add(position);
            }
        }
    }

    private static boolean isDigit(int value) {
        return value >= 1 && value <= Board.SIZE;
    }
}
