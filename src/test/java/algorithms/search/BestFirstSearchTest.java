package algorithms.search;

import algorithms.mazeGenerators.*;
import algorithms.search.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BestFirstSearch algorithm
 */
public class BestFirstSearchTest {

    /**
     * Test that a solution is found for a valid maze
     */
    @Test
    public void testSolutionExists() {
        IMazeGenerator generator = new MyMazeGenerator();
        Maze maze = generator.generate(20, 20);

        SearchableMaze searchable = new SearchableMaze(maze);
        ISearchingAlgorithm best = new BestFirstSearch();

        Solution solution = best.solve(searchable);

        assertNotNull(solution);
        assertNotNull(solution.getSolutionPath());
    }

    /**
     * Test that solution starts at the maze start position
     */
    @Test
    public void testStartPosition() {
        IMazeGenerator generator = new MyMazeGenerator();
        Maze maze = generator.generate(20, 20);

        SearchableMaze searchable = new SearchableMaze(maze);
        ISearchingAlgorithm best = new BestFirstSearch();

        Solution solution = best.solve(searchable);

        assertEquals(
                maze.getStartPosition().toString(),
                solution.getSolutionPath().get(0).toString()
        );
    }

    /**
     * Test that solution ends at the goal position
     */
    @Test
    public void testGoalPosition() {
        IMazeGenerator generator = new MyMazeGenerator();
        Maze maze = generator.generate(20, 20);

        SearchableMaze searchable = new SearchableMaze(maze);
        ISearchingAlgorithm best = new BestFirstSearch();

        Solution solution = best.solve(searchable);

        int last = solution.getSolutionPath().size() - 1;

        assertEquals(
                maze.getGoalPosition().toString(),
                solution.getSolutionPath().get(last).toString()
        );
    }

    /**
     * Test behavior when domain is null
     */
    @Test
    public void testNullDomain() {
        ISearchingAlgorithm best = new BestFirstSearch();

        assertThrows(NullPointerException.class, () -> {
            best.solve(null);
        });
    }

    /**
     * Test behavior on very small maze (edge case)
     */
    @Test
    public void testSmallMaze() {
        IMazeGenerator generator = new MyMazeGenerator();
        Maze maze = generator.generate(1, 1);

        SearchableMaze searchable = new SearchableMaze(maze);
        ISearchingAlgorithm best = new BestFirstSearch();

        Solution solution = best.solve(searchable);

        assertNotNull(solution);
        assertEquals(1, solution.getSolutionPath().size());
    }
}