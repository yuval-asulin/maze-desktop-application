package algorithms.search;

/**
 * Abstract class for search algorithms.
 */
public abstract class ASearchingAlgorithm implements ISearchingAlgorithm {

    protected int nodesEvaluated;

    /**
     * @return number of evaluated nodes
     */
    @Override
    public int getNumberOfNodesEvaluated() {
        return nodesEvaluated;
    }
}
