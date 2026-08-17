package Server;

import algorithms.mazeGenerators.Maze;
import algorithms.search.BestFirstSearch;
import algorithms.search.BreadthFirstSearch;
import algorithms.search.DepthFirstSearch;
import algorithms.search.ISearchingAlgorithm;
import algorithms.search.SearchableMaze;
import algorithms.search.Solution;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public class ServerStrategySolveSearchProblem implements IServerStrategy {

    @Override
    public void serverStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            toClient.flush();
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);

            Maze maze = (Maze) fromClient.readObject();
            Solution solution = getCachedSolution(maze);

            toClient.writeObject(solution);
            toClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Solution getCachedSolution(Maze maze) throws Exception {
        File cacheFile = getCacheFile(maze);
        if (cacheFile.exists()) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(cacheFile))) {
                return (Solution) input.readObject();
            }
        }

        ISearchingAlgorithm searcher = createSearchingAlgorithm();
        Solution solution = searcher.solve(new SearchableMaze(maze));
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(cacheFile))) {
            output.writeObject(solution);
            output.flush();
        }
        return solution;
    }

    private File getCacheFile(Maze maze) throws Exception {
        String tempDirectoryPath = System.getProperty("java.io.tmpdir");
        File cacheDirectory = new File(tempDirectoryPath, "ATPProjectMazeSolutions");
        if (!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        return new File(cacheDirectory, hashMaze(maze) + ".solution");
    }

    private String hashMaze(Maze maze) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(maze.toByteArray());
        StringBuilder builder = new StringBuilder();
        for (byte b : hash) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    private ISearchingAlgorithm createSearchingAlgorithm() {
        String algorithm = Configurations.getInstance().getMazeSearchingAlgorithm();
        if ("BreadthFirstSearch".equalsIgnoreCase(algorithm)) {
            return new BreadthFirstSearch();
        }
        if ("DepthFirstSearch".equalsIgnoreCase(algorithm)) {
            return new DepthFirstSearch();
        }
        return new BestFirstSearch();
    }
}
