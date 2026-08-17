package algorithms.search;

/**
 * Interface for search algorithms.
 */
public interface ISearchingAlgorithm {

    /**
     * Solves the given problem.
     *
     * @param domain the searchable problem
     * @return solution
     */
    Solution solve(ISearchable domain);

    /**
     * @return algorithm name
     */
    String getName();

    /**
     * @return number of nodes evaluated
     */
    int getNumberOfNodesEvaluated();
}
