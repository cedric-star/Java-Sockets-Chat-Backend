package protocol;

public interface Commands {
    byte updateFile = 1;    // Client -> Server (XML oder MP3 updaten oder erstellen)
    byte deleteFile = 2;    // Client -> Server (Cleint löscht datei auf server
    byte syncAll = 3;        // Server -> Client (sendet alle für user neu)


}
