package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.logic.SudokuSolver;
import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import com.test.sudoku.model.Position;
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
