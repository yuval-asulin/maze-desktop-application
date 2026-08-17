package IO;

import algorithms.mazeGenerators.Maze;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Compresses a maze byte array using bit-packing on the maze cell data.
 * Metadata header is written verbatim; each maze cell (0 or 1) is packed
 * into individual bits — 8 cells per byte — achieving ~8x compression on
 * the cell data compared to the uncompressed representation.
 */
public class MyCompressorOutputStream extends OutputStream {

    private final OutputStream out;

    public MyCompressorOutputStream(OutputStream out) {
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

        // Bit-pack maze cells: 8 cells per output byte
        int bitBuffer = 0;
        int bitCount = 0;
        for (int i = Maze.MAZE_METADATA_BYTES; i < b.length; i++) {
            bitBuffer = (bitBuffer << 1) | (b[i] & 1);
            bitCount++;
            if (bitCount == 8) {
                out.write(bitBuffer);
                bitBuffer = 0;
                bitCount = 0;
            }
        }
        // Flush remaining bits (padded with 0s)
        if (bitCount > 0) {
            bitBuffer <<= (8 - bitCount);
            out.write(bitBuffer);
        }
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
