package algorithms.search;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.ArrayList;

/**
 * Adapts a Maze to a searchable problem.
 */
public class SearchableMaze implements ISearchable {

    private Maze maze;

    /**
     * Constructor
     *
     * @param maze the maze to adapt
     */
    public SearchableMaze(Maze maze) {
        this.maze = maze;
    }

    @Override
    public AState getStartState() {
        Position start = maze.getStartPosition();
        return new MazeState(start);
    }

    @Override
    public AState getGoalState() {
        Position goal = maze.getGoalPosition();
        return new MazeState(goal);
    }

    @Override
    public ArrayList<AState> getAllPossibleStates(AState state) {
        ArrayList<AState> neighbors = new ArrayList<>();

        MazeState current = (MazeState) state;
        int row = current.getPosition().getRowIndex();
        int col = current.getPosition().getColumnIndex();

        int[][] directions = {
                {0, 1}, {0, -1}, {1, 0}, {-1, 0},
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            if (isValidMove(newRow, newCol)) {
                MazeState neighbor = new MazeState(new algorithms.mazeGenerators.Position(newRow, newCol));

                // set cost
                if (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) {
                    neighbor.setCost(1.5); // diagonal move
                } else {
                    neighbor.setCost(1); // straight move
                }

                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }
    /**
     * Checks if a move is valid in the maze.
     *
     * @param row row index
     * @param col column index
     * @return true if the cell is within bounds and not a wall
     */
    private boolean isValidMove(int row, int col) {
        return row >= 0 &&
                row < maze.getGoalPosition().getRowIndex() + 1 &&
                col >= 0 &&
                col < maze.getGoalPosition().getColumnIndex() + 1 &&
                maze.getCell(row, col) == 0;
    }
}
