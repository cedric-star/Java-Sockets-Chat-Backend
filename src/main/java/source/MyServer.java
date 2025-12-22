package source;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class MyServer {
    private ServerSocket serverSocket;
    private static final ArrayList<ClientHandler> allClients = new ArrayList<>();
    private final String chatFileName;

    public MyServer() {
        this.chatFileName = "mydata";
    }

    public void start(int port) {
        System.out.println("Server Starten...");

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Server gestartet auf port: " + port);

        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            ClientHandler clientHandler = new ClientHandler(clientSocket, this);
            allClients.add(clientHandler);
            new Thread(clientHandler).start();
        }

    }
}