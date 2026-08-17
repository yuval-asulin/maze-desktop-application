package algorithms.mazeGenerators;

import java.io.Serializable;

/**
 * The Maze class represents a 2D maze.
 * The maze is represented by a 2D int array:
 * 0 - empty cell (free space)
 * 1 - wall
 */
public class Maze implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 2D array representing the maze structure
     */
    private int[][] maze;

    /**
     * Number of rows in the maze
     */
    private int rows;

    /**
     * Number of columns in the maze
     */
    private int columns;

    /**
     * Starting position of the maze
     */
    private Position startPosition;

    /**
     * Goal (exit) position of the maze
     */
    private Position goalPosition;

    /** Number of bytes used for maze metadata in the byte array representation */
    public static final int MAZE_METADATA_BYTES = 12;

    /**
     * Constructor for creating a maze with given dimensions.
     * Initializes an empty maze and sets default start and goal positions.
     *
     * @param rows number of rows
     * @param columns number of columns
     */
    public Maze(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.maze = new int[rows][columns];

        this.startPosition = new Position(0, 0);
        this.goalPosition = new Position(rows - 1, columns - 1);
    }

    /**
     * @return the start position of the maze
     */
    public Position getStartPosition() {
        return startPosition;
    }

    /**
     * @return the goal (exit) position of the maze
     */
    public Position getGoalPosition() {
        return goalPosition;
    }

    /**
     * Prints the maze to the console.
     * Start position is marked with 'S'
     * Goal position is marked with 'E'
     */
    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {

                if (i == startPosition.getRowIndex() && j == startPosition.getColumnIndex()) {
                    System.out.print("S ");
                } else if (i == goalPosition.getRowIndex() && j == goalPosition.getColumnIndex()) {
                    System.out.print("E ");
                } else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Sets the value of a specific cell in the maze.
     *
     * @param row the row index
     * @param col the column index
     * @param value the value to set (0 = empty, 1 = wall)
     */
    public void setCell(int row, int col, int value) {
        if (row >= 0 && row < rows && col >= 0 && col < columns) {
            maze[row][col] = value;
        }
    }
    /**
     * Returns the value of a specific cell in the maze.
     *
     * @param row the row index
     * @param col the column index
     * @return the value of the cell (0 = empty, 1 = wall)
     */
    public int getCell(int row, int col) {
        return maze[row][col];
    }

    /**
     * Serializes the maze into a byte array.
     * Format: [rows hi][rows lo][cols hi][cols lo][startRow hi][startRow lo]
     *         [startCol hi][startCol lo][goalRow hi][goalRow lo][goalCol hi][goalCol lo]
     *         [cell(0,0)]...[cell(rows-1,cols-1)]
     */
    public byte[] toByteArray() {
        byte[] result = new byte[MAZE_METADATA_BYTES + rows * columns];
        result[0]  = (byte)(rows >> 8);
        result[1]  = (byte)(rows & 0xFF);
        result[2]  = (byte)(columns >> 8);
        result[3]  = (byte)(columns & 0xFF);
        result[4]  = (byte)(startPosition.getRowIndex() >> 8);
        result[5]  = (byte)(startPosition.getRowIndex() & 0xFF);
        result[6]  = (byte)(startPosition.getColumnIndex() >> 8);
        result[7]  = (byte)(startPosition.getColumnIndex() & 0xFF);
        result[8]  = (byte)(goalPosition.getRowIndex() >> 8);
        result[9]  = (byte)(goalPosition.getRowIndex() & 0xFF);
        result[10] = (byte)(goalPosition.getColumnIndex() >> 8);
        result[11] = (byte)(goalPosition.getColumnIndex() & 0xFF);
        int index = MAZE_METADATA_BYTES;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                result[index++] = (byte) maze[i][j];
        return result;
    }

    /**
     * Reconstructs a maze from its uncompressed byte array representation.
     *
     * @param byteArray uncompressed byte array produced by toByteArray()
     */
    public Maze(byte[] byteArray) {
        this.rows    = ((byteArray[0] & 0xFF) << 8) | (byteArray[1] & 0xFF);
        this.columns = ((byteArray[2] & 0xFF) << 8) | (byteArray[3] & 0xFF);
        int startRow = ((byteArray[4] & 0xFF) << 8) | (byteArray[5] & 0xFF);
        int startCol = ((byteArray[6] & 0xFF) << 8) | (byteArray[7] & 0xFF);
        int goalRow  = ((byteArray[8] & 0xFF) << 8) | (byteArray[9] & 0xFF);
        int goalCol  = ((byteArray[10] & 0xFF) << 8) | (byteArray[11] & 0xFF);
        this.startPosition = new Position(startRow, startCol);
        this.goalPosition  = new Position(goalRow, goalCol);
        this.maze = new int[rows][columns];
        int index = MAZE_METADATA_BYTES;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                maze[i][j] = byteArray[index++] & 0xFF;
    }
}
