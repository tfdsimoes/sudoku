package com.tfdsimoes.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.tfdsimoes.sudoku.logic.GameSession;
import com.tfdsimoes.sudoku.model.Difficulty;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class GameScreenPanelTest {

    @Test
    void newGameButtonRunsRetryActionOnly() {
        AtomicInteger retries = new AtomicInteger();
        AtomicInteger menus = new AtomicInteger();
        GameScreenPanel panel = new GameScreenPanel(
                new GameSession(Difficulty.EASY, 1L), retries::incrementAndGet, menus::incrementAndGet);

        panel.getNewGameButton().doClick();

        assertEquals("New game", panel.getNewGameButton().getText());
        assertEquals(1, retries.get());
        assertEquals(0, menus.get());
    }

    @Test
    void backToMenuButtonRunsLevelSelectionActionOnly() {
        AtomicInteger retries = new AtomicInteger();
        AtomicInteger menus = new AtomicInteger();
        GameScreenPanel panel = new GameScreenPanel(
                new GameSession(Difficulty.EASY, 1L), retries::incrementAndGet, menus::incrementAndGet);

        panel.getBackToMenuButton().doClick();

        assertEquals("Back to menu", panel.getBackToMenuButton().getText());
        assertEquals(0, retries.get());
        assertEquals(1, menus.get());
    }
}
