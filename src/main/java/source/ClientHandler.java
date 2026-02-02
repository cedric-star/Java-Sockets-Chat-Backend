package source;

import protocol.Commands;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    private DataInputStream in;
    private DataOutputStream out;
    private final MyServer server;
    private final Socket clientSocket;
    private IO io;

    /**
     * Verwaltet die Verbindung zwischen einem Client und dem Server.
     * Liefert Methoden zum löschen, snchronisieren, ...
     * @param socket
     * @param server
     */
    public ClientHandler(Socket socket, MyServer server) {
        this.clientSocket = socket;
        this.server = server;
        this.io = IO.getInstance();
    }

    /**
     * Als Thread für jeden Client gestartet, mit ClientHandler.start() aufrufen,
     * nicht mit ClientHanlder.run()!
     */
    @Override
    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            out.flush();

            while (!clientSocket.isClosed()) {
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
            }

        } catch (Exception e) {
            System.out.println("Connection to" + clientSocket.getInetAddress() + " removed: " + e.getMessage());
        } finally {
            //client aus Liste entfernen
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Liest den Nutzer ein, und erhält anschließend eine Datei, die
     * auf dem Server gespeichert werden soll.
     * @param in
     * @throws IOException
     */
    private void updateFile(DataInputStream in) throws IOException{
        System.out.println("    updating file...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        String filename = in.readUTF();
        System.out.println("    filename: "+filename);

        long len = in.readLong();
        System.out.println("    filelen: "+len);

        byte[] content = in.readNBytes(Math.toIntExact(len));

        io.saveFile(user, filename, content);
    }

    /**
     * Liest den Nutzer und Dateinamen um die jeweilige Datei zu löschen.
     * @param in
     * @throws IOException
     */
    private void deleteFile(DataInputStream in) throws IOException{
        System.out.println("    deleting file...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        String filename = in.readUTF();
        System.out.println("    filename: "+filename);

        io.deleteFile(user, filename);
    }

    /**
     * Liest den jeweiligen Nutzer ein, um aus seinem Verzeichnis
     * alle vorhandenen Dateien zu schicken.
     * @param in
     * @param out
     * @throws IOException
     */
    private void syncAll(DataInputStream in, DataOutputStream out) throws IOException{
        System.out.println("    synching...");
        String user = in.readUTF();
        System.out.println("    userid: "+user);

        ArrayList<File> files = io.sendAllFiles(user);
        int filenum = files.size();
        out.writeInt(filenum);

        for (File file : files) sendFile(file, out);
        out.flush();
    }

    /**
     * Sendet eine einzelne Datei (Nutzer sollte woanders gesetzt werden).
     * @param file
     * @param out
     * @throws IOException
     */
    private void sendFile(File file, DataOutputStream out) throws IOException {
        out.writeUTF(file.getName());
        out.writeLong(file.length());
        out.write(Files.readAllBytes(file.toPath()));
    }
}