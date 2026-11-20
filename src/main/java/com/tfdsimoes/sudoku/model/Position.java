package com.tfdsimoes.sudoku.model;

/**
 * A cell position on the board.
 *
 * @param row row index, 0-8
 * @param col column index, 0-8
 */
public record Position(int row, int col) {
}
