package com.test.sudoku.ui;

import com.test.sudoku.logic.GameSession;
import com.test.sudoku.model.Difficulty;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/** Coordinates level selection, game creation, retry, and return-to-menu navigation. */
public final class GameController {

    private final ViewHost viewHost;
    private final Function<Difficulty, GameSession> sessionFactory;

    public GameController() {
        this(new SwingGameViews());
    }

    public GameController(ViewHost viewHost) {
        this(viewHost, GameSession::new);
    }

    public GameController(ViewHost viewHost, Function<Difficulty, GameSession> sessionFactory) {
        this.viewHost = Objects.requireNonNull(viewHost, "viewHost");
        this.sessionFactory = Objects.requireNonNull(sessionFactory, "sessionFactory");
    }

    /** Displays the level-selection menu. */
    public void showMenu() {
        viewHost.showMenu(this::startGame);
    }

    private void startGame(Difficulty difficulty) {
        Difficulty selectedDifficulty = Objects.requireNonNull(difficulty, "difficulty");
        GameSession session = Objects.requireNonNull(
                sessionFactory.apply(selectedDifficulty), "sessionFactory result");
        viewHost.showGame(
                session,
                () -> startGame(selectedDifficulty),
                this::showMenu);
    }

    /** View operations used by the controller, replaceable for navigation tests. */
    public interface ViewHost {
        void showMenu(Consumer<Difficulty> onDifficultySelected);

        void showGame(GameSession session, Runnable onRetry, Runnable onChooseLevel);
    }
}
