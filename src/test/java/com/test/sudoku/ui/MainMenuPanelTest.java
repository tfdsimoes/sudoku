package com.test.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.test.sudoku.model.Difficulty;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import org.junit.jupiter.api.Test;

class MainMenuPanelTest {

    @Test
    void eachLevelButtonReportsItsDifficulty() throws Exception {
        AtomicReference<Difficulty> selected = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            MainMenuPanel panel = new MainMenuPanel(selected::set);

            for (Difficulty difficulty : Difficulty.values()) {
                var button = panel.getDifficultyButton(difficulty);
                assertNotNull(button);
                button.doClick();
                assertEquals(difficulty, selected.get());
            }
        });
    }
}
