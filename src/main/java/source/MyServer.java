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

    public void broadcastToAll(String message) {
        if (allClients.isEmpty()) {
            System.err.println("kein clients gestartet");
        } else {
            for (ClientHandler client : allClients) {
                client.sendMessage(message);
            }
        }
    }

    public void notifyMsg() {
        broadcastToAll(ReadChat.getChat().toString());
    }
}