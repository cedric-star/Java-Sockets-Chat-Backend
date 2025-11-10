package source;

import org.json.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    private static void write2XML(String msg, String usr, String ts) {
        try {
            File xmlFile = new File("chat.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            Node root = document.getElementsByTagName("messages").item(0);
            Node elem = document.createElement("message");

            Node userElem = document.createElement("user");
            userElem.setTextContent(usr);
            elem.appendChild(userElem);

            Node timestampElem = document.createElement("timestamp");
            timestampElem.setTextContent(ts);
            elem.appendChild(timestampElem);

            Node msgElem = document.createElement("msg");
            msgElem.setTextContent(msg);
            elem.appendChild(msgElem);

            root.appendChild(elem);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource src = new DOMSource(document);
            StreamResult res = new StreamResult(xmlFile);
            transformer.transform(src, res);

        } catch (ParserConfigurationException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        } catch (TransformerConfigurationException e) {
            System.err.println(e.getMessage());
        } catch (TransformerException e) {
            System.err.println(e.getMessage());
        }
    }

    public static synchronized void addMsg(String message) {
        JSONObject obj = new JSONObject(message);
        String ts = java.time.Instant.now().toString();
        obj.put("timestamp", ts);

        write2XML(obj.getString("msg"), obj.getString("user"), ts);

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
