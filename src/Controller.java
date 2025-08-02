import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.ArrayList;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;

public class Controller {

    private AnchorPane visualizationPane;
    @FXML
    private HBox legendBox;

    @FXML
    private ChoiceBox<String> Algorithm;
    
    @FXML
    private Slider speedSlider;

    private String currentAlgorithm = "Bubble Sort";
    @FXML
    private javafx.scene.control.Label complexityLabel;

    @FXML
    private javafx.scene.control.Label performanceLabel;

    @FXML
    private javafx.scene.control.Label comparisonsLabel;

    @FXML
    private javafx.scene.control.Label swapsLabel;

    @FXML
    private Button viewDetailsBtn;

    
    // Timeline duration in milliseconds
    private int timelineDuration = 200;
    
    // Sorting algorithms instance
    private SortingAlgorithms sortingAlgorithms;

    // Show details popup for selected algorithm
    @FXML
    private void handleViewDetails(ActionEvent event) {
        String algo = currentAlgorithm;
        String details = AlgorithmDescription.DETAILS.getOrDefault(algo, "No details available.");
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(algo + " Details");
        alert.setHeaderText(algo + " Details");
        javafx.scene.control.TextArea textArea = new javafx.scene.control.TextArea(details);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setPrefWidth(800);
        textArea.setPrefHeight(600);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setStyle("-fx-font-size: 18px;");
        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(850);
        alert.getDialogPane().setPrefHeight(700);
        alert.showAndWait();
    }

    void setRoot(AnchorPane root) {
        this.visualizationPane = (AnchorPane) root.lookup("#visualizationPane");
        this.legendBox = (HBox) root.lookup("#legendBox");

        // Add listener to reposition rectangles when the pane size changes
        visualizationPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!rects.isEmpty()) {
                centerRectangles();
            }
        });

        // Initialize the algorithm choice box
        initAlgorithmChoiceBox();
        
        // Initialize the speed slider
        initSpeedSlider();

        randomizer(new ActionEvent()); // Initialize with random rectangles
        updateLegend(currentAlgorithm); // Set initial legend
    }

    private void initAlgorithmChoiceBox() {
        // Initialize algorithm choices
        Algorithm.setItems(FXCollections.observableArrayList(
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort",
            "Quick Sort"
        ));

        // Set default value
        Algorithm.setValue("Bubble Sort");
        updateComplexityLabel("Bubble Sort");

        // Add listener for algorithm selection changes
        Algorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentAlgorithm = newVal;
                updateComplexityLabel(newVal);
                updateLegend(newVal);
            }
        });


    }

    private void updateComplexityLabel(String algorithm) {
        String complexity = SortingAlgorithms.getComplexity(algorithm);
        if (complexityLabel != null) {
            complexityLabel.setText("Complexity:  " + complexity);
        }
    }

    private void updatePerformanceLabel() {
        if (comparisonsLabel != null && swapsLabel != null && sortingAlgorithms != null) {
            int comparisons = sortingAlgorithms.getComparisons();
            int swaps = sortingAlgorithms.getSwaps();
            comparisonsLabel.setText(String.valueOf(comparisons));
            swapsLabel.setText(String.valueOf(swaps));
        }
    }

    private void initSpeedSlider() {
        // Set initial value (inverted to match the speed calculation)
        speedSlider.setValue(1100 - timelineDuration);
        
        // Add listener to update animation speed in real-time
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Invert the slider value so higher value = faster animation (lower duration)
            timelineDuration = (int)(1100 - newVal.doubleValue());
            
            // Update timeline if it's running
            if (sortingAlgorithms != null && sortingAlgorithms.getTimeline() != null && 
                sortingAlgorithms.getTimeline().getStatus() == Timeline.Status.RUNNING) {
                updateTimelineSpeed();
            }
        });
    }
    
    private void updateTimelineSpeed() {
        if (sortingAlgorithms != null) {
            // Simply update the speed without restarting the animation
            sortingAlgorithms.updateSpeed(timelineDuration);
        }
    }

    final int N = 15;  // Default size for random generation
    final int W = 15;
    final int gap = 15;
    ArrayList<Rectangle> rects = new ArrayList<>();
    ArrayList<Text> labels = new ArrayList<>();

    @FXML
    private Button rndBtn;

    @FXML
    private Button sortBtn;

    @FXML
    private Button customArrayBtn;

    @FXML
    private TextField customArrayField;

    private boolean customInputMode = false;

    @FXML
    void randomizer(ActionEvent event) {
        if (sortingAlgorithms != null) {
            sortingAlgorithms.stopAnimation();
        }

        // Reset performance label
        if (comparisonsLabel != null && swapsLabel != null) {
            comparisonsLabel.setText("0");
            swapsLabel.setText("0");
        }

        visualizationPane.getChildren().removeAll(rects);
        visualizationPane.getChildren().removeAll(labels);
        rects.clear();
        labels.clear();

        // Generate random rectangles
        for (int i = 0; i < N; i++) {
            Rectangle rect = new Rectangle();
            rect.setWidth(W);
            rect.setHeight(Math.random() * 250);
            rect.setFill(Color.RED);
            rects.add(rect);

            Text label = new Text(String.valueOf((int) rect.getHeight()));
            label.getStyleClass().add("bar-label");
            labels.add(label);
        }

        // Center the rectangles in the AnchorPane
        centerRectangles();
        visualizationPane.getChildren().addAll(rects);
        visualizationPane.getChildren().addAll(labels);
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

            if (sortingAlgorithms != null) {
                sortingAlgorithms.stopAnimation();
            }

            // Reset performance label
            if (comparisonsLabel != null && swapsLabel != null) {
                comparisonsLabel.setText("0");
                swapsLabel.setText("0");
            }

            visualizationPane.getChildren().removeAll(rects);
            visualizationPane.getChildren().removeAll(labels);
            rects.clear();
            labels.clear();

            for (String value : values) {
                double height = Double.parseDouble(value.trim());
                Rectangle rect = new Rectangle();
                rect.setWidth(W);
                rect.setHeight(height);
                rect.setFill(Color.RED);
                rects.add(rect);
                
                Text label = new Text(String.valueOf((int) height));
                label.getStyleClass().add("bar-label");
                labels.add(label);
            }

            // Center the rectangles in the AnchorPane
            centerRectangles();
            visualizationPane.getChildren().addAll(rects);
            visualizationPane.getChildren().addAll(labels);
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
        // Stop any existing animation
        if (sortingAlgorithms != null) {
            sortingAlgorithms.stopAnimation();
        }

        // Exit if there are no rectangles to sort
        if (rects.isEmpty()) {
            return;
        }

        // Reset performance label
        if (comparisonsLabel != null && swapsLabel != null) {
            comparisonsLabel.setText("0");
            swapsLabel.setText("0");
        }

        // Create new sorting algorithms instance with current parameters
        sortingAlgorithms = new SortingAlgorithms(rects, labels, timelineDuration);

        Timeline timeline = null;
        
        // Use different sorting algorithms based on selection
        switch (currentAlgorithm) {
            case "Bubble Sort":
                timeline = sortingAlgorithms.bubbleSort();
                break;
            case "Selection Sort":
                timeline = sortingAlgorithms.selectionSort();
                break;
            case "Insertion Sort":
                timeline = sortingAlgorithms.insertionSort();
                break;
            case "Merge Sort":
                timeline = sortingAlgorithms.mergeSort();
                break;
            case "Quick Sort":
                timeline = sortingAlgorithms.quickSort();
                break;
            default:
                timeline = sortingAlgorithms.bubbleSort(); // Default to bubble sort
        }
        
        // Start the animation if timeline was created
        if (timeline != null) {
            // Create a separate timeline to update performance label regularly
            Timeline performanceUpdateTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(50), e -> updatePerformanceLabel())
            );
            performanceUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
            
            // Stop performance update timeline when sorting is complete
            timeline.setOnFinished(e -> {
                performanceUpdateTimeline.stop();
                updatePerformanceLabel(); // Final update
            });
            
            timeline.play();
            performanceUpdateTimeline.play();
        }
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

            // Position the text label at the top of the rectangle
            Text label = labels.get(i);
            label.setText(String.valueOf((int)rect.getHeight()));
            label.setX(rect.getX() + W / 2 - label.getBoundsInLocal().getWidth() / 2);
            label.setY(rect.getY() - 5); // Position above the bar
        }
    }
    /**
     * Updates the color legend at the bottom of the window based on the selected algorithm.
     */
    private void updateLegend(String algorithm) {
        if (legendBox == null) return;
        legendBox.getChildren().clear();

        // Helper to create a legend item
        java.util.function.BiFunction<Color, String, HBox> legendItem = (color, text) -> {
            Rectangle rect = new Rectangle(20, 20, color);
            Label label = new Label(text);
            HBox box = new HBox(5, rect, label);
            box.setAlignment(Pos.CENTER_LEFT);
            return box;
        };

        switch (algorithm) {
            case "Bubble Sort":
                legendBox.getChildren().addAll(
                    legendItem.apply(Color.RED, "Unsorted"),
                    legendItem.apply(Color.BLUE, "Comparing"),
                    legendItem.apply(Color.GREEN, "Sorted")
                );
                break;
            case "Selection Sort":
                legendBox.getChildren().addAll(
                    legendItem.apply(Color.RED, "Unsorted"),
                    legendItem.apply(Color.BLUE, "Current/Min"),
                    legendItem.apply(Color.ORCHID, "New Min"),
                    legendItem.apply(Color.GREEN, "Sorted")
                );
                break;
            case "Insertion Sort":
                legendBox.getChildren().addAll(
                    legendItem.apply(Color.RED, "Unsorted"),
                    legendItem.apply(Color.GREEN, "Sorted"),
                    legendItem.apply(Color.BLUE, "Key/Insert"),
                    legendItem.apply(Color.ORCHID, "Comparing")
                );
                break;
            case "Merge Sort":
                legendBox.getChildren().addAll(
                    legendItem.apply(Color.RED, "Unsorted"),
                    legendItem.apply(Color.BLUE, "Left Subarray"),
                    legendItem.apply(Color.ORCHID, "Right Subarray"),
                    legendItem.apply(Color.GREEN, "Merged/Sorted")
                );
                break;
            case "Quick Sort":
                legendBox.getChildren().addAll(
                    legendItem.apply(Color.RED, "Unsorted/Partition"),
                    legendItem.apply(Color.BLUE, "Comparing"),
                    legendItem.apply(Color.ORCHID, "Pivot"),
                    legendItem.apply(Color.GREEN, "Sorted")
                );
                break;
            default:
                legendBox.getChildren().add(legendItem.apply(Color.GRAY, "State"));
        }
    }
}
