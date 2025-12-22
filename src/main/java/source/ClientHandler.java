package source;

import protocol.Commands;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

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
            out.flush();


            while (true) {
                System.out.println("\nNachricht:");

                byte cmd = in.readByte();

                switch (cmd) {
                    case Commands.updateFile:
                        updateFile(in);
                        break;
                    case Commands.deleteFile:
                        deleteFile(in);
                        break;
                    case Commands.syncAll:
                        syncAll(in, out);
                        break;


                }


                //sende kompletten shit zurück

            }

        } catch (Exception e) {
            System.out.println("Connection to" + clientSocket.getInetAddress() + " removed: " + e.getMessage());
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

    private void updateFile(DataInputStream in) throws IOException{
        System.out.println("    updating file...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        String filename = in.readUTF();
        System.out.println("    filename: "+filename);

        long len = in.readLong();
        System.out.println("    filelen: "+len);

        byte[] content = in.readNBytes(Math.toIntExact(len));


        IO.saveFile(user, filename, content);
    }

    private void deleteFile(DataInputStream in) throws IOException{
        System.out.println("    deleting file...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        String filename = in.readUTF();
        System.out.println("    filename: "+filename);

        IO.deleteFile(user, filename);
    }

    private void syncAll(DataInputStream in, DataOutputStream out) throws IOException{
        System.out.println("    synching...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        ArrayList<File> files = IO.sendAllFiles(user);
        int filenum = files.size();
        out.writeInt(filenum);

        for (File file : files) sendFile(file, out);
        out.flush();
    }

    private void sendFile(File file, DataOutputStream out) throws IOException {
        out.writeUTF(file.getName());
        out.writeLong(file.length());
        out.write(Files.readAllBytes(file.toPath()));
    }



}