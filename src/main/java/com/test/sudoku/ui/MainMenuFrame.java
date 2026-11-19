package com.test.sudoku.ui;

import com.test.sudoku.model.Difficulty;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.JFrame;

/** Window hosting the Sudoku level-selection menu. */
public final class MainMenuFrame extends JFrame {

    public static final String WINDOW_TITLE = "Sudoku - Choose Your Level";

    public MainMenuFrame(Consumer<Difficulty> onDifficultySelected) {
        super(WINDOW_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new MainMenuPanel(onDifficultySelected));
        setMinimumSize(new Dimension(680, 420));
        setSize(860, 540);
        setLocationRelativeTo(null);
    }
}
