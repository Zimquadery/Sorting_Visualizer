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
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sorting Algorithm Visualiser");
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("icon.png")));
        
        primaryStage.show();
    }
}