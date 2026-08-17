package algorithms.mazeGenerators;

import java.io.Serializable;

/**
 * Represents a position in the maze (row, column)
 */
public class Position implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Row index of the position
     */
    private int row;

    /**
     * Column index of the position
     */
    private int column;

    /**
     * Constructor for creating a position
     *
     * @param row row index
     * @param column column index
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * @return row index
     */
    public int getRowIndex() {
        return row;
    }

    /**
     * @return column index
     */
    public int getColumnIndex() {
        return column;
    }

    /**
     * Returns the position in format {row,column}
     */
    @Override
    public String toString() {
        return "{" + row + "," + column + "}";
    }
}
