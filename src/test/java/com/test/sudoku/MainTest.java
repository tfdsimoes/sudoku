package com.test.sudoku;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MainTest {

    @Test
    void unlimitedHelpIsDisabledByDefault() {
        assertFalse(Main.parseUnlimitedHelpOption(new String[0]));
    }

    @Test
    void unlimitedHelpOptionIsRecognized() {
        assertTrue(Main.parseUnlimitedHelpOption(new String[] {"--unlimited-help"}));
    }

    @Test
    void unsupportedCommandLineOptionsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> Main.parseUnlimitedHelpOption(new String[] {"--unknown"}));
    }
}
