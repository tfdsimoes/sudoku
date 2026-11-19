package com.test.sudoku.model;

/**
 * Game difficulty. Each level maps to a range of pre-filled cells (givens)
 * the generated puzzle will have. Fewer givens means a harder puzzle.
 */
public enum Difficulty {
    EASY(38, 40),
    MEDIUM(30, 34),
    HARD(24, 28);

    private final int minGivens;
    private final int maxGivens;

    Difficulty(int minGivens, int maxGivens) {
        this.minGivens = minGivens;
        this.maxGivens = maxGivens;
    }

    public int minGivens() {
        return minGivens;
    }

    public int maxGivens() {
        return maxGivens;
    }
}
