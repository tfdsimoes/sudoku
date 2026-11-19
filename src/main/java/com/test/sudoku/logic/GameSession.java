package com.test.sudoku.logic;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import java.util.Objects;
import java.util.Random;

/**
 * Holds the state and rules for one Sudoku game.
 */
public final class GameSession {

    /** Maximum incorrect entries allowed before the game ends. */
    public static final int MAX_STRIKES = 3;

    private final SudokuGenerator generator = new SudokuGenerator();
    private final Random seedSource;
    private Difficulty difficulty;
    private Board board;
    private Board solution;
    private int strikes;
    private boolean gameOver;
    private boolean won;

    /**
     * Starts a game with a randomly seeded puzzle.
     *
     * @param difficulty the selected difficulty
     */
    public GameSession(Difficulty difficulty) {
        this(difficulty, new Random().nextLong());
    }

    /**
     * Starts a reproducible game, useful for tests and debugging.
     *
     * @param difficulty the selected difficulty
     * @param seed the random seed used for this game and subsequent games
     * @throws NullPointerException if difficulty is null
     */
    public GameSession(Difficulty difficulty, long seed) {
        seedSource = new Random(seed);
        startNewGame(difficulty);
    }

    /**
     * Starts a fresh puzzle and resets strikes and end-of-game state.
     *
     * @param difficulty the selected difficulty
     * @throws NullPointerException if difficulty is null
     */
    public void startNewGame(Difficulty difficulty) {
        Difficulty newDifficulty = Objects.requireNonNull(difficulty, "difficulty");
        Board newBoard = generator.generate(newDifficulty, seedSource.nextLong());
        Board newSolution = SudokuSolver.solve(newBoard)
                .orElseThrow(() -> new IllegalStateException("Generated puzzle has no solution"));
        this.difficulty = newDifficulty;
        board = newBoard;
        solution = newSolution;
        strikes = 0;
        gameOver = false;
        won = false;
    }

    /**
     * Returns the current puzzle and player entries as an independent copy.
     *
     * @return independent copy of the current board
     */
    public Board getBoard() {
        return board.copy();
    }

    /**
     * Returns the difficulty of the active game.
     *
     * @return current difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the number of incorrect entries so far.
     *
     * @return strikes recorded
     */
    public int getStrikes() {
        return strikes;
    }

    /**
     * Returns how many incorrect entries remain before game over.
     *
     * @return remaining strikes
     */
    public int getRemainingStrikes() {
        return MAX_STRIKES - strikes;
    }

    /**
     * Returns whether the active game has ended due to three strikes.
     *
     * @return whether the session is game over
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Returns whether the player completed the puzzle.
     *
     * @return whether the session is won
     */
    public boolean isWon() {
        return won;
    }

    /**
     * Enters a value, or clears a player-entered cell when {@code value} is 0.
     * Incorrect values are rejected and add a strike.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @param value 0 to clear or a digit from 1 to 9
     * @return the outcome of the attempted move
     * @throws IllegalArgumentException if value is outside 0-9
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    public MoveResult enterValue(int row, int col, int value) {
        board.getValue(row, col);
        if (value < 0 || value > Board.SIZE) {
            throw new IllegalArgumentException("Value must be between 0 and " + Board.SIZE + ": " + value);
        }
        if (gameOver) {
            return MoveResult.GAME_OVER;
        }
        if (won) {
            return MoveResult.WON;
        }
        if (board.isGiven(row, col)) {
            return MoveResult.GIVEN_CELL;
        }
        if (value == 0) {
            board.setValue(row, col, 0);
            return MoveResult.CLEARED;
        }
        if (value != solution.getValue(row, col)) {
            strikes++;
            if (strikes == MAX_STRIKES) {
                gameOver = true;
            }
            return MoveResult.INCORRECT;
        }

        board.setValue(row, col, value);
        if (SudokuRules.isSolved(board)) {
            won = true;
            return MoveResult.WON;
        }
        return MoveResult.CORRECT;
    }

    /** Outcome of an attempted value entry. */
    public enum MoveResult {
        /** The correct value was accepted. */
        CORRECT,
        /** A player-entered value was cleared. */
        CLEARED,
        /** The value was rejected and a strike was recorded. */
        INCORRECT,
        /** The selected cell is a fixed clue. */
        GIVEN_CELL,
        /** The game has already ended from strikes. */
        GAME_OVER,
        /** The puzzle was completed, or the game was already won. */
        WON
    }
}
