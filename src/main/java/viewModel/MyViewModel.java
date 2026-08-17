package viewModel;

import model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyViewModel {

    private final IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generateMaze(int rows, int columns) {
        model.generateMaze(rows, columns);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Position getPlayerPosition() {
        return model.getPlayerPosition();
    }

    public void movePlayer(int rowDelta, int columnDelta) {
        model.movePlayer(rowDelta, columnDelta);
    }

    public boolean isMazeSolved() {
        return model.isMazeSolved();
    }

    public boolean shouldShowSolvedMessage() {
        return model.isMazeSolved() && !model.isSolvedMessageShown();
    }

    public void markSolvedMessageShown() {
        model.setSolvedMessageShown(true);
    }

    public void solveMaze() {
        model.solveMaze();
    }

    public List<Position> getSolutionPath() {
        return model.getSolutionPath();
    }

    public void saveMaze(File file) throws IOException {
        model.saveMaze(file);
    }

    public void loadMaze(File file) throws IOException {
        model.loadMaze(file);
    }

    public void stopServers() {
        model.stopServers();
    }

    public String getMazeAsText() {
        Maze maze = model.getMaze();
        if (maze == null) {
            return "";
        }

        byte[] mazeBytes = maze.toByteArray();
        int rows = ((mazeBytes[0] & 0xFF) << 8) | (mazeBytes[1] & 0xFF);
        int columns = ((mazeBytes[2] & 0xFF) << 8) | (mazeBytes[3] & 0xFF);

        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (maze.getStartPosition().getRowIndex() == row &&
                        maze.getStartPosition().getColumnIndex() == column) {
                    builder.append("S ");
                } else if (maze.getGoalPosition().getRowIndex() == row &&
                        maze.getGoalPosition().getColumnIndex() == column) {
                    builder.append("E ");
                } else {
                    builder.append(maze.getCell(row, column)).append(' ');
                }
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
