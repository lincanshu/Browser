package Downloader;


import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Downloader {

    private static Stage downloadStage;
    private static Stage primaryStage;
    private static AnchorPane an;

    private String sURL;

    public Downloader(Stage stage) {
        primaryStage = stage;
        CreateDownloadStage();
    }

    private void CreateDownloadStage() {

        an = new AnchorPane();

        downloadStage = new Stage();
        downloadStage.initOwner(primaryStage);
        downloadStage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(an);
        downloadStage.setScene(scene);

        downloadStage.setTitle("Download");
        downloadStage.setWidth(600);
        downloadStage.setHeight(400);
    }

    public static void show () {

        downloadStage.show();
    }

    public Downloader(String sURL) {

        this.sURL = sURL;
    }

    private long GetLength(URL url) {

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection.getContentLengthLong();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public void download() {

        System.setProperty("https.protocols", "TLSv1.1");
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("D:\\WebDownloads"));
        File directory = chooser.showDialog(primaryStage);
        if(directory != null) {
            String filepath = directory.getPath();
            try {
                URL url = new URL(sURL);
                long length = GetLength(url);

                String fileName = filepath + "\\" + url.getFile().substring(url.getFile().lastIndexOf("/") + 1);
                File file = new File(fileName);
                DownloadThreadPool pool = new DownloadThreadPool(url, file, length);
                pool.start(4);

                Thread t = new ShowDownloadTask(an, file, length);
                t.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isDownloadable() {

        try {
            URL url = new URL(sURL);
            URLConnection connection = url.openConnection();

            String flag = connection.getContentType();
            if(flag.contains("application") || flag.contains("video") || flag.contains("audio") || flag.contains("image")) {
                return true;
            }

        } catch (IOException e) {
            return false;
        }
        return false;
    }
}

