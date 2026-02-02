package source;

public class Main {
    /**
     * Startet Server bei Prgorammstart.
     * @param args
     */
    public static void main(String[] args) {
        MyServer server = new MyServer();
        server.start(16969);
    }
}