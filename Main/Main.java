package Browser;

import javafx.application.Application;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class Main extends Application {

    public void start(Stage primaryStage){

        TabPane content = new TabPane();
        new Browser(content, primaryStage);
    }

    public static void main(String []args) {
        launch(args);
    }

}
