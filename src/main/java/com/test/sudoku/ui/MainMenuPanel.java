package com.test.sudoku.ui;

import com.test.sudoku.model.Difficulty;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Styled level-selection menu. The host supplies the action for a selected level.
 */
public final class MainMenuPanel extends JPanel {

    private static final Color BACKGROUND = new Color(20, 32, 45);
    private static final Color CARD_BACKGROUND = new Color(35, 53, 69);
    private static final Color PRIMARY_TEXT = new Color(246, 244, 235);
    private static final Color SECONDARY_TEXT = new Color(174, 190, 196);
    private static final Color ACCENT = new Color(229, 178, 76);
    private static final Map<Difficulty, String> DESCRIPTIONS = Map.of(
            Difficulty.EASY, "A gentle warm-up",
            Difficulty.MEDIUM, "A balanced challenge",
            Difficulty.HARD, "For Sudoku pros");

    private final Map<Difficulty, JButton> difficultyButtons = new EnumMap<>(Difficulty.class);

    public MainMenuPanel(Consumer<Difficulty> onDifficultySelected) {
        Objects.requireNonNull(onDifficultySelected, "onDifficultySelected");
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(42, 48, 34, 48));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.add(createHeader());
        content.add(Box.createVerticalStrut(38));
        content.add(createDifficultyChoices(onDifficultySelected));
        content.add(Box.createVerticalStrut(26));
        content.add(createFooter());
        add(content, BorderLayout.CENTER);
    }

    JButton getDifficultyButton(Difficulty difficulty) {
        return difficultyButtons.get(difficulty);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("A LITTLE LOGIC GOES A LONG WAY");
        eyebrow.setAlignmentX(CENTER_ALIGNMENT);
        eyebrow.setForeground(ACCENT);
        eyebrow.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));

        JLabel title = new JLabel("SUDOKU");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setForeground(PRIMARY_TEXT);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));

        JLabel subtitle = new JLabel("Choose your level");
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setForeground(SECONDARY_TEXT);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 17));

        header.add(eyebrow);
        header.add(Box.createVerticalStrut(8));
        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        return header;
    }

    private JPanel createDifficultyChoices(Consumer<Difficulty> onDifficultySelected) {
        JPanel choices = new JPanel(new GridLayout(1, Difficulty.values().length, 16, 0));
        choices.setOpaque(false);
        choices.setMaximumSize(new Dimension(820, 190));
        for (Difficulty difficulty : Difficulty.values()) {
            JButton button = createDifficultyButton(difficulty, onDifficultySelected);
            difficultyButtons.put(difficulty, button);
            choices.add(button);
        }
        return choices;
    }

    private JButton createDifficultyButton(Difficulty difficulty,
                                           Consumer<Difficulty> onDifficultySelected) {
        String level = titleCase(difficulty.name());
        String clueRange = difficulty.minGivens() + "-" + difficulty.maxGivens() + " clues";
        String label = "<html><center><font size='+2'><b>" + level + "</b></font><br>"
                + clueRange + "<br><br><font size='2'>" + DESCRIPTIONS.get(difficulty)
                + "</font></center></html>";
        JButton button = new JButton(label);
        button.setActionCommand(difficulty.name());
        button.getAccessibleContext().setAccessibleName(level + " difficulty");
        button.getAccessibleContext().setAccessibleDescription(
                clueRange + ", " + DESCRIPTIONS.get(difficulty));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setForeground(PRIMARY_TEXT);
        button.setBackground(CARD_BACKGROUND);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 91, 104), 1),
                BorderFactory.createEmptyBorder(20, 12, 20, 12)));
        button.addActionListener(event -> onDifficultySelected.accept(difficulty));
        return button;
    }

    private JLabel createFooter() {
        JLabel footer = new JLabel("Every puzzle has one solution. Take your time.", SwingConstants.CENTER);
        footer.setAlignmentX(CENTER_ALIGNMENT);
        footer.setForeground(SECONDARY_TEXT);
        footer.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        return footer;
    }

    private static String titleCase(String text) {
        return text.charAt(0) + text.substring(1).toLowerCase();
    }
}
