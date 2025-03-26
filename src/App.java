import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file and set up the scene
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        
        // Get the controller and set the root pane
        Controller controller = loader.getController();
        controller.setRoot(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Visualizer");
        primaryStage.show();
    }
}