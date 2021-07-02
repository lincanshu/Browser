package Browser;

import Downloader.Downloader;
import Email.Email;
import com.sun.xml.internal.ws.util.ReadAllStream;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Browser {

    private static TabPane content;
    private static Stage primaryStage;

    private URL homePage;
    private TextField addressBar;
    private TextField searchBar;
    private WebEngine engine;
    private Background background;

    Browser() {

    }

    private void whenNoTabs() {
        if(content.getTabs().isEmpty()) {
            primaryStage.close();
        }
    }

    private void Initialize() {

        Downloader downloader = new Downloader(primaryStage);
    }

    Browser(TabPane con, Stage stage) {

        Initialize();
        content = con;
        primaryStage = stage;
        Tab tab = CreateNewTab();
        tab.setOnClosed(event -> {
            whenNoTabs();
        });
        content.getTabs().add(tab);

        Scene scene = new Scene(content);

        primaryStage.setScene(scene);
        primaryStage.setTitle("儿子");
        primaryStage.setHeight(900);
        primaryStage.setWidth(1350);
        primaryStage.setOnCloseRequest(event ->  System.exit(0));
        primaryStage.show();
    }

    private MenuButton CreateMenuButton(String text, String url) {

        MenuButton menuButton = new MenuButton(text);
        menuButton.setGraphic(new ImageView(new Image(url, 20, 20, true, true)));
        menuButton.setBackground(background);

        return menuButton;
    }

    private Button CreateButton(String tip, String location) {

        Button button = new Button();
        button.setPrefSize(20, 20);
        button.setGraphic(new ImageView(new Image(location, 20, 20, true, true)));
        button.setBackground(background);
        button.setTooltip(new Tooltip(tip));

        return button;
    }

    private TextField CreateTextField(String tip, String text) {

        TextField textField = new TextField();
        textField.setBackground(background);
        textField.setPromptText(text);
        textField.setTooltip(new Tooltip(tip));

        return  textField;
    }

    private Tab CreateNewTab() {

        try {
            homePage = new URL("http://www.baidu.com");
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnchorPane root = new AnchorPane();
        Tab result = new Tab("New Tab", root);

        WebView web = new WebView();
        web.setContextMenuEnabled(false);

        engine = web.getEngine();
        engine.load(homePage.toString());

        WebHistory history = engine.getHistory();

        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf("#FFFFFF"), new CornerRadii(30), new Insets(2));
        background = new Background(backgroundFill);

        HBox hbox = new HBox(20);

        Button goForward = CreateButton("前进", "image/next.png");

        Button goback = CreateButton("后退", "image/back.png");

        Button home = CreateButton("主页", "image/home.png");

        Button refresh = CreateButton("刷新(F5)", "image/refresh.png");

        Button search = CreateButton("搜索", "image/search.png");

        Button add = CreateButton("新建标签页", "image/add.png");

        Button bookmark = CreateButton("添加到收藏夹", "image/star.png");

        Button download = CreateButton("下载", "image/download.png");

        Button email= CreateButton("电子邮件", "image/mail.png");


        MenuButton record = CreateMenuButton("历史记录", "image/record.png" );

        MenuButton favorites = CreateMenuButton("收藏夹", "image/favorites.png");

        ProgressBar progress = new ProgressBar();
        progress.setMinHeight(30);
        progress.setMaxWidth(Double.MAX_VALUE);

        addressBar = CreateTextField("地址栏", "请输入网址");
        addressBar.setPrefSize(300, 30);

        searchBar = CreateTextField("搜索栏", "搜索");
        searchBar.setPrefSize(200, 30);

        hbox.getChildren().addAll(goback, goForward, refresh, home, progress, addressBar, searchBar, search, add, bookmark, download, email, record, favorites);

        root.getChildren().addAll(hbox, web);
        AnchorPane.setTopAnchor(web, 30.0);

        web.prefHeightProperty().bind(content.heightProperty().subtract(30));
        web.prefWidthProperty().bind(content.widthProperty());
        hbox.prefWidthProperty().bind(content.widthProperty());

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == Worker.State.SUCCEEDED) {
                addressBar.setText(engine.getLocation());
                for (MenuItem item : record.getItems()) {
                    if(item.getText().equals(engine.getLocation())) {
                        return ;
                    }
                }
                MenuItem item = new MenuItem(engine.getLocation());
                item.setOnAction(event -> engine.load(item.getText()));
                record.getItems().add(item);
                searchBar.setText("");
                result.setText(engine.getTitle());
            }
//            下载
            else if (newValue == Worker.State.CANCELLED) {
                Downloader downloader = new Downloader(engine.getLocation());
                if(downloader.isDownloadable()) {
                    downloader.download();
                }
                return;
            }
        });


//        此处主要是将焦点聚焦在地址栏
        Platform.runLater(addressBar::requestFocus);

        goForward.setOnAction(event -> {
            try {
                history.go(1);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.setContentText("没有可以前进的页面");
                alert.showAndWait();
                return;
            }
        });

        goback.setOnAction(event -> {
            try {
                history.go(-1);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initModality(Modality.WINDOW_MODAL);
                alert.setContentText("没有可以后退的页面");
                alert.showAndWait();
                return;
            }
        });

        refresh.setOnAction(event -> engine.reload());
        refresh.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.F5)) {
                engine.reload();
            }
        });

        home.setOnAction(event -> {
            engine.load(homePage.toString());
            addressBar.setText(homePage.toString());
        });


        progress.progressProperty().bind(engine.getLoadWorker().progressProperty());

        searchBar.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                search();
            }
        });

        addressBar.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                smart_search();
            }
        });

        search.setOnAction(event -> search());

        add.setOnAction((ActionEvent event) -> {
            Browser browser = new Browser();
            Tab tab = browser.CreateNewTab();
            tab.setOnClosed(eve-> whenNoTabs());
            content.getTabs().add(tab);
            content.getSelectionModel().select(tab);
        });

        bookmark.setOnAction(event -> {
            MenuItem item = new MenuItem(engine.getLocation());
            item.setOnAction(e -> {
                engine.load(item.getText());
            });
            favorites.getItems().add(item);
        });

        download.setOnAction(event -> {
            Downloader.show();
        });

        email.setOnAction(event -> {
            new Email(primaryStage);
        });

        return result;
    }

    private void smart_search() {
        String search_content = addressBar.getText();

//        判断是否是合法IP或者是合法域名
        if(isVaildIP(search_content) || isValidDomainName(search_content)) {
//            System.out.println(search_content);
            engine.load("http:\\" + addressBar.getText());
        }
        else {
           GotoNotFoundPage();
        }
    }

    private void GotoNotFoundPage() {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("src\\Browser\\404.html")));
            StringBuilder body = new StringBuilder();
            String temp;
            while ((temp = reader.readLine()) != null) {
                body.append(temp + "\n");
            }
            engine.loadContent(body.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void search() {
        String search_content = searchBar.getText();
        engine.load("http://www.baidu.com/s?wd=" + search_content);
    }

    private boolean isVaildIP(String content) {

        String pattern = "(2[0-5]{2}|[0-1]?\\d{1,2})(\\.(2[0-5]{2}|[0-1]?\\d{1,2})){3}(:(6[0-5]{2}[0-3][0-5$]|[1-5]\\d{4}|[1-9]\\d{1,3}|[0-9])|)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);

        return m.matches();
    }

    private boolean isValidDomainName(String content) {

        if (!content.contains(".")) {
            return  false;
        }
        try {
            InetAddress address = InetAddress.getByName(content);
            System.out.println(address.getHostAddress());
            return true;
        } catch (UnknownHostException u) {
            return false;
        }
    }
}
