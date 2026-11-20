package com.tfdsimoes.sudoku.ui;

import com.tfdsimoes.sudoku.logic.GameSession;
import com.tfdsimoes.sudoku.model.Board;
import com.tfdsimoes.sudoku.model.Position;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

/**
 * Displays and accepts input for a Sudoku game session.
 */
public final class SudokuBoardPanel extends JPanel {

    private static final Color BOARD_WHITE = Color.WHITE;
    private static final Color GRID_BLACK = Color.BLACK;
    private static final Color HINT_RED = new Color(139, 0, 0);
    private static final Color PLAYER_BLUE = new Color(0, 0, 139);
    private static final Color SELECTION_GRAY = new Color(232, 232, 232);

    private final GameSession session;
    private final Consumer<GameSession.MoveResult> onMove;
    private final BoardGrid boardGrid;
    private final JButton helpButton;
    private final JLabel selectionHint = new JLabel("Select a cell before entering a number",
            SwingConstants.CENTER);
    private Position selectedCell;

    /**
     * Creates the board and controls for a session.
     *
     * @param session active game session
     * @param onMove callback for the outcome of each attempted move
     */
    public SudokuBoardPanel(GameSession session, Consumer<GameSession.MoveResult> onMove) {
        this.session = Objects.requireNonNull(session, "session");
        this.onMove = Objects.requireNonNull(onMove, "onMove");
        setLayout(new BorderLayout(0, 14));
        setBackground(BOARD_WHITE);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        selectionHint.setForeground(new Color(70, 70, 70));
        boardGrid = new BoardGrid();
        helpButton = createHelpButton();
        updateHelpButton();
        add(selectionHint, BorderLayout.NORTH);
        add(boardGrid, BorderLayout.CENTER);
        add(helpButton, BorderLayout.SOUTH);
        installKeyBindings(boardGrid);
    }

    /**
     * Selects a board cell and gives it keyboard focus.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    public void selectCell(int row, int col) {
        session.getBoard().getValue(row, col);
        selectedCell = new Position(row, col);
        selectionHint.setText("Selected row " + (row + 1) + ", column " + (col + 1));
        boardGrid.repaint();
        boardGrid.requestFocusInWindow();
    }

    /**
     * Returns the currently selected cell.
     *
     * @return selected zero-based board position, or empty if no cell is selected
     */
    public Optional<Position> getSelectedCell() {
        return Optional.ofNullable(selectedCell);
    }

    /**
     * Maps a pointer coordinate to a cell in a centered square board.
     *
     * @param x pointer x-coordinate
     * @param y pointer y-coordinate
     * @param width component width
     * @param height component height
     * @return the cell, or empty when the point is outside the board
     */
    public static Optional<Position> cellAtPoint(int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return Optional.empty();
        }
        int boardSize = Math.min(width, height);
        int localX = x - (width - boardSize) / 2;
        int localY = y - (height - boardSize) / 2;
        if (localX < 0 || localY < 0 || localX >= boardSize || localY >= boardSize) {
            return Optional.empty();
        }
        return Optional.of(new Position(
                localY * Board.SIZE / boardSize,
                localX * Board.SIZE / boardSize));
    }

    /**
     * Maps a digit or numpad key code to its Sudoku value.
     *
     * @param keyCode AWT key code
     * @return the digit, or empty if the key is not 1-9
     */
    public static OptionalInt digitForKeyCode(int keyCode) {
        if (keyCode >= KeyEvent.VK_1 && keyCode <= KeyEvent.VK_9) {
            return OptionalInt.of(keyCode - KeyEvent.VK_0);
        }
        if (keyCode >= KeyEvent.VK_NUMPAD1 && keyCode <= KeyEvent.VK_NUMPAD9) {
            return OptionalInt.of(keyCode - KeyEvent.VK_NUMPAD0);
        }
        return OptionalInt.empty();
    }

    static Color numberColor(boolean isGiven, boolean isHint) {
        if (isGiven) {
            return GRID_BLACK;
        }
        return isHint ? HINT_RED : PLAYER_BLUE;
    }

    JButton getHelpButton() {
        return helpButton;
    }

    private JButton createHelpButton() {
        JButton button = new JButton();
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        button.setForeground(GRID_BLACK);
        button.setBackground(BOARD_WHITE);
        button.setOpaque(true);
        button.addActionListener(event -> revealHint());
        return button;
    }

    private void updateHelpButton() {
        OptionalInt remaining = session.getRemainingHints();
        String count = remaining.isPresent()
                ? Integer.toString(remaining.getAsInt()) : "\u221e";
        helpButton.setText("Help (" + count + ")");
        helpButton.setEnabled(remaining.isEmpty() || remaining.getAsInt() > 0);
        helpButton.getAccessibleContext().setAccessibleName(remaining.isEmpty()
                ? "Reveal a random correct cell, unlimited uses"
                : "Reveal a random correct cell, " + remaining.getAsInt() + " uses remaining");
    }

    private void revealHint() {
        Optional<GameSession.Hint> hint = session.revealRandomEmptyCell();
        if (hint.isEmpty()) {
            selectionHint.setText(session.isGameOver() || session.isWon()
                    ? "Help is unavailable after the game ends"
                    : "No empty cells remain to reveal");
            return;
        }

        GameSession.Hint revealed = hint.orElseThrow();
        Position position = revealed.position();
        selectCell(position.row(), position.col());
        selectionHint.setText("Hint: row " + (position.row() + 1)
                + ", column " + (position.col() + 1) + " is " + revealed.value());
        updateHelpButton();
        onMove.accept(revealed.result());
    }

    private void installKeyBindings(JComponent component) {
        var inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var actionMap = component.getActionMap();
        for (int value = 1; value <= Board.SIZE; value++) {
            int digit = value;
            String actionName = "enter-" + digit;
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_0 + digit, 0), actionName);
            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + digit, 0), actionName);
            actionMap.put(actionName, new AbstractAction() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent event) {
                    enterValue(digit);
                }
            });
        }
        bindClearKey(inputMap, actionMap, KeyEvent.VK_BACK_SPACE, "clear-backspace");
        bindClearKey(inputMap, actionMap, KeyEvent.VK_DELETE, "clear-delete");
    }

    private void bindClearKey(javax.swing.InputMap inputMap, javax.swing.ActionMap actionMap,
                              int keyCode, String actionName) {
        inputMap.put(KeyStroke.getKeyStroke(keyCode, 0), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent event) {
                enterValue(0);
            }
        });
    }

    private void enterValue(int value) {
        if (selectedCell == null) {
            selectionHint.setText("Select a cell before entering a number");
            return;
        }
        GameSession.MoveResult result = session.enterValue(
                selectedCell.row(), selectedCell.col(), value);
        boardGrid.repaint();
        onMove.accept(result);
    }

    private final class BoardGrid extends JComponent {

        private BoardGrid() {
            setOpaque(true);
            setBackground(BOARD_WHITE);
            setFocusable(true);
            setPreferredSize(new Dimension(540, 540));
            setMinimumSize(new Dimension(270, 270));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent event) {
                    if (event.getButton() == MouseEvent.BUTTON1) {
                        cellAtPoint(event.getX(), event.getY(), getWidth(), getHeight())
                                .ifPresent(position -> selectCell(position.row(), position.col()));
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            try {
                g.setColor(BOARD_WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int side = Math.min(getWidth(), getHeight());
                if (side <= 0) {
                    return;
                }
                int originX = (getWidth() - side) / 2;
                int originY = (getHeight() - side) / 2;
                Board board = session.getBoard();
                paintCells(g, board, originX, originY, side);
                paintGrid(g, originX, originY, side);
            } finally {
                g.dispose();
            }
        }

        private void paintCells(Graphics2D g, Board board, int originX, int originY, int side) {
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    int x1 = originX + col * side / Board.SIZE;
                    int x2 = originX + (col + 1) * side / Board.SIZE;
                    int y1 = originY + row * side / Board.SIZE;
                    int y2 = originY + (row + 1) * side / Board.SIZE;
                    if (selectedCell != null
                            && selectedCell.row() == row && selectedCell.col() == col) {
                        g.setColor(SELECTION_GRAY);
                        g.fillRect(x1, y1, x2 - x1, y2 - y1);
                    }
                    int value = board.getValue(row, col);
                    if (value != 0) {
                        Color color = numberColor(board.isGiven(row, col),
                                session.isHint(row, col));
                        paintValue(g, value, x1, y1, x2, y2, color);
                    }
                }
            }
        }

        private void paintValue(Graphics2D g, int value, int x1, int y1, int x2, int y2,
                                Color color) {
            int cellSize = Math.min(x2 - x1, y2 - y1);
            g.setColor(color);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(12, cellSize / 2)));
            var metrics = g.getFontMetrics();
            String text = Integer.toString(value);
            int x = x1 + ((x2 - x1) - metrics.stringWidth(text)) / 2;
            int y = y1 + ((y2 - y1) - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(text, x, y);
        }

        private void paintGrid(Graphics2D g, int originX, int originY, int side) {
            for (int line = 0; line <= Board.SIZE; line++) {
                g.setColor(GRID_BLACK);
                g.setStroke(new java.awt.BasicStroke(
                        line % Board.BOX_SIZE == 0 ? 3f : 1f));
                int x = originX + line * side / Board.SIZE;
                int y = originY + line * side / Board.SIZE;
                g.drawLine(x, originY, x, originY + side);
                g.drawLine(originX, y, originX + side, y);
            }
        }
    }
}
