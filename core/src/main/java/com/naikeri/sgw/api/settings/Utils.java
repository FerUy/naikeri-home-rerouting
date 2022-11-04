package com.naikeri.sgw.api.settings;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    private static final Logger logger = Logger.getLogger(Utils.class);

    public InputStream getInputStream(String filename) throws IOException {
        String fullpathFilename = System.getProperty("user.dir") + "/" + filename;
        InputStream inputStream;
        File file = new File(fullpathFilename);
        if (file.exists()) {
            if (logger != null)
                logger.info("Loading configuration from '" + fullpathFilename + "'");
            inputStream = new FileInputStream(file);
        } else {
            if (logger != null)
                logger.info("Loading configuration from 'resources/" + filename + "'");
            inputStream = this.getClass().getClassLoader().getResourceAsStream(filename);
        }

        /*if (inputStream != null) {
            if (logger != null)
                logger.debug("Opening configuration file...");
            BufferedInputStream bin = new BufferedInputStream(inputStream);
            StringBuilder sb = new StringBuilder();
            byte[] contents = new byte[1024];
            String strFileContents;
            int bytesRead = 0;

            while ((bytesRead = bin.read(contents)) != -1) {
                strFileContents = new String(contents, 0, bytesRead);
                sb.append(strFileContents);
            }

            if (logger != null)
                logger.debug("Configuration loaded success!");

            return sb.toString();
        }

        throw new IOException("File not found!");*/

        return inputStream;
    }

    public static String toHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int v = (data[i] & 0xFF);
            if (v <= 0xF) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString();
    }
}