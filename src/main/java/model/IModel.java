package model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IModel {
    void generateMaze(int rows, int columns);

    Maze getMaze();

    Position getPlayerPosition();

    void movePlayer(int rowDelta, int columnDelta);

    boolean isMazeSolved();

    boolean isSolvedMessageShown();

    void setSolvedMessageShown(boolean solvedMessageShown);

    void solveMaze();

    List<Position> getSolutionPath();

    void saveMaze(File file) throws IOException;

    void loadMaze(File file) throws IOException;

    void stopServers();
}
