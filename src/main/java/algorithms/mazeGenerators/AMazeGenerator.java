package algorithms.mazeGenerators;

/**
 * Abstract class for maze generators.
 * Provides a common implementation for measuring algorithm runtime.
 */
public abstract class AMazeGenerator implements IMazeGenerator {

    /**
     * Measures the time it takes to generate a maze.
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return time in milliseconds
     */
    @Override
    public long measureAlgorithmTimeMillis(int rows, int columns) {
        long start = System.currentTimeMillis();

        generate(rows, columns); // calling the algorithm

        long end = System.currentTimeMillis();

        return end - start;
    }

    /**
     * Abstract method for generating a maze.
     * Must be implemented by subclasses.
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return generated Maze
     */
    @Override
    public abstract Maze generate(int rows, int columns);
}
