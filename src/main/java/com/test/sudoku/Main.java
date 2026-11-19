package com.test.sudoku;

import com.test.sudoku.ui.GameController;
import javax.swing.SwingUtilities;

/** Desktop application entry point. */
public class Main {

    private Main() {
    }

    /**
     * Launches the level-selection menu on Swing's event dispatch thread.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController().showMenu());
    }
}
