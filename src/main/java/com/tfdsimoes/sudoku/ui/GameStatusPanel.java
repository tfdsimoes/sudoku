package com.tfdsimoes.sudoku.ui;

import com.tfdsimoes.sudoku.logic.GameSession;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/** Displays the number of strikes remaining in the active game. */
public final class GameStatusPanel extends JPanel {

    static final int FEEDBACK_HOLD_MILLIS = 3_000;
    static final int FEEDBACK_FADE_MILLIS = 1_500;
    private static final int FADE_INTERVAL_MILLIS = 50;
    private static final Color TEXT_COLOR = new Color(35, 53, 69);
    private static final Color ERROR_COLOR = new Color(139, 0, 0);
    private final JLabel strikesLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
    private final Timer feedbackTimer;
    private int fadeElapsedMillis;

    /**
     * Creates the HUD initialized from the active game session.
     *
     * @param session active game session
     */
    public GameStatusPanel(GameSession session) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        strikesLabel.setForeground(TEXT_COLOR);
        strikesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        strikesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setForeground(ERROR_COLOR);
        feedbackLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        feedbackLabel.setMinimumSize(new Dimension(0, 22));
        feedbackLabel.setPreferredSize(new Dimension(0, 22));
        feedbackLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        add(strikesLabel);
        add(feedbackLabel);
        feedbackTimer = new Timer(FADE_INTERVAL_MILLIS, event -> advanceFeedbackFade());
        feedbackTimer.setInitialDelay(FEEDBACK_HOLD_MILLIS);
        update(session);
    }

    /**
     * Refreshes the displayed strike count from the game session.
     *
     * @param session active game session
     */
    public void update(GameSession session) {
        Objects.requireNonNull(session, "session");
        strikesLabel.setText("Strikes left: " + session.getRemainingStrikes());
    }

    /**
     * Refreshes the strike count and displays feedback for the latest move.
     *
     * @param session active game session
     * @param result outcome of the latest move
     */
    public void update(GameSession session, GameSession.MoveResult result) {
        Objects.requireNonNull(result, "result");
        update(session);
        if (result == GameSession.MoveResult.INCORRECT) {
            showFeedback(session.isGameOver()
                    ? "Incorrect number. You lost your final strike; game over."
                    : "Incorrect number. One strike used. "
                    + session.getRemainingStrikes() + " strikes remaining.");
        } else if (result == GameSession.MoveResult.LOCKED_CELL) {
            showFeedback("This correct number is locked and cannot be changed.");
        } else if (result == GameSession.MoveResult.GIVEN_CELL) {
            showFeedback("Given numbers cannot be changed.");
        }
    }

    private void showFeedback(String message) {
        feedbackLabel.setText(message);
        feedbackLabel.setForeground(ERROR_COLOR);
        fadeElapsedMillis = 0;
        feedbackTimer.restart();
    }

    private void advanceFeedbackFade() {
        fadeElapsedMillis = Math.min(
                fadeElapsedMillis + FADE_INTERVAL_MILLIS, FEEDBACK_FADE_MILLIS);
        feedbackLabel.setForeground(feedbackColorAt(fadeElapsedMillis));
        if (fadeElapsedMillis == FEEDBACK_FADE_MILLIS) {
            feedbackTimer.stop();
            feedbackLabel.setText(" ");
            feedbackLabel.setForeground(ERROR_COLOR);
        }
    }

    static Color feedbackColorAt(int elapsedFadeMillis) {
        int elapsed = Math.min(Math.max(elapsedFadeMillis, 0), FEEDBACK_FADE_MILLIS);
        int alpha = 255 * (FEEDBACK_FADE_MILLIS - elapsed) / FEEDBACK_FADE_MILLIS;
        return new Color(ERROR_COLOR.getRed(), ERROR_COLOR.getGreen(),
                ERROR_COLOR.getBlue(), alpha);
    }

    String getStatusText() {
        return strikesLabel.getText();
    }

    String getFeedbackText() {
        return feedbackLabel.getText();
    }
}
