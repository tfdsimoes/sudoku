# Sudoku

A Java 25 desktop Sudoku game built with Swing.

## Rules

- The board is a 9x9 grid divided into nine 3x3 boxes.
- Every row, column, and box must contain each digit from 1 to 9 exactly once.
- Pre-filled clues are fixed and cannot be changed.
- A generated puzzle has exactly one solution. An entered value must match that
  solution; an incorrect entry is rejected and adds a strike.
- The game ends after three strikes. A completed puzzle returns to level selection.

## Difficulty and generation

| Level | Pre-filled clues |
|---|---:|
| Easy | 38-40 |
| Medium | 30-34 |
| Hard | 24-28 |

The generator randomizes a complete valid grid, then removes clues only when the
remaining puzzle still has a unique solution. Clue count is the difficulty
measure used by this project. If the chosen exact clue target is unreachable, it
keeps another unique puzzle within the selected difficulty range when available.

## Controls

- Select a cell with the left mouse button; it highlights immediately on press.
  Dragging across the board does not move the selection. The game asks you to
  select a cell first, and ignores number input until one is selected.
- Enter a digit with the keyboard's number row or numeric keypad.
- Once a correct value is entered or revealed by Help, that cell is locked.
  Further input cannot change it or add a strike.
- Click Help to reveal the correct value in a random empty cell without adding a
  strike. You can use Help three times per game; the button shows remaining uses
  and is disabled when they run out. The revealed cell becomes selected.
- An incorrect number displays feedback explaining that a strike was used and
  how many remain; the message has reserved space so the board does not resize,
  stays visible for three seconds, then fades away gradually. The third strike
  displays a game-over message.
- Given numbers are black, player-entered numbers are dark blue, and Help-revealed
  numbers are dark red. The board is white, with thicker black lines separating
  the 3x3 boxes; the selected cell is highlighted in light gray.

## Build and run

Requirements: JDK 25 and Apache Maven 3.9 or newer.

```powershell
mvn test
mvn compile
java -cp target\classes com.test.sudoku.Main
java -cp target\classes com.test.sudoku.Main --unlimited-help
```

The application opens at the level-selection menu. Choose Easy, Medium, or Hard
to start a game. After a loss, choose Try again for a new puzzle at the same
difficulty or Choose level to return to the menu. The default launch allows
three Help uses per game; add `--unlimited-help` to run with unlimited Help,
displayed as `Help (∞)`.

## Project structure

```text
src/main/java/com/test/sudoku/
  Main.java                 Application entry point
  logic/                    Rules, solver, generator, and game-session logic
  model/                    Board, difficulty, and cell-position types
  ui/                       Swing menu, board, HUD, dialogs, and navigation
src/test/java/com/test/sudoku/
  logic/                    Model and game-logic tests
  ui/                       Swing component and navigation tests
```

The generator exposes a seeded overload so puzzles can be reproduced in tests.

## Repository hygiene

Local IntelliJ project settings (`.idea/` and `*.iml`) and Maven build output are
ignored rather than committed.

## Verification

Run the complete Maven verification lifecycle, including the automated tests:

```powershell
mvn verify
```

For a desktop smoke test, launch the app and check each difficulty, repeated cell
selection without leftover highlights, correct and incorrect entries, the
three-strike game-over choices, retry, and the win dialog.

GitHub Actions also runs `mvn verify` for pull requests and pushes to `main` or
`develop`.
