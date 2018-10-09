
package clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author testn
 */
public class ChatClient extends Application {

    private String userName;
    
    void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void start(Stage primaryStage) {

        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Enter your user name:");
        dialog.setContentText("Enter your user name:");
        userName = "DefaultUSer";
        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            userName = result.get();
        }

        GridPane grid = new GridPane();
        grid.add(new Label("Message"), 0, 0);
        Button sendButton = new Button("Send");
        TextArea chat = new TextArea();
        TextField message = new TextField();
        grid.add(message, 1, 0);
        grid.add(sendButton, 2, 0);

        grid.add(chat, 1, 2, 1, 1);
        StackPane root = new StackPane();
        
        Scene scene = new Scene(grid, 600, 300);

        primaryStage.setTitle(userName);
        primaryStage.setScene(scene);
        primaryStage.show();

        Thread readThread;
        readThread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("localhost", 8000);
                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    while (true) {
                        String response = reader.readLine();
                        response =  "\n" + response + chat.getText();
                        chat.setText(response);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        readThread.start();

        Thread writeThread;
        writeThread = new Thread() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("localhost", 8000);
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);

                    setUserName(userName);
                    writer.println(userName);

                } catch (IOException ex) {
                    Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        writeThread.start();

        sendButton.setOnAction((ActionEvent event) -> {
            try {
                Socket socket = new Socket("localhost", 8000);
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(userName + ": " + message.getText());
                message.setText("");
            } catch (IOException ex) {
                Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}
