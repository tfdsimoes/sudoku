# sudoku
Game of Sudoku in Java

## Requirements

- JDK 25
- Apache Maven 3.9+

## Build and test

```powershell
mvn test
```

## Run

```powershell
mvn compile
java -cp target\classes com.test.sudoku.Main
```

## Puzzle generation

The generator creates a randomized, valid completed grid, then removes values while
checking that exactly one solution remains. Difficulty is based on the number of
pre-filled cells: Easy has 38–40, Medium 30–34, and Hard 24–28. A seeded generator
overload is available to reproduce puzzles in tests.

## Game session

`GameSession` owns the current puzzle and solution, accepts only correct player
entries, and records an incorrect entry as a strike without changing the board.
After three strikes, further moves are rejected until a new game is started.
Givens cannot be edited; entering 0 clears a player-entered cell.

## Menu

The Swing menu offers Easy, Medium, and Hard selections and reports the selected
difficulty to its host window. Each option shows its clue range and a short level
description.

## Board controls

The board uses a white background with black numbers and grid lines; thicker lines
separate the 3x3 boxes. Select a cell, then enter 1-9 on the keyboard or use the
on-screen number pad. Backspace, Delete, or the Clear button removes a player entry.
