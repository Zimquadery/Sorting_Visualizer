import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class Controller {

    private AnchorPane visualizationPane;

    void setRoot(AnchorPane root) {
        this.visualizationPane = (AnchorPane) root.lookup("#visualizationPane");
    }

    final int N = 15;  // Default size for random generation
    final int W = 15;
    final int gap = 10;
    ArrayList<Rectangle> rects = new ArrayList<>();

    private byte state = 0; // 0=init, 1=iterating, 2=swapping, 3=done
    private int i = 0;
    private int j = 0;

    @FXML
    private Button rndBtn;

    @FXML
    private Button sortBtn;

    @FXML
    private Button customArrayBtn;

    @FXML
    private TextField customArrayField;

    private Timeline timeline;

    private boolean customInputMode = false;

    @FXML
    void randomizer(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }

        visualizationPane.getChildren().removeAll(rects);
        rects.clear();

        // Generate random rectangles
        for (int i = 0; i < N; i++) {
            Rectangle rect = new Rectangle();
            rect.setWidth(W);
            rect.setHeight(Math.random() * 300);
            rect.setFill(Color.RED);
            rects.add(rect);
        }

        // Center the rectangles in the AnchorPane
        centerRectangles();
        visualizationPane.getChildren().addAll(rects);
    }

    @FXML
    void handleCustomArray(ActionEvent event) {
        if (!customInputMode) {
            // Enter custom input mode
            customInputMode = true;
            customArrayBtn.setText("Apply");
            customArrayField.setVisible(true);
            rndBtn.setDisable(true);
            sortBtn.setDisable(true);
        } else {
            // Apply custom values and exit custom input mode
            customInputMode = false;
            customArrayBtn.setText("Custom Array");
            customArrayField.setVisible(false);
            rndBtn.setDisable(false);
            sortBtn.setDisable(false);

            applyCustomValues();
        }
    }

    private void applyCustomValues() {
        String input = customArrayField.getText().trim();
        if (input.isEmpty()) {
            return;
        }

        try {
            // Parse comma-separated values
            String[] values = input.split(",");

            if (timeline != null) {
                timeline.stop();
            }

            visualizationPane.getChildren().removeAll(rects);
            rects.clear();

            for (String value : values) {
                double height = Double.parseDouble(value.trim());
                Rectangle rect = new Rectangle();
                rect.setWidth(W);
                rect.setHeight(height*2);
                rect.setFill(Color.RED);
                rects.add(rect);
            }

            // Center the rectangles in the AnchorPane
            centerRectangles();
            visualizationPane.getChildren().addAll(rects);
            customArrayField.clear();

        } catch (NumberFormatException e) {
            // Handle invalid input
            customArrayField.setText("Invalid input! Use comma-separated numbers");
            customInputMode = true;
            customArrayBtn.setText("Apply");
            customArrayField.setVisible(true);
            rndBtn.setDisable(true);
            sortBtn.setDisable(true);
        }
    }

    @FXML
    void sort(ActionEvent event) {
        if (timeline != null) {
            timeline.stop();
        }

        // Exit if there are no rectangles to sort
        if (rects.isEmpty()) {
            return;
        }

        state = 0;
        i = 0;
        j = 0;

        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            int arraySize = rects.size();  // Use actual array size instead of N

            switch (state) {
                case 0: // Initialize
                    i = 0;
                    j = 0;
                    state = 1;
                    break;

                case 1: // Compare
                    if (i >= arraySize - 1) {
                        state = 3;
                    }

                    // Reset colors
                    for (int k = 0; k < arraySize - i; k++) {
                        rects.get(k).setFill(Color.RED);
                    }

                    // Highlight current comparison
                    rects.get(j).setFill(Color.BLUE);
                    rects.get(j + 1).setFill(Color.BLUE);

                    if (rects.get(j).getHeight() > rects.get(j + 1).getHeight()) {
                        state = 2; // Need to swap
                    } else {
                        j++;
                        if (j >= arraySize - i - 1) {
                            rects.get(arraySize - i - 1).setFill(Color.GREEN);
                            i++;
                            j = 0;
                        }
                    }
                    break;

                case 2: // Swap
                    // Swap heights
                    double tempHeight = rects.get(j).getHeight();
                    rects.get(j).setHeight(rects.get(j + 1).getHeight());
                    rects.get(j + 1).setHeight(tempHeight);

                    // Move to next comparison
                    j++;
                    if (j >= arraySize - i - 1) {
                        rects.get(arraySize - i - 1).setFill(Color.GREEN);
                        i++;
                        j = 0;
                    }
                    state = 1;
                    break;

                case 3: // Done
                    for (int k = 0; k < arraySize; k++) {
                        rects.get(k).setFill(Color.GREEN);
                    }
                    timeline.stop();
                    return;
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void centerRectangles() {
        double totalWidth = rects.size() * (W + gap) - gap; // Total width of all rectangles including gaps
        double startX = (visualizationPane.getWidth() - totalWidth) / 2; // Calculate starting X position to center the array

        for (int i = 0; i < rects.size(); i++) {
            Rectangle rect = rects.get(i);
            rect.setX(startX + i * (W + gap)); // Set X position for each rectangle
            rect.setY(100); // Fixed Y position for alignment
        }
    }
}