package com.tfdsimoes.sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfdsimoes.sudoku.model.Board;
import com.tfdsimoes.sudoku.model.Difficulty;
import org.junit.jupiter.api.Test;

class GameFlowIntegrationTest {

    @Test
    void everyDifficultySupportsUniquePuzzleWinAndLossFlows() {
        long seed = 300;
        for (Difficulty difficulty : Difficulty.values()) {
            GameSession winningSession = new GameSession(difficulty, seed++);
            Board puzzle = winningSession.getBoard();
            Board solution = SudokuSolver.solve(puzzle).orElseThrow();

            assertTrue(puzzle.countGivens() >= difficulty.minGivens());
            assertTrue(puzzle.countGivens() <= difficulty.maxGivens());
            assertTrue(SudokuSolver.hasUniqueSolution(puzzle));
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    if (!puzzle.isGiven(row, col)) {
                        winningSession.enterValue(row, col, solution.getValue(row, col));
                    }
                }
            }
            assertTrue(winningSession.isWon());
            assertFalse(winningSession.isGameOver());

            GameSession losingSession = new GameSession(difficulty, seed++);
            Board losingPuzzle = losingSession.getBoard();
            Board losingSolution = SudokuSolver.solve(losingPuzzle).orElseThrow();
            int[] emptyCell = findEmptyCell(losingPuzzle);
            int wrongValue = losingSolution.getValue(emptyCell[0], emptyCell[1])
                    % Board.SIZE + 1;
            for (int strike = 0; strike < GameSession.MAX_STRIKES; strike++) {
                losingSession.enterValue(emptyCell[0], emptyCell[1], wrongValue);
            }
            assertTrue(losingSession.isGameOver());
            assertFalse(losingSession.isWon());
            assertEquals(GameSession.MAX_STRIKES, losingSession.getStrikes());
        }
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
}
