package com.tfdsimoes.sudoku.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.sudoku.logic.GameSession;
import com.tfdsimoes.sudoku.model.Difficulty;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    @Test
    void selectionStartsGameAndCallbacksNavigateAndRetry() {
        RecordingViewHost view = new RecordingViewHost();
        AtomicLong seed = new AtomicLong();
        GameController controller = new GameController(
                view, difficulty -> new GameSession(difficulty, seed.incrementAndGet()));

        controller.showMenu();
        assertEquals(1, view.menuCount);
        view.levelSelection.accept(Difficulty.MEDIUM);
        GameSession firstSession = view.session;
        assertNotNull(firstSession);
        assertEquals(Difficulty.MEDIUM, firstSession.getDifficulty());

        view.retry.run();
        assertNotSame(firstSession, view.session);
        assertEquals(Difficulty.MEDIUM, view.session.getDifficulty());
        assertEquals(2, view.gameCount);

        view.chooseLevel.run();
        assertEquals(2, view.menuCount);
        view.levelSelection.accept(Difficulty.HARD);
        assertEquals(Difficulty.HARD, view.session.getDifficulty());
        assertEquals(3, view.gameCount);
    }

    @Test
    void unlimitedHelpOptionIsAppliedToCreatedSessionsAndRetries() {
        RecordingViewHost view = new RecordingViewHost();
        GameController controller = new GameController(view, true);

        controller.showMenu();
        view.levelSelection.accept(Difficulty.EASY);
        assertTrue(view.session.getRemainingHints().isEmpty());
        for (int use = 0; use < GameSession.MAX_HINTS + 2; use++) {
            assertTrue(view.session.revealRandomEmptyCell().isPresent());
        }

        view.retry.run();

        assertTrue(view.session.getRemainingHints().isEmpty());
    }

    private static final class RecordingViewHost implements GameController.ViewHost {

        private Consumer<Difficulty> levelSelection;
        private GameSession session;
        private Runnable retry;
        private Runnable chooseLevel;
        private int menuCount;
        private int gameCount;

        @Override
        public void showMenu(Consumer<Difficulty> onDifficultySelected) {
            levelSelection = onDifficultySelected;
            menuCount++;
        }

        @Override
        public void showGame(GameSession gameSession, Runnable onRetry, Runnable onChooseLevel) {
            session = gameSession;
            retry = onRetry;
            chooseLevel = onChooseLevel;
            gameCount++;
        }
    }
}
