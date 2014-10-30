package main;

import java.io.*;
import java.net.URL;

/**
 * Created by forando on 08.04.14.
 * Class that performs operation with resource file
 */


public class ResourceFile {
    File file = null;
    String path = null;
    URL res = null;

    public ResourceFile(String path) {
        this.path = path;
        res = getClass().getResource(this.path);
    }

    public File getFile() {
        if (res == null) return null;
        if (res.toString().startsWith("jar:")) {
            try {
                InputStream input = getClass().getResourceAsStream(path);
                file = File.createTempFile("tempfile", ".tmp");
                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            //this will probably work in your IDE, but not from a JAR
            file = new File(res.getFile());
        }

        if (file != null && !file.exists()) {
            throw new RuntimeException("Error: File " + file + " not found!");
        }
        return file;
    }
}
