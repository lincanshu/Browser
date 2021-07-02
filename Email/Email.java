package Email;

import com.sun.mail.util.MailSSLSocketFactory;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.Properties;

public class Email{

    private TextArea editor;
    private TextField subject;
    private TextField to;
    private Stage stage;

    private void Initialize() {

        to = new TextField("2017163058@email.szu.edu.cn");
        to.setFont(new Font(14));
        to.setPrefColumnCount(20);

        subject = new TextField("");
        subject.setFont(new Font(14));
        subject.setPrefColumnCount(20);
    }

    private HBox CreateHBox(String temp, TextField textField) {

        HBox hBox = new HBox(30);
        hBox.setPadding(new Insets(5, 0,0 ,60));

        Label label = new Label(temp);
        label.setFont(new Font(20));

        hBox.getChildren().addAll(label, textField);

        return hBox;
    }

    private AnchorPane CreatePane() {

        AnchorPane an = new AnchorPane();
        HBox hBox_to = CreateHBox("TO", to);
        HBox hBox_subject = CreateHBox("Subject", subject);
        AnchorPane.setLeftAnchor(hBox_to, 20.0);
        AnchorPane.setTopAnchor(hBox_to, 30.0);

        AnchorPane.setLeftAnchor(hBox_subject, 20.0);
        AnchorPane.setTopAnchor(hBox_subject, 70.0);

        editor = new TextArea();
        editor.setPrefSize(500, 200);
        AnchorPane.setTopAnchor(editor, 140.0);
        AnchorPane.setLeftAnchor(editor, 30.0);
        AnchorPane.setRightAnchor(editor, 30.0);

        Button ok = new Button("send");
        AnchorPane.setTopAnchor(ok, 40.0);
        AnchorPane.setRightAnchor(ok, 50.0);

        an.getChildren().addAll(hBox_to, hBox_subject, editor, ok);

        ok.setOnAction(event -> {
            sendEmail();
            stage.close();
        });

        return an;
    }

    public Email(Stage primaryStage) {

        Initialize();

        AnchorPane an = CreatePane();

        stage = new Stage();
        stage.initOwner(primaryStage);
        stage.initModality(Modality.WINDOW_MODAL);

        Scene scene = new Scene(an);
        stage.setScene(scene);

        stage.setTitle("Email");
        stage.setWidth(600);
        stage.setHeight(400);
        stage.show();

    }

    private void sendEmail() {
        String from = "616092943@qq.com";
        String host = "smtp.qq.com";
        Properties properties = System.getProperties();

        properties.setProperty("mail.smtp.host", host);
        properties.put("mail.smtp.auth", "true");

        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException g) {
        }
        try {
            sf.setTrustAllHosts(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.ssl.socketFactory", sf);

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("616092943@qq.com" ,"yacokyvrkqffbgah");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getText()));
            message.setSubject(subject.getText());
            message.setText(editor.getText());

            Transport.send(message);

        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

}
