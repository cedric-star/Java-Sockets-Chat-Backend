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
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadXML {
    private static final String jsonFile = "chat.json";
    private static final String xmlFile = "chat.xml";
    private static final String xsltFile = "stylesheet.xslt";
    private static final String htmlFile = "out.html";

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
            File xmlF = new File(xmlFile);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlF);

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

    private static void printWithXPath() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);

            XPathFactory xpathfactory = XPathFactory.newInstance();
            XPath xpath = xpathfactory.newXPath();

            // User-Elemente statt Text
            XPathExpression expr = xpath.compile("/messages/message/user");

            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;

            System.out.println("=== user ===");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node userNode = nodes.item(i);
                System.out.println("user: " + userNode.getTextContent());
                //System.out.println("node name: " + userNode.getNodeName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static synchronized void gotMSG(String message) {
        System.out.println("\n");
        printWithXPath();
        System.out.println("\n\n");
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

    public static void genHTML() {
        Source srcXML = new StreamSource(xmlFile);
        Source srcXSLT = new StreamSource(xsltFile);

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(srcXSLT);

            StreamResult res = new StreamResult(htmlFile);
            t.transform(srcXML, res);
            System.out.println("Completed XSLT transofmation");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
