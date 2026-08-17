package algorithms.search;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a solution to a search problem.
 * Contains the path from the start state to the goal state.
 */
public class Solution implements Serializable {

    private static final long serialVersionUID = 1L;

    private ArrayList<AState> solutionPath;

    /**
     * Constructor
     *
     * @param solutionPath the path from start to goal
     */
    public Solution(ArrayList<AState> solutionPath) {
        this.solutionPath = solutionPath;
    }

    /**
     * @return the solution path as a list of states
     */
    public ArrayList<AState> getSolutionPath() {
        return solutionPath;
    }
}
