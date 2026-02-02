package source;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class IO {
    private static IO INSTANCE;
    private IO() {}

    /**
     * Diese Klasse ist als Singleton implementiert.
     * @return
     */
    public static IO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IO();
        }
        return INSTANCE;
    }

    /**
     * Sichert per Socket (als ByteStream) gesendete Dateien.
     * @param user
     * @param filename
     * @param content
     */
    public synchronized void saveFile(String user, String filename, byte[] content) {
        System.out.println("\nSaving File: "+filename);

        File baseDir = new File("java_xml_mp3_user_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File userDir = new File(baseDir, user);
        if (!userDir.exists()) userDir.mkdirs();

        File file = new File(userDir, filename);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("File saved: "+file.getAbsolutePath());

        genHTML(user);
    }

    /**
     * Löscht Datei für jeweilgen Nutzer anhand o Dateinamen.
     * @param user
     * @param filename
     */
    public synchronized void deleteFile(String user, String filename) {
        System.out.println("\nDeleting File: "+filename);

        File baseDir = new File("java_xml_mp3_user_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File userDir = new File(baseDir, user);
        if (!userDir.exists()) userDir.mkdirs();

        File fileDir = new File(userDir, filename);

        try {
            if (fileDir.exists()) fileDir.delete();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        System.out.println("file deleted? "+(!fileDir.exists()));

        genHTML(user);
    }

    /**
     * Geht in einen Nutzerordner und gibt alle vorhandenen Dateien in Liste zurück.
     * @param user
     * @return
     */
    public synchronized ArrayList<File> sendAllFiles(String user) {
        File baseDir = new File("java_xml_mp3_user_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File userDir = new File(baseDir, user);
        if (!userDir.exists()) userDir.mkdirs();
        genHTML(user);
        File[] files = userDir.listFiles();

        return new ArrayList<File>(Arrays.asList(files));
    }

    /**
     * Generierung der HTML-Datei passiert Serverseitig hier, danach
     * sollten alle Dateien an den Nutzer geschickt werden (syncAll).
     * @param user
     */
    private synchronized void genHTML(String user) {
        File baseDir = new File("java_xml_mp3_user_data");
        if (!baseDir.exists()) baseDir.mkdirs();

        File userDir = new File(baseDir, user);
        if (!userDir.exists()) userDir.mkdirs();

        File xml = new File(userDir, user+"_music.xml");
        File xslt = new File(userDir, user+"_style.xslt");
        File html = new File(userDir, user+"_index.html");

        if (!xml.exists()) return;
        if (!xslt.exists()) {
            try {
                Files.copy(new File("stylesheet.xslt").toPath(), xslt.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        Source srcXML = new StreamSource(xml);
        Source srcXSLT = new StreamSource(xslt);

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer(srcXSLT);

            StreamResult res = new StreamResult(html);
            t.transform(srcXML, res);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
