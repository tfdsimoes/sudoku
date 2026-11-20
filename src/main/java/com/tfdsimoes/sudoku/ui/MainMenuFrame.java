package com.tfdsimoes.sudoku.ui;

import com.tfdsimoes.sudoku.model.Difficulty;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.JFrame;

/** Window hosting the Sudoku level-selection menu. */
public final class MainMenuFrame extends JFrame {

    /** Window title used on the level-selection menu. */
    public static final String WINDOW_TITLE = "Sudoku - Choose Your Level";

    /**
     * Creates the menu window.
     *
     * @param onDifficultySelected action invoked when a level is selected
     */
    public MainMenuFrame(Consumer<Difficulty> onDifficultySelected) {
        super(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new MainMenuPanel(onDifficultySelected));
        setMinimumSize(new Dimension(680, 420));
        setSize(860, 540);
        setLocationRelativeTo(null);
    }
}
