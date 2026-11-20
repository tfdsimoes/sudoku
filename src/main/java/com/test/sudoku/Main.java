package com.test.sudoku;

import com.test.sudoku.ui.GameController;
import javax.swing.SwingUtilities;

/** Desktop application entry point. */
public class Main {

    private static final String UNLIMITED_HELP_OPTION = "--unlimited-help";

    private Main() {
    }

    /**
     * Launches the level-selection menu on Swing's event dispatch thread.
     *
     * @param args optional {@code --unlimited-help} command-line option
     */
    public static void main(String[] args) {
        boolean unlimitedHelp;
        try {
            unlimitedHelp = parseUnlimitedHelpOption(args);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
            return;
        }
        SwingUtilities.invokeLater(() -> new GameController(unlimitedHelp).showMenu());
    }

    static boolean parseUnlimitedHelpOption(String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (args.length == 1 && UNLIMITED_HELP_OPTION.equals(args[0])) {
            return true;
        }
        throw new IllegalArgumentException(
                "Usage: java -cp target\\classes com.test.sudoku.Main [--unlimited-help]");
    }
}
