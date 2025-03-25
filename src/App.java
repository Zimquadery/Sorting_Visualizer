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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("view.fxml"));
        AnchorPane root = loader.load();
        Scene scene = new Scene(root);
        Controller controller = loader.getController();
        controller.setRoot(root);
        controller.randomizer(null);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Visualizer");
        primaryStage.show();
    }
}
