package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.logic.SudokuSolver;
import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import com.test.sudoku.model.Position;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class SudokuBoardPanelTest {

    @Test
    void draggingAcrossBoardDoesNotChangeSelection() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 10);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            Component grid = panel.getComponent(0);
            grid.setSize(540, 540);
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_PRESSED,
                    300, 300, 1, MouseEvent.BUTTON1));
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_DRAGGED,
                    450, 450, 1, MouseEvent.NOBUTTON));
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_RELEASED,
                    450, 450, 1, MouseEvent.BUTTON1));
            panelRef.set(panel);
        });

        assertEquals(new Position(0, 0), panelRef.get().getSelectedCell());
    }

    @Test
    void completedLeftClickSelectsTheCell() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 9);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            Component grid = panel.getComponent(0);
            grid.setSize(540, 540);
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_CLICKED,
                    300, 300, 1, MouseEvent.BUTTON1));
            panelRef.set(panel);
        });

        assertEquals(new Position(5, 5), panelRef.get().getSelectedCell());
    }

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

    private static MouseEvent mouseEvent(Component source, int eventId, int x, int y,
                                         int clickCount, int button) {
        int modifiers = eventId == MouseEvent.MOUSE_DRAGGED
                ? MouseEvent.BUTTON1_DOWN_MASK : 0;
        return new MouseEvent(source, eventId, System.currentTimeMillis(), modifiers,
                x, y, clickCount, false, button);
    }
}
