package com.test.sudoku;

import com.test.sudoku.ui.GameController;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameController().showMenu());
    }
}
