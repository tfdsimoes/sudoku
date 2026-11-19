package com.test.sudoku.ui;

import java.awt.Component;
import java.util.Objects;
import javax.swing.JOptionPane;

/** Presents end-of-game dialogs and dispatches the selected follow-up action. */
public final class GameEndDialogs {

    private static final String[] GAME_OVER_OPTIONS = {"Try again", "Choose level"};
    private static final String[] WIN_OPTIONS = {"Choose level"};

    private GameEndDialogs() {
    }

    /**
     * Shows the game-over dialog; closing it is treated as choosing a level.
     *
     * @param parent parent component for the dialog
     * @return selected follow-up action
     */
    public static GameOverChoice showGameOver(Component parent) {
        int selected = JOptionPane.showOptionDialog(
                parent,
                "You used all three strikes.",
                "Game over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                GAME_OVER_OPTIONS,
                GAME_OVER_OPTIONS[0]);
        return selected == 0 ? GameOverChoice.RETRY : GameOverChoice.CHOOSE_LEVEL;
    }

    /**
     * Shows the win dialog before returning to level selection.
     *
     * @param parent parent component for the dialog
     */
    public static void showWin(Component parent) {
        JOptionPane.showOptionDialog(
                parent,
                "Puzzle complete. Well done!",
                "You solved it",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                WIN_OPTIONS,
                WIN_OPTIONS[0]);
    }

    /**
     * Runs the action associated with a game-over choice.
     *
     * @param choice selected follow-up action
     * @param onRetry starts a new game at the same level
     * @param onChooseLevel returns to level selection
     */
    public static void dispatchGameOverChoice(GameOverChoice choice,
                                              Runnable onRetry,
                                              Runnable onChooseLevel) {
        Objects.requireNonNull(choice, "choice");
        Objects.requireNonNull(onRetry, "onRetry");
        Objects.requireNonNull(onChooseLevel, "onChooseLevel");
        switch (choice) {
            case RETRY -> onRetry.run();
            case CHOOSE_LEVEL -> onChooseLevel.run();
        }
    }

    /**
     * Runs after the win dialog has been dismissed.
     *
     * @param onChooseLevel returns to level selection
     */
    public static void dispatchWinChoice(Runnable onChooseLevel) {
        Objects.requireNonNull(onChooseLevel, "onChooseLevel").run();
    }

    /** Available follow-up actions after a loss. */
    public enum GameOverChoice {
        /** Starts a new puzzle at the same difficulty. */
        RETRY,
        /** Returns to level selection. */
        CHOOSE_LEVEL
    }
}
