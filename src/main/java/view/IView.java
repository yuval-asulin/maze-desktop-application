package view;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;

import java.util.List;

public interface IView {
    void displayMaze(Maze maze, Position playerPosition, List<Position> solutionPath);
}
