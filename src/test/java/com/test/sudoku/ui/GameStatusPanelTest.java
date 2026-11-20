package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.logic.SudokuSolver;
import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import com.test.sudoku.model.Position;
import java.awt.Color;
import java.awt.Dimension;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class GameStatusPanelTest {

    @Test
    void displaysAndUpdatesRemainingStrikes() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 21);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        int wrongValue = solution.getValue(emptyCell.row(), emptyCell.col()) % Board.SIZE + 1;
        AtomicReference<GameStatusPanel> statusRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GameStatusPanel status = new GameStatusPanel(session);
            assertEquals("Strikes left: 3", status.getStatusText());
            session.enterValue(emptyCell.row(), emptyCell.col(), wrongValue);
            status.update(session);
            statusRef.set(status);
        });

        SwingUtilities.invokeAndWait(
                () -> assertEquals("Strikes left: 2", statusRef.get().getStatusText()));
    }

    @Test
    void explainsIncorrectMoveAndReportsRemainingStrikes() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 22);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        int wrongValue = solution.getValue(emptyCell.row(), emptyCell.col()) % Board.SIZE + 1;
        AtomicReference<GameStatusPanel> statusRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GameStatusPanel status = new GameStatusPanel(session);
            GameSession.MoveResult result = session.enterValue(
                    emptyCell.row(), emptyCell.col(), wrongValue);
            status.update(session, result);
            statusRef.set(status);
        });

        assertEquals("Strikes left: 2", statusRef.get().getStatusText());
        assertEquals("Incorrect number. One strike used. 2 strikes remaining.",
                statusRef.get().getFeedbackText());
    }

    @Test
    void explainsWhenIncorrectMoveUsesFinalStrike() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 23);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        int wrongValue = solution.getValue(emptyCell.row(), emptyCell.col()) % Board.SIZE + 1;
        AtomicReference<GameStatusPanel> statusRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GameStatusPanel status = new GameStatusPanel(session);
            GameSession.MoveResult result = GameSession.MoveResult.CORRECT;
            for (int strike = 0; strike < GameSession.MAX_STRIKES; strike++) {
                result = session.enterValue(emptyCell.row(), emptyCell.col(), wrongValue);
            }
            status.update(session, result);
            statusRef.set(status);
        });

        assertEquals("Strikes left: 0", statusRef.get().getStatusText());
        assertEquals("Incorrect number. You lost your final strike; game over.",
                statusRef.get().getFeedbackText());
    }

    @Test
    void explainsThatCorrectlyFilledCellsAreLocked() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 25);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        session.enterValue(emptyCell.row(), emptyCell.col(),
                solution.getValue(emptyCell.row(), emptyCell.col()));
        AtomicReference<GameStatusPanel> statusRef = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GameStatusPanel status = new GameStatusPanel(session);
            status.update(session, GameSession.MoveResult.LOCKED_CELL);
            statusRef.set(status);
        });

        assertEquals("This correct number is locked and cannot be changed.",
                statusRef.get().getFeedbackText());
    }

    @Test
    void incorrectFeedbackUsesReservedSpaceAndSurvivesSubsequentMoves() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 24);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position firstCell = findEmptyCell(puzzle);
        int wrongValue = solution.getValue(firstCell.row(), firstCell.col()) % Board.SIZE + 1;
        AtomicReference<GameStatusPanel> statusRef = new AtomicReference<>();
        AtomicReference<Integer> initialHeight = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            GameStatusPanel status = new GameStatusPanel(session);
            initialHeight.set(status.getPreferredSize().height);
            GameSession.MoveResult result = session.enterValue(
                    firstCell.row(), firstCell.col(), wrongValue);
            status.update(session, result);
            String feedback = status.getFeedbackText();

            Position nextCell = findAnotherEmptyCell(puzzle, firstCell);
            GameSession.MoveResult nextResult = session.enterValue(nextCell.row(),
                    nextCell.col(), solution.getValue(nextCell.row(), nextCell.col()));
            status.update(session, nextResult);

            assertEquals(feedback, status.getFeedbackText());
            assertEquals(initialHeight.get(), status.getPreferredSize().height);
            statusRef.set(status);
        });

        assertEquals("Incorrect number. One strike used. 2 strikes remaining.",
                statusRef.get().getFeedbackText());
    }

    @Test
    void feedbackColorFadesGraduallyAfterTheHoldPeriod() {
        Color start = GameStatusPanel.feedbackColorAt(0);
        Color midpoint = GameStatusPanel.feedbackColorAt(
                GameStatusPanel.FEEDBACK_FADE_MILLIS / 2);
        Color end = GameStatusPanel.feedbackColorAt(GameStatusPanel.FEEDBACK_FADE_MILLIS);

        assertEquals(255, start.getAlpha());
        assertTrue(midpoint.getAlpha() > 0);
        assertTrue(midpoint.getAlpha() < start.getAlpha());
        assertEquals(0, end.getAlpha());
        assertEquals(3_000, GameStatusPanel.FEEDBACK_HOLD_MILLIS);
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

    private static Position findAnotherEmptyCell(Board board, Position excluded) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!board.isGiven(row, col)
                        && (row != excluded.row() || col != excluded.col())) {
                    return new Position(row, col);
                }
            }
        }
        throw new AssertionError("Generated puzzle must have multiple empty cells");
    }
}
