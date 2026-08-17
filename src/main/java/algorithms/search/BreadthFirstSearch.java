package algorithms.search;

import java.util.*;

/**
 * Breadth First Search (BFS) algorithm implementation.
 * Explores the search space level by level using a queue.
 */
public class BreadthFirstSearch extends ASearchingAlgorithm {

    /**
     * Solves the given searchable problem using BFS.
     *
     * @param domain the searchable problem
     * @return the solution path from start to goal
     */
    @Override
    public Solution solve(ISearchable domain) {

        Queue<AState> queue = new LinkedList<>();
        HashSet<AState> visited = new HashSet<>();

        AState start = domain.getStartState();
        AState goal = domain.getGoalState();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            AState current = queue.poll();

            nodesEvaluated++;

            if (current.equals(goal)) {
                return backtrace(current);
            }

            for (AState neighbor : domain.getAllPossibleStates(current)) {
                if (!visited.contains(neighbor)) {
                    neighbor.setCameFrom(current);
                    queue.add(neighbor);
                    visited.add(neighbor);
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
        return "Breadth First Search";
    }
}
