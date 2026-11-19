package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.logic.SudokuSolver;
import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import com.test.sudoku.model.Position;
import java.awt.event.KeyEvent;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class SudokuBoardPanelTest {

    @Test
    void mapsGridCoordinatesToCellsAndRejectsOutsidePoints() {
        assertEquals(Optional.of(new Position(0, 0)),
                SudokuBoardPanel.cellAtPoint(0, 0, 900, 900));
        assertEquals(Optional.of(new Position(8, 8)),
                SudokuBoardPanel.cellAtPoint(899, 899, 900, 900));
        assertEquals(Optional.of(new Position(0, 0)),
                SudokuBoardPanel.cellAtPoint(180, 0, 900, 540));
        assertTrue(SudokuBoardPanel.cellAtPoint(-1, 0, 900, 900).isEmpty());
        assertTrue(SudokuBoardPanel.cellAtPoint(0, 0, 0, 900).isEmpty());
    }

    @Test
    void mapsDigitAndNumpadKeys() {
        assertEquals(OptionalInt.of(1), SudokuBoardPanel.digitForKeyCode(KeyEvent.VK_1));
        assertEquals(OptionalInt.of(9), SudokuBoardPanel.digitForKeyCode(KeyEvent.VK_NUMPAD9));
        assertTrue(SudokuBoardPanel.digitForKeyCode(KeyEvent.VK_0).isEmpty());
        assertTrue(SudokuBoardPanel.digitForKeyCode(KeyEvent.VK_A).isEmpty());
    }

    @Test
    void onScreenNumberButtonEntersValueAtSelectedCell() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 11);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            panel.selectCell(emptyCell.row(), emptyCell.col());
            panel.getNumberButton(solution.getValue(emptyCell.row(), emptyCell.col())).doClick();
        });

        assertEquals(GameSession.MoveResult.CORRECT, result.get());
        assertEquals(solution.getValue(emptyCell.row(), emptyCell.col()),
                session.getBoard().getValue(emptyCell.row(), emptyCell.col()));
    }

    @Test
    void clearButtonClearsSelectedPlayerEntry() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 12);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        session.enterValue(emptyCell.row(), emptyCell.col(),
                solution.getValue(emptyCell.row(), emptyCell.col()));
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            panel.selectCell(emptyCell.row(), emptyCell.col());
            panel.getNumberButton(0).doClick();
        });

        assertEquals(GameSession.MoveResult.CLEARED, result.get());
        assertTrue(session.getBoard().isEmpty(emptyCell.row(), emptyCell.col()));
        assertFalse(session.isGameOver());
    }

    private static Position findEmptyCell(Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!board.isGiven(row, col)) {
                    return new Position(row, col);
                }
            }
        }
        throw new AssertionError("Generated puzzle must have empty cells");
    }
}
