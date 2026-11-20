package com.tfdsimoes.sudoku.model;

/**
 * Game difficulty. Each level maps to a range of pre-filled cells (givens)
 * the generated puzzle will have. Fewer givens means a harder puzzle.
 */
public enum Difficulty {
    /** 38-40 pre-filled cells. */
    EASY(38, 40),
    /** 30-34 pre-filled cells. */
    MEDIUM(30, 34),
    /** 24-28 pre-filled cells. */
    HARD(24, 28);

    private final int minGivens;
    private final int maxGivens;

    Difficulty(int minGivens, int maxGivens) {
        this.minGivens = minGivens;
        this.maxGivens = maxGivens;
    }

    /**
     * Returns the minimum number of clues used for this level.
     *
     * @return minimum clue count
     */
    public int minGivens() {
        return minGivens;
    }

    /**
     * Returns the maximum number of clues used for this level.
     *
     * @return maximum clue count
     */
    public int maxGivens() {
        return maxGivens;
    }
}
