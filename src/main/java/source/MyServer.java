package source;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        broadcastToAll(readChat());
    }

    private String readChat() {
        String s = null;
        try {
            Path path = Paths.get("mydata");
            if (!Files.exists(path)) return "Chat ist leer";
            byte[] fileBytes = Files.readAllBytes(path);
            s = new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Chat-Datei: " + e.getMessage());
            s = "Fehler beim Laden des Chats";
        }
        return s;
    }

    public synchronized void addMessageToFile(String msg) {
        try {
            Path path = Paths.get(chatFileName);
            String toAppend = msg + System.lineSeparator();// \n für unix; \r\n für windows
            Files.writeString(path, toAppend, StandardOpenOption.APPEND);

        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben in Chat-Datei: " + e.getMessage());
        }
    }
}