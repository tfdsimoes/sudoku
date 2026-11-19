package com.test.sudoku.ui;

import com.test.sudoku.logic.GameSession;
import java.awt.Color;
import java.awt.Font;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/** Displays the number of strikes remaining in the active game. */
public final class GameStatusPanel extends JPanel {

    private static final Color TEXT_COLOR = new Color(35, 53, 69);
    private final JLabel strikesLabel = new JLabel("", SwingConstants.CENTER);

    /**
     * Creates the HUD initialized from the active game session.
     *
     * @param session active game session
     */
    public GameStatusPanel(GameSession session) {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        strikesLabel.setForeground(TEXT_COLOR);
        strikesLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        add(strikesLabel);
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

    String getStatusText() {
        return strikesLabel.getText();
    }
}
