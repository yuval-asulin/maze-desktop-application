package algorithms.search;

import java.io.Serializable;

/**
 * Represents an abstract state in a search problem.
 */
public abstract class AState implements Serializable {

    private static final long serialVersionUID = 1L;

    private String state;
    private double cost;
    private AState cameFrom;

    /**
     * Constructor
     *
     * @param state the state representation
     */
    public AState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    public void setCameFrom(AState state) {
        this.cameFrom = state;
    }

    public AState getCameFrom() {
        return cameFrom;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AState other = (AState) obj;
        return state.equals(other.state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
