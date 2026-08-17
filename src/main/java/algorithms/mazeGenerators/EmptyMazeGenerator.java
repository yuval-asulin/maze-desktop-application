package algorithms.mazeGenerators;

/**
 * Generates an empty maze (no walls at all)
 */
public class EmptyMazeGenerator extends AMazeGenerator {

    /**
     * Generates an empty maze (all cells = 0)
     *
     * @param rows number of rows
     * @param columns number of columns
     * @return empty Maze
     */
    @Override
    public Maze generate(int rows, int columns) {
        return new Maze(rows, columns);
    }
}
