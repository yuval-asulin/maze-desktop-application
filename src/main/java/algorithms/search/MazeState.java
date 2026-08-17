package algorithms.search;

import algorithms.mazeGenerators.Position;

/**
 * Represents a state in the maze.
 */
public class MazeState extends AState {

    private static final long serialVersionUID = 1L;

    private Position position;

    public MazeState(Position position) {
        super(position.toString());
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
    @Override
    public String toString() {
        return position.toString();
    }
}
