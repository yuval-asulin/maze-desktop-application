package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    private final int port;
    private final int listeningIntervalMS;
    private final IServerStrategy serverStrategy;
    private volatile boolean stop;
    private ExecutorService threadPool;

    public Server(int port, int listeningIntervalMS, IServerStrategy serverStrategy) {
        this.port = port;
        this.listeningIntervalMS = listeningIntervalMS;
        this.serverStrategy = serverStrategy;
    }

    public void start() {
        stop = false;
        threadPool = Executors.newFixedThreadPool(Configurations.getInstance().getThreadPoolSize());
        new Thread(this::runServer).start();
    }

    private void runServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(listeningIntervalMS);

            while (!stop) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.execute(() -> handleClient(clientSocket));
                } catch (SocketTimeoutException ignored) {
                    // Timeout lets the server check whether stop() was called.
                }
            }
        } catch (IOException e) {
            if (!stop) {
                e.printStackTrace();
            }
        } finally {
            shutdownThreadPool();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (Socket socket = clientSocket) {
            serverStrategy.serverStrategy(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
    }

    private void shutdownThreadPool() {
        if (threadPool == null) {
            return;
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(listeningIntervalMS, TimeUnit.MILLISECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
