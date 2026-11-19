package com.test.sudoku.logic;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import java.util.Objects;
import java.util.Random;

/**
 * Holds the state and rules for one Sudoku game.
 */
public final class GameSession {

    public static final int MAX_STRIKES = 3;

    private final SudokuGenerator generator = new SudokuGenerator();
    private final Random seedSource;
    private Difficulty difficulty;
    private Board board;
    private Board solution;
    private int strikes;
    private boolean gameOver;
    private boolean won;

    /** Starts a game with a randomly seeded puzzle. */
    public GameSession(Difficulty difficulty) {
        this(difficulty, new Random().nextLong());
    }

    /**
     * Starts a reproducible game, useful for tests and debugging.
     *
     * @param difficulty the selected difficulty
     * @param seed the random seed used for this game and subsequent games
     */
    public GameSession(Difficulty difficulty, long seed) {
        seedSource = new Random(seed);
        startNewGame(difficulty);
    }

    /** Starts a fresh puzzle and resets strikes and end-of-game state. */
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

    /** Returns the current puzzle and player entries as an independent copy. */
    public Board getBoard() {
        return board.copy();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getStrikes() {
        return strikes;
    }

    public int getRemainingStrikes() {
        return MAX_STRIKES - strikes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWon() {
        return won;
    }

    /**
     * Enters a value, or clears a player-entered cell when {@code value} is 0.
     * Incorrect values are rejected and add a strike.
     *
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
        CORRECT,
        CLEARED,
        INCORRECT,
        GIVEN_CELL,
        GAME_OVER,
        WON
    }
}
