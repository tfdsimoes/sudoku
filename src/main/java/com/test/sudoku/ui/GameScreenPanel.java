package com.test.sudoku.ui;

import com.test.sudoku.logic.GameSession;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Objects;
import javax.swing.JPanel;

/** Combines the board, strikes HUD, and end-of-game navigation for one session. */
public final class GameScreenPanel extends JPanel {

    private final GameSession session;
    private final Runnable onRetry;
    private final Runnable onChooseLevel;
    private final GameStatusPanel statusPanel;
    private final SudokuBoardPanel boardPanel;

    public GameScreenPanel(GameSession session, Runnable onRetry, Runnable onChooseLevel) {
        this.session = Objects.requireNonNull(session, "session");
        this.onRetry = Objects.requireNonNull(onRetry, "onRetry");
        this.onChooseLevel = Objects.requireNonNull(onChooseLevel, "onChooseLevel");
        setLayout(new BorderLayout(0, 8));
        setBackground(Color.WHITE);

        statusPanel = new GameStatusPanel(session);
        boardPanel = new SudokuBoardPanel(session, this::handleMove);
        add(statusPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
    }

    private void handleMove(GameSession.MoveResult result) {
        statusPanel.update(session);
        if (result == GameSession.MoveResult.INCORRECT && session.isGameOver()) {
            GameEndDialogs.GameOverChoice choice = GameEndDialogs.showGameOver(this);
            GameEndDialogs.dispatchGameOverChoice(choice, onRetry, onChooseLevel);
        } else if (result == GameSession.MoveResult.WON) {
            GameEndDialogs.showWin(this);
            GameEndDialogs.dispatchWinChoice(onChooseLevel);
        }
    }
}
