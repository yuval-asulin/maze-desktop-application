package Server;

import IO.MyCompressorOutputStream;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.EmptyMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.SimpleMazeGenerator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ServerStrategyGenerateMaze implements IServerStrategy {

    @Override
    public void serverStrategy(InputStream inFromClient, OutputStream outToClient) {
        try {
            ObjectOutputStream toClient = new ObjectOutputStream(outToClient);
            toClient.flush();
            ObjectInputStream fromClient = new ObjectInputStream(inFromClient);

            int[] dimensions = (int[]) fromClient.readObject();
            AMazeGenerator generator = createMazeGenerator();
            Maze maze = generator.generate(dimensions[0], dimensions[1]);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            MyCompressorOutputStream compressor = new MyCompressorOutputStream(byteStream);
            compressor.write(maze.toByteArray());
            compressor.flush();

            toClient.writeObject(byteStream.toByteArray());
            toClient.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AMazeGenerator createMazeGenerator() {
        String algorithm = Configurations.getInstance().getMazeGeneratingAlgorithm();
        if ("EmptyMazeGenerator".equalsIgnoreCase(algorithm)) {
            return new EmptyMazeGenerator();
        }
        if ("SimpleMazeGenerator".equalsIgnoreCase(algorithm)) {
            return new SimpleMazeGenerator();
        }
        return new MyMazeGenerator();
    }
}
