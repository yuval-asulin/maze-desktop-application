package algorithms.mazeGenerators;

import java.util.Random;

/**
 * Generates a maze using Depth-First Search (recursive backtracking).
 * This algorithm creates a maze with paths, dead-ends, and branches.
 */
public class MyMazeGenerator extends AMazeGenerator {

    private static final int[][] DIRECTIONS = {
            {0, 2}, {0, -2}, {2, 0}, {-2, 0}
    };

    private Random rand = new Random();

    /**
     * Generates a maze using DFS algorithm.
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return generated Maze
     */
    @Override
    public Maze generate(int rows, int columns) {
        Maze maze = new Maze(rows, columns);

        // Fill maze with walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                maze.setCell(i, j, 1);
            }
        }

        // Start carving from the entrance
        carvePath(maze, 0, 0);
        maze.setCell(rows - 1, columns - 1, 0);
        return maze;
    }

    /**
     * Recursively carves paths in the maze using DFS.
     *
     * @param maze the maze object
     * @param row current row
     * @param col current column
     */
    private void carvePath(Maze maze, int row, int col) {
        maze.setCell(row, col, 0);

        shuffleDirections();

        for (int[] dir : DIRECTIONS) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValid(maze, newRow, newCol)) {

                // Remove wall between current cell and next cell
                maze.setCell(row + dir[0] / 2, col + dir[1] / 2, 0);

                carvePath(maze, newRow, newCol);
            }
        }
    }

    /**
     * Checks if a cell is within bounds and still a wall.
     *
     * @param maze the maze object
     * @param row row index
     * @param col column index
     * @return true if the cell can be carved
     */
    private boolean isValid(Maze maze, int row, int col) {
        return row >= 0 &&
                row < maze.getGoalPosition().getRowIndex() + 1 &&
                col >= 0 &&
                col < maze.getGoalPosition().getColumnIndex() + 1 &&
                maze.getCell(row, col) == 1;
    }

    /**
     * Randomizes the order of directions for DFS traversal.
     */
    private void shuffleDirections() {
        for (int i = DIRECTIONS.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int[] temp = DIRECTIONS[i];
            DIRECTIONS[i] = DIRECTIONS[j];
            DIRECTIONS[j] = temp;
        }
    }
}
