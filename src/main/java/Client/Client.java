package Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private final InetAddress serverIP;
    private final int serverPort;
    private final IClientStrategy clientStrategy;

    public Client(InetAddress serverIP, int serverPort, IClientStrategy clientStrategy) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientStrategy = clientStrategy;
    }

    public void communicateWithServer() {
        try (Socket serverSocket = new Socket(serverIP, serverPort)) {
            clientStrategy.clientStrategy(serverSocket.getInputStream(), serverSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
