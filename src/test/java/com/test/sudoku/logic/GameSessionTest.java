package com.test.sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import org.junit.jupiter.api.Test;

class GameSessionTest {

    @Test
    void correctMoveIsRecordedWithoutStrike() {
        GameSession session = new GameSession(Difficulty.EASY, 1);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        int[] emptyCell = findEmptyCell(puzzle);
        int row = emptyCell[0];
        int col = emptyCell[1];

        assertEquals(GameSession.MoveResult.CORRECT,
                session.enterValue(row, col, solution.getValue(row, col)));
        assertEquals(solution.getValue(row, col), session.getBoard().getValue(row, col));
        assertEquals(0, session.getStrikes());
        assertEquals(GameSession.MAX_STRIKES, session.getRemainingStrikes());
        assertFalse(session.isGameOver());
    }

    @Test
    void wrongMoveAddsStrikeAndIsRejected() {
        GameSession session = new GameSession(Difficulty.EASY, 2);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        int[] emptyCell = findEmptyCell(puzzle);
        int row = emptyCell[0];
        int col = emptyCell[1];
        int wrongValue = solution.getValue(row, col) % Board.SIZE + 1;

        assertEquals(GameSession.MoveResult.INCORRECT, session.enterValue(row, col, wrongValue));
        assertEquals(0, session.getBoard().getValue(row, col));
        assertEquals(1, session.getStrikes());
        assertEquals(GameSession.MAX_STRIKES - 1, session.getRemainingStrikes());
    }

    @Test
    void thirdStrikeEndsGameAndFurtherMovesAreRejected() {
        GameSession session = new GameSession(Difficulty.EASY, 3);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        int[] emptyCell = findEmptyCell(puzzle);
        int row = emptyCell[0];
        int col = emptyCell[1];
        int wrongValue = solution.getValue(row, col) % Board.SIZE + 1;

        for (int strike = 0; strike < GameSession.MAX_STRIKES; strike++) {
            assertEquals(GameSession.MoveResult.INCORRECT,
                    session.enterValue(row, col, wrongValue));
        }

        assertEquals(GameSession.MAX_STRIKES, session.getStrikes());
        assertTrue(session.isGameOver());
        assertFalse(session.isWon());
        assertEquals(GameSession.MoveResult.GAME_OVER,
                session.enterValue(row, col, solution.getValue(row, col)));
    }

    @Test
    void completingPuzzleWinsGame() {
        GameSession session = new GameSession(Difficulty.EASY, 4);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        GameSession.MoveResult result = GameSession.MoveResult.CORRECT;

        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!puzzle.isGiven(row, col)) {
                    result = session.enterValue(row, col, solution.getValue(row, col));
                }
            }
        }

        assertEquals(GameSession.MoveResult.WON, result);
        assertTrue(session.isWon());
        assertFalse(session.isGameOver());
        assertTrue(SudokuRules.isSolved(session.getBoard()));
        assertEquals(0, session.getStrikes());
    }

    @Test
    void givensCannotBeChanged() {
        GameSession session = new GameSession(Difficulty.EASY, 5);
        Board puzzle = session.getBoard();
        int[] givenCell = findGivenCell(puzzle);
        int row = givenCell[0];
        int col = givenCell[1];
        int originalValue = puzzle.getValue(row, col);
        int otherValue = originalValue % Board.SIZE + 1;

        assertEquals(GameSession.MoveResult.GIVEN_CELL,
                session.enterValue(row, col, otherValue));
        assertEquals(originalValue, session.getBoard().getValue(row, col));
        assertEquals(0, session.getStrikes());
    }

    @Test
    void startingNewGameResetsStateAndChangesDifficulty() {
        GameSession session = new GameSession(Difficulty.EASY, 6);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        int[] emptyCell = findEmptyCell(puzzle);
        int row = emptyCell[0];
        int col = emptyCell[1];
        int wrongValue = solution.getValue(row, col) % Board.SIZE + 1;
        session.enterValue(row, col, wrongValue);

        session.startNewGame(Difficulty.HARD);

        assertEquals(Difficulty.HARD, session.getDifficulty());
        assertEquals(0, session.getStrikes());
        assertFalse(session.isGameOver());
        assertFalse(session.isWon());
        assertTrue(session.getBoard().countGivens() >= Difficulty.HARD.minGivens());
        assertTrue(session.getBoard().countGivens() <= Difficulty.HARD.maxGivens());
        assertNotEquals(puzzle, session.getBoard());
    }

    @Test
    void clearingCellDoesNotAddStrikeAndBoardIsDefensiveCopy() {
        GameSession session = new GameSession(Difficulty.EASY, 7);
        Board puzzle = session.getBoard();
        Board solution = SudokuSolver.solve(puzzle).orElseThrow();
        int[] emptyCell = findEmptyCell(puzzle);
        int row = emptyCell[0];
        int col = emptyCell[1];
        session.enterValue(row, col, solution.getValue(row, col));

        assertEquals(GameSession.MoveResult.CLEARED, session.enterValue(row, col, 0));
        assertEquals(0, session.getStrikes());

        Board externalCopy = session.getBoard();
        externalCopy.setValue(row, col, solution.getValue(row, col));
        assertTrue(session.getBoard().isEmpty(row, col));
    }

    private static int[] findEmptyCell(Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (!board.isGiven(row, col)) {
                    return new int[] {row, col};
                }
            }
        }
        throw new AssertionError("Generated puzzle must have empty cells");
    }

    private static int[] findGivenCell(Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.isGiven(row, col)) {
                    return new int[] {row, col};
                }
            }
        }
        throw new AssertionError("Generated puzzle must have given cells");
    }
}
