package model;

import Client.Client;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.IServerStrategy;
import Server.Server;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.BestFirstSearch;
import algorithms.search.MazeState;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MyModel implements IModel {

    private static final Logger generatorLogger = LogManager.getLogger("MazeGenerator");
    private static final Logger solverLogger = LogManager.getLogger("MazeSolver");
    private static final int SERVER_LISTENING_INTERVAL_MS = 1000;

    private final int mazeGeneratingServerPort;
    private final int mazeSolvingServerPort;
    private final Server mazeGeneratingServer;
    private final Server mazeSolvingServer;
    private Maze maze;
    private Position playerPosition;
    private boolean mazeSolved;
    private boolean solvedMessageShown;
    private List<Position> solutionPath;

    public MyModel() {
        try {
            mazeGeneratingServerPort = getFreePort();
            mazeSolvingServerPort = getFreePort();
            mazeGeneratingServer = new Server(mazeGeneratingServerPort, SERVER_LISTENING_INTERVAL_MS, new ServerStrategyGenerateMaze());
            mazeSolvingServer = new Server(mazeSolvingServerPort, SERVER_LISTENING_INTERVAL_MS, new ServerStrategySolveSearchProblem());
            mazeGeneratingServer.start();
            mazeSolvingServer.start();
            waitForServersToStart();
            generatorLogger.info("Maze generating server started on port {}", mazeGeneratingServerPort);
            solverLogger.info("Maze solving server started on port {}", mazeSolvingServerPort);
        } catch (IOException e) {
            throw new IllegalStateException("Could not start maze servers", e);
        }
    }

    @Override
    public void generateMaze(int rows, int columns) {
        AtomicReference<Maze> generatedMaze = new AtomicReference<>();
        AtomicReference<Exception> clientException = new AtomicReference<>();

        try {
            new Client(InetAddress.getLocalHost(), mazeGeneratingServerPort, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    toServer.flush();
                    toServer.writeObject(new int[]{rows, columns});
                    toServer.flush();

                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    byte[] compressedMaze = (byte[]) fromServer.readObject();
                    generatedMaze.set(new Maze(decompressMazeBytes(compressedMaze)));
                } catch (Exception e) {
                    clientException.set(e);
                }
            }).communicateWithServer();
        } catch (Exception e) {
            throw new IllegalStateException("Could not communicate with maze generating server", e);
        }

        if (clientException.get() != null) {
            throw new IllegalStateException("Maze generation failed", clientException.get());
        }
        if (generatedMaze.get() == null) {
            throw new IllegalStateException("Maze generation server did not return a maze");
        }

        maze = generatedMaze.get();
        playerPosition = maze.getStartPosition();
        mazeSolved = false;
        solvedMessageShown = false;
        solutionPath = new ArrayList<>();
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public Position getPlayerPosition() {
        return playerPosition;
    }

    @Override
    public void movePlayer(int rowDelta, int columnDelta) {
        if (maze == null || playerPosition == null) {
            return;
        }

        int newRow = playerPosition.getRowIndex() + rowDelta;
        int newColumn = playerPosition.getColumnIndex() + columnDelta;
        if (isValidPlayerMove(newRow, newColumn)) {
            playerPosition = new Position(newRow, newColumn);
            mazeSolved = playerPosition.toString().equals(maze.getGoalPosition().toString());
        }
    }

    @Override
    public boolean isMazeSolved() {
        return mazeSolved;
    }

    @Override
    public boolean isSolvedMessageShown() {
        return solvedMessageShown;
    }

    @Override
    public void setSolvedMessageShown(boolean solvedMessageShown) {
        this.solvedMessageShown = solvedMessageShown;
    }

    @Override
    public void solveMaze() {
        solutionPath = new ArrayList<>();
        if (maze == null) {
            solverLogger.warn("Solve requested but no maze exists");
            return;
        }

        AtomicReference<Solution> serverSolution = new AtomicReference<>();
        AtomicReference<Exception> clientException = new AtomicReference<>();

        try {
            new Client(InetAddress.getLocalHost(), mazeSolvingServerPort, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    toServer.flush();
                    toServer.writeObject(maze);
                    toServer.flush();

                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    serverSolution.set((Solution) fromServer.readObject());
                } catch (Exception e) {
                    clientException.set(e);
                }
            }).communicateWithServer();
        } catch (Exception e) {
            throw new IllegalStateException("Could not communicate with maze solving server", e);
        }

        if (clientException.get() != null) {
            throw new IllegalStateException("Maze solving failed", clientException.get());
        }

        Solution solution = serverSolution.get();

        if (solution == null || solution.getSolutionPath().isEmpty()) {
            solverLogger.error("No solution found for the maze");
            return;
        }

        for (AState state : solution.getSolutionPath()) {
            MazeState mazeState = (MazeState) state;
            solutionPath.add(mazeState.getPosition());
        }
    }

    @Override
    public List<Position> getSolutionPath() {
        return solutionPath;
    }

    @Override
    public void saveMaze(File file) throws IOException {
        if (maze == null) {
            generatorLogger.warn("Save requested but no maze exists");
            return;
        }

        generatorLogger.info("Saving maze to file: {}", file.getAbsolutePath());
        try (MyCompressorOutputStream outputStream =
                     new MyCompressorOutputStream(new FileOutputStream(file))) {
            outputStream.write(maze.toByteArray());
            outputStream.flush();
        } catch (IOException e) {
            generatorLogger.error("Failed to save maze to file: {} - {}", file.getAbsolutePath(), e.getMessage());
            throw e;
        }
        generatorLogger.info("Maze saved successfully to: {}", file.getAbsolutePath());
    }

    @Override
    public void loadMaze(File file) throws IOException {
        generatorLogger.info("Loading maze from file: {}", file.getAbsolutePath());
        try (MyDecompressorInputStream inputStream =
                     new MyDecompressorInputStream(new FileInputStream(file));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int currentByte;
            while ((currentByte = inputStream.read()) != -1) {
                byteArrayOutputStream.write(currentByte);
            }

            maze = new Maze(byteArrayOutputStream.toByteArray());
            playerPosition = maze.getStartPosition();
            mazeSolved = false;
            solvedMessageShown = false;
            solutionPath = new ArrayList<>();
        } catch (IOException e) {
            generatorLogger.error("Failed to load maze from file: {} - {}", file.getAbsolutePath(), e.getMessage());
            throw e;
        }
        generatorLogger.info("Maze loaded successfully - Start: {}, Goal: {}", maze.getStartPosition(), maze.getGoalPosition());
    }

    @Override
    public void stopServers() {
        mazeGeneratingServer.stop();
        mazeSolvingServer.stop();
        generatorLogger.info("Maze generating server stopped");
        solverLogger.info("Maze solving server stopped");
    }

    private boolean isValidPlayerMove(int row, int column) {
        return row >= 0 &&
                row <= maze.getGoalPosition().getRowIndex() &&
                column >= 0 &&
                column <= maze.getGoalPosition().getColumnIndex() &&
                maze.getCell(row, column) == 0;
    }

    private static Maze generateSolvableMaze(MyMazeGenerator generator, int rows, int columns) {
        Maze generatedMaze = generator.generate(rows, columns);
        int attempts = 1;

        while (!hasSolution(generatedMaze) && attempts < 20) {
            generatorLogger.warn("Generated maze has no solution, retrying (attempt {})", attempts);
            generatedMaze = generator.generate(rows, columns);
            attempts++;
        }

        if (attempts >= 20) {
            generatorLogger.fatal("Could not generate a solvable maze after 20 attempts - {}x{}", rows, columns);
        }

        return generatedMaze;
    }

    private static boolean hasSolution(Maze mazeToCheck) {
        BestFirstSearch searcher = new BestFirstSearch();
        Solution solution = searcher.solve(new SearchableMaze(mazeToCheck));
        return solution != null && !solution.getSolutionPath().isEmpty();
    }

    private static byte[] decompressMazeBytes(byte[] compressedMaze) throws IOException {
        try (MyDecompressorInputStream inputStream =
                     new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            int currentByte;
            while ((currentByte = inputStream.read()) != -1) {
                byteArrayOutputStream.write(currentByte);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static int getFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void waitForServersToStart() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class ServerStrategyGenerateMaze implements IServerStrategy {

        @Override
        public void serverStrategy(InputStream inFromClient, OutputStream outToClient) {
            try {
                ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
                toClient.flush();
                ObjectInputStream fromClient = new ObjectInputStream(inFromClient);

                int[] dimensions = (int[]) fromClient.readObject();
                if (dimensions == null || dimensions.length != 2) {
                    generatorLogger.error("Invalid maze generation request");
                    return;
                }

                generatorLogger.info("Server received maze generation request - Size: {}x{}",
                        dimensions[0], dimensions[1]);
                Maze generatedMaze = generateSolvableMaze(new MyMazeGenerator(), dimensions[0], dimensions[1]);

                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                try (MyCompressorOutputStream compressor = new MyCompressorOutputStream(byteStream)) {
                    compressor.write(generatedMaze.toByteArray());
                    compressor.flush();
                }

                toClient.writeObject(byteStream.toByteArray());
                toClient.flush();
                generatorLogger.info("Server generated and compressed maze successfully - Start: {}, Goal: {}",
                        generatedMaze.getStartPosition(),
                        generatedMaze.getGoalPosition());
            } catch (Exception e) {
                generatorLogger.error("Server failed to generate maze - {}", e.getMessage(), e);
            }
        }
    }

    private static class ServerStrategySolveSearchProblem implements IServerStrategy {

        @Override
        public void serverStrategy(InputStream inFromClient, OutputStream outToClient) {
            try {
                ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
                toClient.flush();
                ObjectInputStream fromClient = new ObjectInputStream(inFromClient);

                Maze mazeToSolve = (Maze) fromClient.readObject();
                solverLogger.info("Server received maze solving request - Algorithm: BestFirstSearch, Start: {}, Goal: {}",
                        mazeToSolve.getStartPosition(),
                        mazeToSolve.getGoalPosition());

                BestFirstSearch searcher = new BestFirstSearch();
                Solution solution = searcher.solve(new SearchableMaze(mazeToSolve));

                toClient.writeObject(solution);
                toClient.flush();

                int steps = solution == null || solution.getSolutionPath() == null
                        ? 0
                        : solution.getSolutionPath().size();
                solverLogger.info("Server solved maze successfully - Steps: {}", steps);
            } catch (Exception e) {
                solverLogger.error("Server failed to solve maze - {}", e.getMessage(), e);
            }
        }
    }
}
