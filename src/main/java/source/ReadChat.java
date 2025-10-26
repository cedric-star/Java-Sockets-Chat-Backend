package source;

import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ReadChat {
    private static final String jsonFile = "chat.json";

    public static synchronized JSONArray getChat() {
        String s = null;
        try {
            Path path = Paths.get(jsonFile);
            byte[] fileBytes = Files.readAllBytes(path);
            s = new String(fileBytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Chat-Datei: " + e.getMessage());
            s = "Fehler beim Laden des Chats";
        }

        return new JSONArray(s);
    }

    public static synchronized void addMsg(String message) {
        JSONObject obj = new JSONObject(message);
        obj.put("timestamp", java.time.Instant.now().toString());

        JSONArray arr;
        try {
            String content = Files.readString(Paths.get("chat.json"));
            arr = new JSONArray(content);
            arr.put(obj);

            Files.writeString(Paths.get("chat.json"), arr.toString());

        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben in Chat-Datei: " + e.getMessage());
        }
    }

}
