package algorithms.mazeGenerators;

import java.util.Random;

/**
 * Generates a simple maze by randomly placing walls.
 */
public class SimpleMazeGenerator extends AMazeGenerator {

    /**
     * Generates a maze with random walls.
     * Each cell has a probability to become a wall.
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return generated Maze
     */
    @Override
    public Maze generate(int rows, int columns) {
        Maze maze = new Maze(rows, columns);
        Random rand = new Random();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                // 30% chance to be a wall
                if (rand.nextInt(100) < 30) {
                    maze.setCell(i, j, 1);
                }
            }
        }

        return maze;
    }
}
