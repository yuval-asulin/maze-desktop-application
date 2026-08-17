package algorithms.mazeGenerators;

/**
 * Interface for maze generating algorithms
 */
public interface IMazeGenerator {

    /**
     * Generates a maze with given dimensions
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return generated Maze object
     */
    Maze generate(int rows, int columns);

    /**
     * Measures the time (in milliseconds) it takes to generate a maze
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return time in milliseconds
     */
    long measureAlgorithmTimeMillis(int rows, int columns);
}
