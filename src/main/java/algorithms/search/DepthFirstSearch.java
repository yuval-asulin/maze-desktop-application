package algorithms.search;

import java.util.*;

/**
 * Depth First Search (DFS) algorithm implementation.
 * Explores as deep as possible before backtracking.
 */
public class DepthFirstSearch extends ASearchingAlgorithm {

    /**
     * Solves the given searchable problem using DFS.
     *
     * @param domain the searchable problem
     * @return the solution path from start to goal
     */
    @Override
    public Solution solve(ISearchable domain) {

        Stack<AState> stack = new Stack<>();
        HashSet<AState> visited = new HashSet<>();

        AState start = domain.getStartState();
        AState goal = domain.getGoalState();

        stack.push(start);

        while (!stack.isEmpty()) {
            AState current = stack.pop();

            nodesEvaluated++;

            if (current.equals(goal)) {
                return backtrace(current);
            }

            if (!visited.contains(current)) {
                visited.add(current);

                for (AState neighbor : domain.getAllPossibleStates(current)) {
                    if (!visited.contains(neighbor)) {
                        neighbor.setCameFrom(current);
                        stack.push(neighbor);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Builds the solution path from goal to start.
     *
     * @param goal the goal state
     * @return the solution path
     */
    private Solution backtrace(AState goal) {
        ArrayList<AState> path = new ArrayList<>();
        AState current = goal;

        while (current != null) {
            path.add(current);
            current = current.getCameFrom();
        }

        Collections.reverse(path);
        return new Solution(path);
    }

    /**
     * @return the name of the algorithm
     */
    @Override
    public String getName() {
        return "Depth First Search";
    }
}
