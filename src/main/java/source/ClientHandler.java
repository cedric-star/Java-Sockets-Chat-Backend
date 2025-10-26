package source;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private DataInputStream in;
    private DataOutputStream out;
    private final MyServer server;
    private final Socket clientSocket;

    public ClientHandler(Socket socket, MyServer server) {
        this.clientSocket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            String message;
            while (true) {
                message = in.readUTF();
                System.out.println("Nachricht von " + clientSocket.getInetAddress() + ": " + message);

                //schreiben zentral im server für synchronisation
                server.addMessageToFile(message);
                server.notifyMsg();
            }

        } catch (Exception e) {
            System.out.println("Verbindung zu " + clientSocket.getInetAddress() + " getrennt: " + e.getMessage());
        } finally {
            // Client aus Liste entfernen
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}