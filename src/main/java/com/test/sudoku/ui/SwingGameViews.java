package com.test.sudoku.ui;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.model.Difficulty;
import java.awt.Dimension;
import java.util.function.Consumer;

/** Swing window host used by the desktop application. */
public final class SwingGameViews implements GameController.ViewHost {

    private MainMenuFrame frame;

    @Override
    public void showMenu(Consumer<Difficulty> onDifficultySelected) {
        if (frame == null) {
            frame = new MainMenuFrame(onDifficultySelected);
        } else {
            frame.setTitle(MainMenuFrame.WINDOW_TITLE);
            frame.setContentPane(new MainMenuPanel(onDifficultySelected));
            frame.setMinimumSize(new Dimension(680, 420));
            frame.setSize(860, 540);
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
        frame.revalidate();
        frame.repaint();
    }

    @Override
    public void showGame(GameSession session, Runnable onRetry, Runnable onChooseLevel) {
        if (frame == null) {
            throw new IllegalStateException("Show the menu before starting a game");
        }
        frame.setTitle("Sudoku - " + titleCase(session.getDifficulty().name()));
        frame.setContentPane(new GameScreenPanel(session, onRetry, onChooseLevel));
        frame.setMinimumSize(new Dimension(620, 700));
        frame.setSize(780, 850);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }

    private static String titleCase(String text) {
        return text.charAt(0) + text.substring(1).toLowerCase();
    }
}
