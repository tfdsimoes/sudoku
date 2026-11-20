package com.tfdsimoes.sudoku.logic;

import com.tfdsimoes.sudoku.model.Board;
import com.tfdsimoes.sudoku.model.Difficulty;
import com.tfdsimoes.sudoku.model.Position;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.Set;

/**
 * Holds the state and rules for one Sudoku game.
 */
public final class GameSession {

    /** Maximum incorrect entries allowed before the game ends. */
    public static final int MAX_STRIKES = 3;

    /** Maximum Help hints available in one game. */
    public static final int MAX_HINTS = 3;

    private final SudokuGenerator generator = new SudokuGenerator();
    private final Random seedSource;
    private final Set<Position> hintedCells = new HashSet<>();
    private final boolean unlimitedHelp;
    private Difficulty difficulty;
    private Board board;
    private Board solution;
    private int strikes;
    private int remainingHints;
    private boolean gameOver;
    private boolean won;

    /**
     * Starts a game with a randomly seeded puzzle.
     *
     * @param difficulty the selected difficulty
     */
    public GameSession(Difficulty difficulty) {
        this(difficulty, new Random().nextLong(), false);
    }

    /**
     * Starts a game with randomly seeded puzzle generation and optional unlimited Help.
     *
     * @param difficulty the selected difficulty
     * @param unlimitedHelp whether Help hints are unlimited
     */
    public GameSession(Difficulty difficulty, boolean unlimitedHelp) {
        this(difficulty, new Random().nextLong(), unlimitedHelp);
    }

    /**
     * Starts a reproducible game, useful for tests and debugging.
     *
     * @param difficulty the selected difficulty
     * @param seed the random seed used for this game and subsequent games
     * @throws NullPointerException if difficulty is null
     */
    public GameSession(Difficulty difficulty, long seed) {
        this(difficulty, seed, false);
    }

    /**
     * Starts a reproducible game with optional unlimited Help.
     *
     * @param difficulty the selected difficulty
     * @param seed the random seed used for this game and subsequent games
     * @param unlimitedHelp whether Help hints are unlimited
     * @throws NullPointerException if difficulty is null
     */
    public GameSession(Difficulty difficulty, long seed, boolean unlimitedHelp) {
        seedSource = new Random(seed);
        this.unlimitedHelp = unlimitedHelp;
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
        remainingHints = MAX_HINTS;
        gameOver = false;
        won = false;
        hintedCells.clear();
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
     * Returns whether a non-empty cell was filled by the Help control.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @return whether the cell currently contains a revealed hint
     * @throws IndexOutOfBoundsException if the cell is outside the board
     */
    public boolean isHint(int row, int col) {
        board.getValue(row, col);
        return hintedCells.contains(new Position(row, col));
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
     * Returns the number of Help hints remaining in the active game.
     *
     * @return remaining Help uses, or empty when Help is unlimited
     */
    public OptionalInt getRemainingHints() {
        return unlimitedHelp ? OptionalInt.empty() : OptionalInt.of(remainingHints);
    }

    /**
     * Reveals the solution value in a randomly selected empty cell.
     *
     * @return the revealed hint, or empty if the game ended, no cells remain, or no uses remain
     */
    public Optional<Hint> revealRandomEmptyCell() {
        if (gameOver || won || (!unlimitedHelp && remainingHints == 0)) {
            return Optional.empty();
        }

        List<Position> emptyCells = new ArrayList<>();
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    emptyCells.add(new Position(row, col));
                }
            }
        }
        if (emptyCells.isEmpty()) {
            return Optional.empty();
        }

        Position position = emptyCells.get(seedSource.nextInt(emptyCells.size()));
        int value = solution.getValue(position.row(), position.col());
        MoveResult result = enterValue(position.row(), position.col(), value);
        if (result != MoveResult.CORRECT && result != MoveResult.WON) {
            throw new IllegalStateException("Could not apply generated hint: " + result);
        }
        hintedCells.add(position);
        if (!unlimitedHelp) {
            remainingHints--;
        }
        return Optional.of(new Hint(position, value, result));
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
     * Enters a value in an empty cell. Filled cells, including accepted correct entries, are locked.
     * Incorrect values are rejected and add a strike.
     *
     * @param row zero-based row index
     * @param col zero-based column index
     * @param value 0 to keep an empty cell empty or a digit from 1 to 9
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
        if (!board.isEmpty(row, col)) {
            return MoveResult.LOCKED_CELL;
        }
        if (value == 0) {
            board.setValue(row, col, 0);
            hintedCells.remove(new Position(row, col));
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
        hintedCells.remove(new Position(row, col));
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
        /** The selected cell was already empty. */
        CLEARED,
        /** The value was rejected and a strike was recorded. */
        INCORRECT,
        /** The selected cell is a fixed clue. */
        GIVEN_CELL,
        /** The cell already contains a correct value and cannot be changed. */
        LOCKED_CELL,
        /** The game has already ended from strikes. */
        GAME_OVER,
        /** The puzzle was completed, or the game was already won. */
        WON
    }

    /** A correct value revealed in an empty cell. */
    public record Hint(Position position, int value, MoveResult result) { }
}
