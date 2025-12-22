package source;

import protocol.Commands;

import java.io.*;
import java.net.*;
import java.nio.file.Files;

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

            //String initial = ReadXML.getChat().toString();
            //out.writeUTF(initial);
            out.flush();


            while (true) {
                System.out.println("Nachricht:");

                String user = in.readUTF();
                System.out.println("    userid: "+user);
                createFolder(user);

                String filename = in.readUTF();
                System.out.println("    filename: "+filename);

                long len = in.readLong();
                System.out.println("    filelen: "+len);

                byte[] content = in.readNBytes(Math.toIntExact(len));

                String savedir = "/users/"+user;
                File file = new File(savedir, filename);

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content);
                }
                System.out.println("    file saved as: "+file.getPath());

                //sende kompletten shit zurück

            }

        } catch (Exception e) {
            System.out.println("Connectio zu " + clientSocket.getInetAddress() + " getrennt: " + e.getMessage());
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

    public void createFolder(String user) {
        //Files.exists()
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