package IO;

import algorithms.mazeGenerators.Maze;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Compresses a maze byte array using RLE on the 0/1 maze data.
 * The metadata header (first MAZE_METADATA_BYTES bytes) is written verbatim.
 * The maze cells (0s and 1s) are RLE-encoded as run counts starting from 0.
 */
public class SimpleCompressorOutputStream extends OutputStream {

    private final OutputStream out;

    public SimpleCompressorOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b});
    }

    @Override
    public void write(byte[] b) throws IOException {
        // Write metadata header verbatim
        out.write(b, 0, Maze.MAZE_METADATA_BYTES);

        // RLE-encode the maze cell data (all 0s and 1s)
        int current = 0; // RLE starts counting 0s first
        int count = 0;
        for (int i = Maze.MAZE_METADATA_BYTES; i < b.length; i++) {
            int cell = b[i] & 0xFF;
            if (cell == current) {
                count++;
                if (count == 255) {
                    out.write(255);
                    count = 0;
                }
            } else {
                out.write(count);
                current = cell;
                count = 1;
            }
        }
        out.write(count); // flush last run
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
