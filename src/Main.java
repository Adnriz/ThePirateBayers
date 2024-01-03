import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainWindow.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("My Movie Collection");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}