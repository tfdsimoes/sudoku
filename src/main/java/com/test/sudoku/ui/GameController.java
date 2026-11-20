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

    /** Creates a controller using the desktop Swing views. */
    public GameController() {
        this(new SwingGameViews());
    }

    /**
     * Creates a controller for the desktop application.
     *
     * @param unlimitedHelp whether game sessions have unlimited Help uses
     */
    public GameController(boolean unlimitedHelp) {
        this(new SwingGameViews(), unlimitedHelp);
    }

    /**
     * Creates a controller with replaceable views and a fixed Help allowance.
     *
     * @param viewHost target for menu and game screens
     * @param unlimitedHelp whether game sessions have unlimited Help uses
     */
    public GameController(ViewHost viewHost, boolean unlimitedHelp) {
        this(viewHost, difficulty -> new GameSession(difficulty, unlimitedHelp));
    }

    /**
     * Creates a controller using the supplied view host.
     *
     * @param viewHost target for menu and game screens
     */
    public GameController(ViewHost viewHost) {
        this(viewHost, GameSession::new);
    }

    /**
     * Creates a controller with replaceable views and session creation.
     *
     * @param viewHost target for menu and game screens
     * @param sessionFactory creates a session for the selected difficulty
     */
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
        /**
         * Displays the menu and forwards a selected difficulty.
         *
         * @param onDifficultySelected callback for the chosen level
         */
        void showMenu(Consumer<Difficulty> onDifficultySelected);

        /**
         * Displays a game and provides its retry and level-selection actions.
         *
         * @param session active game session
         * @param onRetry starts another game at the same difficulty
         * @param onChooseLevel returns to the level-selection menu
         */
        void showGame(GameSession session, Runnable onRetry, Runnable onChooseLevel);
    }
}
