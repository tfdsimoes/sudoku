package com.tfdsimoes.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class GameEndDialogsTest {

    @Test
    void retryChoiceRunsOnlyRetryAction() {
        AtomicInteger retries = new AtomicInteger();
        AtomicInteger levelSelections = new AtomicInteger();

        GameEndDialogs.dispatchGameOverChoice(
                GameEndDialogs.GameOverChoice.RETRY,
                retries::incrementAndGet,
                levelSelections::incrementAndGet);

        assertEquals(1, retries.get());
        assertEquals(0, levelSelections.get());
    }

    @Test
    void chooseLevelChoiceRunsOnlyLevelAction() {
        AtomicInteger retries = new AtomicInteger();
        AtomicInteger levelSelections = new AtomicInteger();

        GameEndDialogs.dispatchGameOverChoice(
                GameEndDialogs.GameOverChoice.CHOOSE_LEVEL,
                retries::incrementAndGet,
                levelSelections::incrementAndGet);

        assertEquals(0, retries.get());
        assertEquals(1, levelSelections.get());
    }

    @Test
    void winChoiceReturnsToLevelSelection() {
        AtomicInteger levelSelections = new AtomicInteger();

        GameEndDialogs.dispatchWinChoice(levelSelections::incrementAndGet);

        assertEquals(1, levelSelections.get());
    }
}
