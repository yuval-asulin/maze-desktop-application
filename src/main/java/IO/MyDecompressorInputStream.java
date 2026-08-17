package IO;

import algorithms.mazeGenerators.Maze;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decompresses a byte stream produced by MyCompressorOutputStream.
 * Reads the metadata header verbatim, then unpacks bit-packed maze cells.
 */
public class MyDecompressorInputStream extends InputStream {

    private final InputStream in;
    private final byte[] buffer;
    private int bufferPos;
    private int bufferLen;

    public MyDecompressorInputStream(InputStream in) throws IOException {
        this.in = in;
        this.buffer = decompress();
        this.bufferPos = 0;
        this.bufferLen = buffer.length;
    }

    private byte[] decompress() throws IOException {
        // Read metadata header verbatim
        byte[] header = new byte[Maze.MAZE_METADATA_BYTES];
        int bytesRead = 0;
        while (bytesRead < Maze.MAZE_METADATA_BYTES) {
            int r = in.read(header, bytesRead, Maze.MAZE_METADATA_BYTES - bytesRead);
            if (r == -1) break;
            bytesRead += r;
        }

        // Derive total maze cells from header
        int rows       = ((header[0] & 0xFF) << 8) | (header[1] & 0xFF);
        int columns    = ((header[2] & 0xFF) << 8) | (header[3] & 0xFF);
        int totalCells = rows * columns;

        // Unpack bit-packed cells back into individual 0/1 bytes
        byte[] cells = new byte[totalCells];
        int cellIndex = 0;
        int b;
        while (cellIndex < totalCells && (b = in.read()) != -1) {
            for (int bit = 7; bit >= 0 && cellIndex < totalCells; bit--)
                cells[cellIndex++] = (byte)((b >> bit) & 1);
        }

        // Combine header + cells
        byte[] result = new byte[Maze.MAZE_METADATA_BYTES + totalCells];
        System.arraycopy(header, 0, result, 0, Maze.MAZE_METADATA_BYTES);
        System.arraycopy(cells, 0, result, Maze.MAZE_METADATA_BYTES, totalCells);
        return result;
    }

    @Override
    public int read() throws IOException {
        if (bufferPos >= bufferLen) return -1;
        return buffer[bufferPos++] & 0xFF;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int len = Math.min(b.length, bufferLen - bufferPos);
        System.arraycopy(buffer, bufferPos, b, 0, len);
        bufferPos += len;
        return len;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
