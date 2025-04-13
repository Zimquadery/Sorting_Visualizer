import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;

public class Controller {

    private AnchorPane visualizationPane;

    @FXML
    private ChoiceBox<String> Algorithm;

    private String currentAlgorithm = "Bubble Sort";

    void setRoot(AnchorPane root) {
        this.visualizationPane = (AnchorPane) root.lookup("#visualizationPane");

        // Add listener to reposition rectangles when the pane size changes
        visualizationPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!rects.isEmpty()) {
                centerRectangles();
            }
        });

        // Initialize the algorithm choice box
        initAlgorithmChoiceBox();

        randomizer(new ActionEvent()); // Initialize with random rectangles
    }

    private void initAlgorithmChoiceBox() {
        // Initialize algorithm choices
        Algorithm.setItems(FXCollections.observableArrayList(
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort"
        ));

        // Set default value
        Algorithm.setValue("Bubble Sort");

        // Add listener for algorithm selection changes
        Algorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentAlgorithm = newVal;
            }
        });
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
            rect.setHeight(Math.random() * 250);
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
                rect.setHeight(height);
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

        // Use different sorting algorithms based on selection
        switch (currentAlgorithm) {
            case "Bubble Sort":
                bubbleSort();
                break;
            case "Selection Sort":
                selectionSort();
                break;
            case "Insertion Sort":
                insertionSort();
                break;
            default:
                bubbleSort(); // Default to bubble sort
        }
    }

    private void bubbleSort() {
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

    private void selectionSort() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 0;
                    j = i + 1;
                    state = 1;
                    break;

                case 1: // Finding minimum
                    if (i >= arraySize - 1) {
                        state = 3; // Done
                        break;
                    }

                    // Reset colors
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN); // Already sorted
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }

                    // Current minimum and current element
                    rects.get(i).setFill(Color.BLUE);
                    rects.get(j).setFill(Color.BLUE);

                    // Check if new minimum found
                    if (rects.get(j).getHeight() < rects.get(i).getHeight()) {
                        rects.get(j).setFill(Color.ORCHID);
                        int temp = i;
                        i = j;
                        j = temp;
                        state = 2; // Need to swap
                    } else {
                        j++;
                        if (j >= arraySize) {
                            // Element at position i is in its final place
                            rects.get(i).setFill(Color.GREEN);
                            // Reset for next iteration
                            i++;
                            j = i + 1;
                            if (j >= arraySize) {
                                state = 3; // Done
                            }
                        }
                    }
                    break;

                case 2: // Swap
                    // Swap heights
                    double tempHeight = rects.get(i).getHeight();
                    rects.get(i).setHeight(rects.get(j).getHeight());
                    rects.get(j).setHeight(tempHeight);

                    // Swap back indices
                    int temp = i;
                    i = j;
                    j = temp;

                    // Move to next element
                    j++;
                    if (j >= arraySize) {
                        // Element at position i is in its final place
                        rects.get(i).setFill(Color.GREEN);
                        // Reset for next iteration
                        i++;
                        j = i + 1;
                        if (j >= arraySize) {
                            state = 3; // Done
                        }
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

    private void insertionSort() {
        timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 1; // Start with second element
                    j = i;
                    state = 1;
                    break;

                case 1: // Process element
                    if (i >= arraySize) {
                        state = 3; // Done
                        break;
                    }

                    // Reset colors
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN); // Already sorted
                        } else {
                            rects.get(k).setFill(Color.RED);
                        }
                    }

                    // Current element to insert
                    rects.get(j).setFill(Color.BLUE);

                    // Check if element needs to be moved
                    if (j > 0 && rects.get(j - 1).getHeight() > rects.get(j).getHeight()) {
                        state = 2; // Need to swap
                    } else {
                        i++; // Move to next element
                        j = i;
                        if (i >= arraySize) {
                            state = 3; // Done
                        }
                    }
                    break;

                case 2: // Swap
                    // Swap heights
                    double tempHeight = rects.get(j).getHeight();
                    rects.get(j).setHeight(rects.get(j - 1).getHeight());
                    rects.get(j - 1).setHeight(tempHeight);

                    // Move left
                    j--;

                    // If reached beginning or correct position
                    if (j == 0 || rects.get(j - 1).getHeight() <= rects.get(j).getHeight()) {
                        i++; // Move to next element
                        j = i;
                        if (i >= arraySize) {
                            state = 3; // Done
                        } else {
                            state = 1;
                        }
                    }
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
        double paneWidth = visualizationPane.getWidth();
        if (paneWidth == 0) {
            // If the pane width is not yet determined, use the parent container's width
            paneWidth = visualizationPane.getParent().getBoundsInLocal().getWidth();
        }

        double totalWidth = rects.size() * (W + gap) - gap; // Total width of all rectangles including gaps
        double startX = Math.max(0, (paneWidth - totalWidth) / 2); // Calculate starting X position to center the array

        for (int i = 0; i < rects.size(); i++) {
            Rectangle rect = rects.get(i);
            rect.setX(startX + i * (W + gap)); // Set X position for each rectangle
            rect.setY(50); // Fixed Y position for alignment
        }
    }
}