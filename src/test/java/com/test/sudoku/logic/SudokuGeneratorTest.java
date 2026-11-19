package com.test.sudoku.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.test.sudoku.model.Board;
import com.test.sudoku.model.Difficulty;
import org.junit.jupiter.api.Test;

class SudokuGeneratorTest {

    private final SudokuGenerator generator = new SudokuGenerator();

    @Test
    void generatedPuzzleHasUniqueValidSolutionAndDifficultyGivens() {
        long seed = 1200;
        for (Difficulty difficulty : Difficulty.values()) {
            Board puzzle = generator.generate(difficulty, seed++);

            assertTrue(puzzle.countGivens() >= difficulty.minGivens());
            assertTrue(puzzle.countGivens() <= difficulty.maxGivens());
            assertTrue(SudokuSolver.hasUniqueSolution(puzzle));
            Board solution = SudokuSolver.solve(puzzle).orElseThrow();
            assertTrue(SudokuRules.isSolved(solution));
            assertGivensPreserved(puzzle, solution);
        }
    }

    @Test
    void sameSeedProducesSamePuzzle() {
        assertEquals(generator.generate(Difficulty.EASY, 7),
                generator.generate(Difficulty.EASY, 7));
    }

    @Test
    void generatedPuzzleHasUniqueSolutionWithoutExplicitSeed() {
        Board puzzle = generator.generate(Difficulty.MEDIUM);

        assertTrue(puzzle.countGivens() >= Difficulty.MEDIUM.minGivens());
        assertTrue(puzzle.countGivens() <= Difficulty.MEDIUM.maxGivens());
        assertTrue(SudokuSolver.hasUniqueSolution(puzzle));
    }

    @Test
    void differentSeedsProduceDifferentPuzzles() {
        assertNotEquals(generator.generate(Difficulty.EASY, 7),
                generator.generate(Difficulty.EASY, 8));
    }

    private static void assertGivensPreserved(Board puzzle, Board solution) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (puzzle.isGiven(row, col)) {
                    assertEquals(puzzle.getValue(row, col), solution.getValue(row, col));
                }
            }
        }
    }
}
