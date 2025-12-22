package protocol;

public interface Commands {
    byte updateFile = 1;    // Client -> Server (XML oder MP3 updaten oder erstellen)
    byte sync = 2;          // Server -> Client (sendet alle für user neu)
    byte delete = 3;        // Client -> Server (Cleint löscht datei auf server

}
