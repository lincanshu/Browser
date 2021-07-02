package Downloader;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import java.io.File;

public class ShowDownloadTask extends Thread{

    private File file;
    private long totalLength;
    private AnchorPane an;
    private ProgressBar progress;

    ShowDownloadTask(AnchorPane an, File file, long totalLength) {

        this.file = file;
        this.totalLength = totalLength;
        this.an = an;
        CreatePane();
    }

    private void CreatePane() {

        HBox hBox = CreateHBox();
        an.getChildren().add(hBox);
        AnchorPane.setTopAnchor(hBox, 50.0);
    }

    private HBox CreateHBox() {

        HBox hBox = new HBox(30);
        hBox.setPadding(new Insets(5, 0,0 ,60));

        Label label = new Label(file.getName());
        label.setFont(new Font(20));

        progress = new ProgressBar(0);
        progress.setMinHeight(30);
        progress.setMaxWidth(Double.MAX_VALUE);

        hBox.getChildren().addAll(label, progress);
        return hBox;
    }

    public void run() {

        long length;
        do {
            length = file.length();
            System.out.println(1.0*length/totalLength);
            progress.setProgress(1.0*length/totalLength);
            try {
                Thread.sleep(200);
            } catch (InterruptedException i) {
                i.printStackTrace();
            }
        }while (length != totalLength);

        an.getChildren().remove(0);
    }
}
