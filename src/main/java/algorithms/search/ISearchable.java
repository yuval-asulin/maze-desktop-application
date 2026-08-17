package algorithms.search;

import java.util.ArrayList;

/**
 * Interface representing a searchable problem.
 */
public interface ISearchable {

    /**
     * @return the start state of the problem
     */
    AState getStartState();

    /**
     * @return the goal state of the problem
     */
    AState getGoalState();

    /**
     * Returns all possible states reachable from the given state.
     *
     * @param state the current state
     * @return list of reachable states
     */
    ArrayList<AState> getAllPossibleStates(AState state);
}
