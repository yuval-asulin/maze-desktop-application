package view;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;
import java.util.List;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private Position playerPosition;
    private List<Position> solutionPath;
    private final Image playerImage  = loadPlayerImage();
    private final Image microImage   = loadImage("/media/woman-red-hat-sunbathing-public-domain.png");
    private final Image welcomeImage = loadImage("/media/welcome-page.jpg");
    private double zoomFactor = 1.0;

    public void setMaze(Maze maze, Position playerPosition, List<Position> solutionPath) {
        this.maze = maze;
        this.playerPosition = playerPosition;
        this.solutionPath = solutionPath;
        redraw();
    }

    public void redraw() {
        GraphicsContext graphics = getGraphicsContext2D();
        graphics.setFill(Color.web("#FECC97"));
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.setStroke(Color.web("#9E5E32"));
        graphics.strokeRect(0, 0, getWidth(), getHeight());

        if (maze == null) {
            if (welcomeImage != null && !welcomeImage.isError()) {
                graphics.drawImage(welcomeImage, 0, 0, getWidth(), getHeight());
            }
            return;
        }

        byte[] mazeBytes = maze.toByteArray();
        int rows = ((mazeBytes[0] & 0xFF) << 8) | (mazeBytes[1] & 0xFF);
        int columns = ((mazeBytes[2] & 0xFF) << 8) | (mazeBytes[3] & 0xFF);
        double cellSize = Math.min(getWidth() / columns, getHeight() / rows) * zoomFactor;
        double boardWidth = columns * cellSize;
        double boardHeight = rows * cellSize;
        double boardX = (getWidth() - boardWidth) / 2;
        double boardY = (getHeight() - boardHeight) / 2;
        double markerSize = Math.max(cellSize * 0.7, Math.min(8, cellSize));
        double markerOffset = (cellSize - markerSize) / 2;
        double goalMarkerSize = cellSize;
        double goalMarkerOffset = (cellSize - goalMarkerSize) / 2;

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (maze.getCell(row, column) == 1) {
                    graphics.setFill(Color.web("#C47A45"));
                } else {
                    graphics.setFill(Color.web("#FECC97"));
                }
                graphics.fillRect(boardX + column * cellSize, boardY + row * cellSize, cellSize, cellSize);
            }
        }

        if (microImage != null && !microImage.isError()) {
            graphics.drawImage(microImage,
                    boardX + maze.getGoalPosition().getColumnIndex() * cellSize + goalMarkerOffset,
                    boardY + maze.getGoalPosition().getRowIndex() * cellSize + goalMarkerOffset,
                    goalMarkerSize, goalMarkerSize);
        }

        if (solutionPath != null) {
            graphics.setFill(Color.rgb(255, 215, 0, 0.55));
            for (Position position : solutionPath) {
                graphics.fillOval(
                        boardX + position.getColumnIndex() * cellSize + cellSize * 0.25,
                        boardY + position.getRowIndex() * cellSize + cellSize * 0.25,
                        cellSize * 0.5,
                        cellSize * 0.5
                );
            }
        }

        if (playerPosition != null) {
            double playerX = boardX + playerPosition.getColumnIndex() * cellSize + markerOffset;
            double playerY = boardY + playerPosition.getRowIndex() * cellSize + markerOffset;

            if (playerImage != null && !playerImage.isError()) {
                graphics.drawImage(playerImage, playerX, playerY, markerSize, markerSize);
            } else {
                graphics.setFill(Color.DODGERBLUE);
                graphics.fillOval(playerX, playerY, markerSize, markerSize);
            }
        }
    }

    public void zoomIn() {
        zoomFactor = Math.min(2.0, zoomFactor + 0.1);
        redraw();
    }

    public void zoomOut() {
        zoomFactor = Math.max(0.5, zoomFactor - 0.1);
        redraw();
    }

    public Position getMazePositionByCoordinates(double x, double y) {
        if (maze == null) {
            return null;
        }

        byte[] mazeBytes = maze.toByteArray();
        int rows = ((mazeBytes[0] & 0xFF) << 8) | (mazeBytes[1] & 0xFF);
        int columns = ((mazeBytes[2] & 0xFF) << 8) | (mazeBytes[3] & 0xFF);
        double cellSize = Math.min(getWidth() / columns, getHeight() / rows) * zoomFactor;
        double boardWidth = columns * cellSize;
        double boardHeight = rows * cellSize;
        double boardX = (getWidth() - boardWidth) / 2;
        double boardY = (getHeight() - boardHeight) / 2;

        if (x < boardX || y < boardY || x >= boardX + boardWidth || y >= boardY + boardHeight) {
            return null;
        }

        int row = (int) ((y - boardY) / cellSize);
        int column = (int) ((x - boardX) / cellSize);
        return new Position(row, column);
    }

    private Image loadPlayerImage() {
        return loadImage("/media/sun-public-domain.png");
    }

    private Image loadImage(String resourcePath) {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            return null;
        }
        return new Image(inputStream);
    }
}
