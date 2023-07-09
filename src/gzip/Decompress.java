package gzip;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class Decompress {
    private static final int BUFFER_SIZE = 1024;

    public static void decompressGzipFile(String gzipFilePath) throws IOException {
        decompressGzipFile(new File(gzipFilePath));
    }

    public static void decompressGzipFile(File gzipFile) throws IOException {
        if (gzipFile.isDirectory()) {
            File[] gzipFiles = gzipFile.listFiles();
            if (gzipFiles == null) {
                throw new FileNotFoundException("No logs found.");
            }
            for (File file : gzipFiles) {
                decompressGzipFile(file);
            }
        } else {
            String gzipFilePath = gzipFile.getAbsolutePath();
            GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzipFile));
            FileOutputStream fos = new FileOutputStream(gzipFilePath.substring(0, gzipFilePath.lastIndexOf(".")) + ".log");

            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }

            fos.close();
            gis.close();
        }
    }
}
