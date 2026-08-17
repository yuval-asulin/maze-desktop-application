package algorithms.search;

import java.util.*;

/**
 * Best First Search algorithm implementation.
 * Uses a priority queue to explore states based on cost.
 */
public class BestFirstSearch extends ASearchingAlgorithm {

    /**
     * Solves the given searchable problem using Best First Search.
     *
     * @param domain the searchable problem
     * @return the solution path from start to goal
     */
    @Override
    public Solution solve(ISearchable domain) {

        PriorityQueue<AState> openList = new PriorityQueue<>(Comparator.comparingDouble(AState::getCost));
        HashSet<AState> visited = new HashSet<>();

        AState start = domain.getStartState();
        AState goal = domain.getGoalState();

        start.setCost(0);
        openList.add(start);

        while (!openList.isEmpty()) {
            AState current = openList.poll();

            nodesEvaluated++;

            if (current.equals(goal)) {
                return backtrace(current);
            }

            visited.add(current);

            for (AState neighbor : domain.getAllPossibleStates(current)) {

                if (!visited.contains(neighbor)) {

                    neighbor.setCameFrom(current);

                    // accumulate cost
                    neighbor.setCost(current.getCost() + neighbor.getCost());

                    openList.add(neighbor);
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
        return "Best First Search";
    }
}
