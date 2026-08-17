package view;

import model.MyModel;
import viewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MyViewController implements IView {

    private static final Logger logger = LogManager.getLogger("MazeGenerator");

    @FXML
    private MazeDisplayer mazeDisplayer;

    @FXML
    private javafx.scene.control.Button solveButton;

    @FXML
    private javafx.scene.control.Button saveButton;

    @FXML
    private StackPane mazePane;

    @FXML
    private Spinner<Integer> rowsSpinner;

    @FXML
    private Spinner<Integer> columnsSpinner;

    private MyViewModel viewModel;
    private ConfettiOverlay confettiOverlay;

    @FXML
    public void initialize() {
        viewModel = new MyViewModel(new MyModel());
        mazeDisplayer.setFocusTraversable(true);
        mazeDisplayer.sceneProperty().addListener((observable, oldScene, newScene) -> registerKeyboardHandler(newScene));
        mazeDisplayer.widthProperty().bind(mazePane.widthProperty());
        mazeDisplayer.heightProperty().bind(mazePane.heightProperty());
        mazeDisplayer.widthProperty().addListener((observable, oldValue, newValue) -> mazeDisplayer.redraw());
        mazeDisplayer.heightProperty().addListener((observable, oldValue, newValue) -> mazeDisplayer.redraw());
        rowsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 10));
        columnsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(Integer.MIN_VALUE, Integer.MAX_VALUE, 10));
        rowsSpinner.setEditable(true);
        columnsSpinner.setEditable(true);

        confettiOverlay = new ConfettiOverlay();
        confettiOverlay.widthProperty().bind(mazePane.widthProperty());
        confettiOverlay.heightProperty().bind(mazePane.heightProperty());
        mazePane.getChildren().add(confettiOverlay);

    }

    @FXML
    protected void onGenerateMazeClick() {
        int rows, cols;
        try {
            rows = Integer.parseInt(rowsSpinner.getEditor().getText().trim());
            cols = Integer.parseInt(columnsSpinner.getEditor().getText().trim());
        } catch (NumberFormatException e) {
            logger.error("Invalid maze dimensions entered - rows: '{}', cols: '{}'",
                    rowsSpinner.getEditor().getText().trim(),
                    columnsSpinner.getEditor().getText().trim());
            showError("Invalid input", "Rows and columns must be whole numbers.");
            return;
        }
        if (rows < 2 || cols < 2) {
            logger.error("Maze dimensions too small - {}x{} (minimum is 2x2)", rows, cols);
            showError("Invalid input", "Rows and columns must be at least 2.");
            return;
        }
        if (rows > 1000 || cols > 1000) {
            logger.error("Maze dimensions too large - {}x{} (maximum is 1000x1000)", rows, cols);
            showError("Invalid input", "Rows and columns must not exceed 1000.");
            return;
        }

        viewModel.generateMaze(rows, cols);
        displayMaze(viewModel.getMaze(), viewModel.getPlayerPosition(), viewModel.getSolutionPath());
        setGameActionsDisabled(false);
        mazeDisplayer.requestFocus();
    }

    @FXML
    protected void onSolveMazeClick() {
        if (viewModel.getMaze() == null) {
            showInformation("No maze", "Generate a maze before solving it.");
            return;
        }

        viewModel.solveMaze();
        displayMaze(viewModel.getMaze(), viewModel.getPlayerPosition(), viewModel.getSolutionPath());
        mazeDisplayer.requestFocus();
    }

    @FXML
    protected void onSaveMazeClick() {
        if (viewModel.getMaze() == null) {
            showInformation("No maze", "Generate a maze before saving it.");
            return;
        }

        FileChooser fileChooser = createMazeFileChooser();
        File file = fileChooser.showSaveDialog(mazeDisplayer.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            viewModel.saveMaze(file);
        } catch (IOException e) {
            logger.error("Save failed for file: {} - {}", file.getAbsolutePath(), e.getMessage());
            showError("Save failed", e.getMessage());
        }
        mazeDisplayer.requestFocus();
    }

    @FXML
    protected void onLoadMazeClick() {
        FileChooser fileChooser = createMazeFileChooser();
        File file = fileChooser.showOpenDialog(mazeDisplayer.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            viewModel.loadMaze(file);
            displayMaze(viewModel.getMaze(), viewModel.getPlayerPosition(), viewModel.getSolutionPath());
            setGameActionsDisabled(false);
        } catch (IOException e) {
            logger.error("Load failed for file: {} - {}", file.getAbsolutePath(), e.getMessage());
            showError("Load failed", e.getMessage());
        }
        mazeDisplayer.requestFocus();
    }

    @FXML
    protected void onExitClick() {
        shutdown();
        Platform.exit();
    }

    public void shutdown() {
        if (viewModel != null) {
            viewModel.stopServers();
        }
    }

    @FXML
    protected void onHelpClick() {
        showInformation("Help", "Generate a maze, then move with NumPad: 8 up, 2 down, 4 left, 6 right, and 7/9/1/3 diagonals.\nUse Ctrl + mouse wheel to zoom.\nDrag the player to a nearby valid cell to move with the mouse.");
    }

    @FXML
    protected void onAboutClick() {
        showInformation("About", "Maze Game\nGenerator: MyMazeGenerator\nSolver: BestFirstSearch");
    }

    @FXML
    protected void onPropertiesClick() {
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getResourceAsStream("/config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException e) {
            showError("Properties", e.getMessage());
            return;
        }

        String message = String.format(
                "threadPoolSize=%s%nmazeGeneratingAlgorithm=%s%nmazeSearchingAlgorithm=%s",
                properties.getProperty("threadPoolSize", "unclear"),
                properties.getProperty("mazeGeneratingAlgorithm", "unclear"),
                properties.getProperty("mazeSearchingAlgorithm", "unclear")
        );
        showInformation("Properties", message);
    }

    @FXML
    protected void onKeyPressed(KeyEvent keyEvent) {
        if (viewModel.getMaze() == null) {
            return;
        }

        KeyCode keyCode = keyEvent.getCode();
        if (keyCode == KeyCode.UP || keyCode == KeyCode.NUMPAD8) {
            movePlayer(-1, 0);
        } else if (keyCode == KeyCode.DOWN || keyCode == KeyCode.NUMPAD2) {
            movePlayer(1, 0);
        } else if (keyCode == KeyCode.LEFT || keyCode == KeyCode.NUMPAD4) {
            movePlayer(0, -1);
        } else if (keyCode == KeyCode.RIGHT || keyCode == KeyCode.NUMPAD6) {
            movePlayer(0, 1);
        } else if (keyCode == KeyCode.NUMPAD7) {
            movePlayer(-1, -1);
        } else if (keyCode == KeyCode.NUMPAD9) {
            movePlayer(-1, 1);
        } else if (keyCode == KeyCode.NUMPAD1) {
            movePlayer(1, -1);
        } else if (keyCode == KeyCode.NUMPAD3) {
            movePlayer(1, 1);
        } else if (keyCode == KeyCode.DIGIT8) {
            movePlayer(-1, 0);
        } else if (keyCode == KeyCode.DIGIT2) {
            movePlayer(1, 0);
        } else if (keyCode == KeyCode.DIGIT4) {
            movePlayer(0, -1);
        } else if (keyCode == KeyCode.DIGIT6) {
            movePlayer(0, 1);
        } else if (keyCode == KeyCode.DIGIT7) {
            movePlayer(-1, -1);
        } else if (keyCode == KeyCode.DIGIT9) {
            movePlayer(-1, 1);
        } else if (keyCode == KeyCode.DIGIT1) {
            movePlayer(1, -1);
        } else if (keyCode == KeyCode.DIGIT3) {
            movePlayer(1, 1);
        } else {
            return;
        }

        keyEvent.consume();
    }

    @FXML
    protected void onMazeScroll(ScrollEvent scrollEvent) {
        if (!scrollEvent.isControlDown()) {
            return;
        }

        if (scrollEvent.getDeltaY() > 0) {
            mazeDisplayer.zoomIn();
        } else {
            mazeDisplayer.zoomOut();
        }
        scrollEvent.consume();
    }

    @FXML
    protected void onMazeDragged(MouseEvent mouseEvent) {
        if (viewModel.getMaze() == null || viewModel.getPlayerPosition() == null) {
            return;
        }

        Position targetPosition = mazeDisplayer.getMazePositionByCoordinates(mouseEvent.getX(), mouseEvent.getY());
        if (targetPosition == null) {
            return;
        }

        Position currentPosition = viewModel.getPlayerPosition();
        int rowDelta = targetPosition.getRowIndex() - currentPosition.getRowIndex();
        int columnDelta = targetPosition.getColumnIndex() - currentPosition.getColumnIndex();

        if (Math.abs(rowDelta) <= 1 && Math.abs(columnDelta) <= 1 && (rowDelta != 0 || columnDelta != 0)) {
            movePlayer(rowDelta, columnDelta);
        }
        mouseEvent.consume();
    }

    private void registerKeyboardHandler(Scene scene) {
        if (scene != null) {
            scene.setOnKeyPressed(this::onKeyPressed);
        }
    }

    private void movePlayer(int rowDelta, int columnDelta) {
        displayMaze(viewModel.getMaze(), viewModel.getPlayerPosition(), viewModel.getSolutionPath());
        viewModel.movePlayer(rowDelta, columnDelta);
        displayMaze(viewModel.getMaze(), viewModel.getPlayerPosition(), viewModel.getSolutionPath());
        if (viewModel.shouldShowSolvedMessage()) {
            viewModel.markSolvedMessageShown();
            confettiOverlay.start();
            showInformation("Maze solved", "YOU DID IT!!!\n NOW LET'S GET THOSE TAN LINES");
            confettiOverlay.stop();
        }
    }

    @Override
    public void displayMaze(Maze maze, Position playerPosition, List<Position> solutionPath) {
        mazeDisplayer.setMaze(maze, playerPosition, solutionPath);
    }

    private void showInformation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        mazeDisplayer.requestFocus();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private FileChooser createMazeFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files", "*.maze"));
        return fileChooser;
    }

    private void setGameActionsDisabled(boolean disabled) {
        solveButton.setDisable(disabled);
        saveButton.setDisable(disabled);
    }

}
