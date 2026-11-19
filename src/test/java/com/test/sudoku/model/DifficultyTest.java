package com.test.sudoku.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DifficultyTest {

    @Test
    void levelsHaveExpectedGivenRanges() {
        assertEquals(38, Difficulty.EASY.minGivens());
        assertEquals(40, Difficulty.EASY.maxGivens());
        assertEquals(30, Difficulty.MEDIUM.minGivens());
        assertEquals(34, Difficulty.MEDIUM.maxGivens());
        assertEquals(24, Difficulty.HARD.minGivens());
        assertEquals(28, Difficulty.HARD.maxGivens());
    }

    @Test
    void harderLevelsHaveFewerGivens() {
        assertTrue(Difficulty.EASY.minGivens() > Difficulty.MEDIUM.maxGivens());
        assertTrue(Difficulty.MEDIUM.minGivens() > Difficulty.HARD.maxGivens());
    }
}
