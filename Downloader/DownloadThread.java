package Downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread{

    private File file;
    private URL url;
    private long start;
    private long end;

    DownloadThread(File file, URL url, long start, long end) {
        this.file = file;
        this.url = url;
        this.start = start;
        this.end = end;
    }

    public void run() {

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Range", "bytes=" + start + "-" + end);

            int code = connection.getResponseCode();
            if (code == 200 || code == 206) {
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                out.seek(start);

                byte[] data = new byte[1024];
                int length;
                while ((length = in.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, length);
                }
                in.close();
                out.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
