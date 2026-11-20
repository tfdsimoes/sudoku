package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.logic.SudokuSolver;
import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import com.test.sudoku.model.Position;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class SudokuBoardPanelTest {

    @Test
    void draggingAcrossBoardDoesNotChangeSelection() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 10);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            Component grid = panel.getComponent(1);
            grid.setSize(540, 540);
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_PRESSED,
                    300, 300, 1, MouseEvent.BUTTON1));
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_DRAGGED,
                    450, 450, 1, MouseEvent.NOBUTTON));
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_RELEASED,
                    450, 450, 1, MouseEvent.BUTTON1));
            panelRef.set(panel);
        });

        assertEquals(Optional.of(new Position(5, 5)), panelRef.get().getSelectedCell());
        assertEquals("Selected row 6, column 6",
                ((JLabel) panelRef.get().getComponent(0)).getText());
    }

    @Test
    void leftButtonPressSelectsCellImmediately() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 9);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            Component grid = panel.getComponent(1);
            grid.setSize(540, 540);
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_PRESSED,
                    300, 300, 1, MouseEvent.BUTTON1));
            panelRef.set(panel);
        });

        assertEquals(Optional.of(new Position(5, 5)), panelRef.get().getSelectedCell());
    }

    @Test
    void repaintingAfterSelectionChangeClearsPreviousHighlight() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 9);
        AtomicReference<Integer> previousCellPixel = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            Component grid = panel.getComponent(1);
            grid.setSize(540, 540);
            BufferedImage image = new BufferedImage(540, 540, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = image.createGraphics();
            try {
                panel.selectCell(5, 5);
                grid.paint(graphics);
                panel.selectCell(2, 2);
                grid.paint(graphics);
                previousCellPixel.set(image.getRGB(301, 301));
            } finally {
                graphics.dispose();
            }
        });

        assertEquals(Color.WHITE.getRGB(), previousCellPixel.get());
    }

    @Test
    void keyboardInputDoesNotDefaultToTopLeftBeforeCellSelection() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 8);
        Board originalBoard = session.getBoard();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            invokeKeyAction((JComponent) panel.getComponent(1), KeyEvent.VK_1);
            assertTrue(panel.getSelectedCell().isEmpty());
            assertEquals("Select a cell before entering a number",
                    ((JLabel) panel.getComponent(0)).getText());
        });

        assertEquals(originalBoard, session.getBoard());
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
    void numberColorsDistinguishGivenPlayerAndHintValues() {
        assertEquals(Color.BLACK, SudokuBoardPanel.numberColor(true, false));
        assertEquals(new Color(0, 0, 139), SudokuBoardPanel.numberColor(false, false));
        assertEquals(new Color(139, 0, 0), SudokuBoardPanel.numberColor(false, true));
    }

    @Test
    void keyboardDigitEntersValueAtSelectedCell() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 11);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            Component grid = panel.getComponent(1);
            grid.setSize(540, 540);
            grid.dispatchEvent(mouseEvent(grid, MouseEvent.MOUSE_PRESSED,
                    emptyCell.col() * 60 + 30, emptyCell.row() * 60 + 30,
                    1, MouseEvent.BUTTON1));
            assertEquals(Optional.of(emptyCell), panel.getSelectedCell());
            int value = solution.getValue(emptyCell.row(), emptyCell.col());
            invokeKeyAction((JComponent) grid, KeyEvent.VK_0 + value);
        });

        assertEquals(GameSession.MoveResult.CORRECT, result.get());
        assertEquals(solution.getValue(emptyCell.row(), emptyCell.col()),
                session.getBoard().getValue(emptyCell.row(), emptyCell.col()));
    }

    @Test
    void bottomControlContainsHelpButton() throws Exception {
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() ->
                panelRef.set(new SudokuBoardPanel(
                        new GameSession(Difficulty.EASY, 9), result -> { })));

        assertEquals(3, panelRef.get().getComponentCount());
        assertEquals("Help (3)", panelRef.get().getHelpButton().getText());
    }

    @Test
    void helpButtonRevealsCorrectValueInAnEmptyCell() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 12);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            panel.getHelpButton().doClick();
            panelRef.set(panel);
        });

        Position position = panelRef.get().getSelectedCell().orElseThrow();
        assertTrue(puzzle.isEmpty(position.row(), position.col()));
        assertEquals(solution.getValue(position.row(), position.col()),
                session.getBoard().getValue(position.row(), position.col()));
        assertEquals(GameSession.MoveResult.CORRECT, result.get());
        assertEquals(0, session.getStrikes());
        assertEquals("Help (2)", panelRef.get().getHelpButton().getText());
    }

    @Test
    void helpButtonDisablesAfterThreeUses() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 14);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            panelRef.set(panel);
            panel.getHelpButton().doClick();
            panel.getHelpButton().doClick();
            panel.getHelpButton().doClick();
        });

        assertEquals("Help (0)", panelRef.get().getHelpButton().getText());
        assertFalse(panelRef.get().getHelpButton().isEnabled());
        assertEquals(OptionalInt.of(0), session.getRemainingHints());
    }

    @Test
    void unlimitedHelpButtonRemainsAvailableAndShowsInfinity() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 15, true);
        AtomicReference<SudokuBoardPanel> panelRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result -> { });
            panelRef.set(panel);
            assertEquals("Help (\u221e)", panel.getHelpButton().getText());
            for (int use = 0; use < GameSession.MAX_HINTS + 2; use++) {
                panel.getHelpButton().doClick();
            }
        });

        assertTrue(panelRef.get().getHelpButton().isEnabled());
        assertEquals("Help (\u221e)", panelRef.get().getHelpButton().getText());
        assertTrue(session.getRemainingHints().isEmpty());
    }

    @Test
    void backspaceCannotClearLockedPlayerEntry() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 12);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        int correctValue = solution.getValue(emptyCell.row(), emptyCell.col());
        session.enterValue(emptyCell.row(), emptyCell.col(), correctValue);
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            panel.selectCell(emptyCell.row(), emptyCell.col());
            invokeKeyAction((JComponent) panel.getComponent(1), KeyEvent.VK_BACK_SPACE);
        });

        assertEquals(GameSession.MoveResult.LOCKED_CELL, result.get());
        assertEquals(correctValue,
                session.getBoard().getValue(emptyCell.row(), emptyCell.col()));
        assertFalse(session.isGameOver());
    }

    @Test
    void enteringAnotherNumberInLockedCellDoesNotAddStrike() throws Exception {
        GameSession session = new GameSession(Difficulty.EASY, 16);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        Position emptyCell = findEmptyCell(puzzle);
        int correctValue = solution.getValue(emptyCell.row(), emptyCell.col());
        int wrongValue = correctValue % Board.SIZE + 1;
        session.enterValue(emptyCell.row(), emptyCell.col(), correctValue);
        AtomicReference<GameSession.MoveResult> result = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            SudokuBoardPanel panel = new SudokuBoardPanel(session, result::set);
            panel.selectCell(emptyCell.row(), emptyCell.col());
            invokeKeyAction((JComponent) panel.getComponent(1), KeyEvent.VK_0 + wrongValue);
        });

        assertEquals(GameSession.MoveResult.LOCKED_CELL, result.get());
        assertEquals(0, session.getStrikes());
        assertEquals(correctValue,
                session.getBoard().getValue(emptyCell.row(), emptyCell.col()));
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

    private static void invokeKeyAction(JComponent component, int keyCode) {
        Object actionKey = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .get(KeyStroke.getKeyStroke(keyCode, 0));
        Action action = component.getActionMap().get(actionKey);
        action.actionPerformed(new ActionEvent(
                component, ActionEvent.ACTION_PERFORMED, "keyboard-input"));
    }

    private static MouseEvent mouseEvent(Component source, int eventId, int x, int y,
                                         int clickCount, int button) {
        int modifiers = eventId == MouseEvent.MOUSE_DRAGGED
                ? MouseEvent.BUTTON1_DOWN_MASK : 0;
        return new MouseEvent(source, eventId, System.currentTimeMillis(), modifiers,
                x, y, clickCount, false, button);
    }
}
