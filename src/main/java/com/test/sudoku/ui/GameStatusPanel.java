package com.test.sudoku.ui;

import com.test.sudoku.logic.GameSession;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Displays the number of strikes remaining in the active game. */
public final class GameStatusPanel extends JPanel {

    private static final Color TEXT_COLOR = new Color(35, 53, 69);
    private static final Color ERROR_COLOR = new Color(139, 0, 0);
    private final JLabel strikesLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel feedbackLabel = new JLabel("", SwingConstants.CENTER);

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
        add(strikesLabel);
        add(feedbackLabel);
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
        feedbackLabel.setText("");
    }

    /**
     * Refreshes the strike count and displays feedback for the latest move.
     *
     * @param session active game session
     * @param result outcome of the latest move
     */
    public void update(GameSession session, GameSession.MoveResult result) {
        update(session);
        if (result == GameSession.MoveResult.INCORRECT) {
            feedbackLabel.setText(session.isGameOver()
                    ? "Incorrect number. You lost your final strike; game over."
                    : "Incorrect number. One strike used. "
                    + session.getRemainingStrikes() + " strikes remaining.");
        }
    }

    String getStatusText() {
        return strikesLabel.getText();
    }

    String getFeedbackText() {
        return feedbackLabel.getText();
    }
}
