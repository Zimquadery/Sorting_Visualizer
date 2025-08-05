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
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

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

    
    private int timelineDuration = 200;
    
    private SortingAlgorithms sortingAlgorithms;

    @FXML
    private void handleViewDetails(ActionEvent event) {
        String algo = currentAlgorithm;
        String details = AlgorithmDescription.DETAILS.getOrDefault(algo, "No details available.");
        Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(algo + " Details");
        alert.setHeaderText(algo + " Details");
        TextArea textArea = new TextArea(details);
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

        visualizationPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (!rects.isEmpty()) {
                centerRectangles();
            }
        });

        initAlgorithmChoiceBox();
        
        initSpeedSlider();

        randomizer(new ActionEvent());
        updateLegend(currentAlgorithm);
    }

    private void initAlgorithmChoiceBox() {
        Algorithm.setItems(FXCollections.observableArrayList(
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort",
            "Quick Sort"
        ));

        Algorithm.setValue("Bubble Sort");
        updateComplexityLabel("Bubble Sort");

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
        speedSlider.setValue(1100 - timelineDuration);
        
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            timelineDuration = (int)(1100 - newVal.doubleValue());
            
            if (sortingAlgorithms != null && sortingAlgorithms.getTimeline() != null && 
                sortingAlgorithms.getTimeline().getStatus() == Timeline.Status.RUNNING) {
                updateTimelineSpeed();
            }
        });
    }
    
    private void updateTimelineSpeed() {
        if (sortingAlgorithms != null) {
            sortingAlgorithms.updateSpeed(timelineDuration);
        }
    }

    final int N = 15;  
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

        if (comparisonsLabel != null && swapsLabel != null) {
            comparisonsLabel.setText("0");
            swapsLabel.setText("0");
        }

        visualizationPane.getChildren().removeAll(rects);
        visualizationPane.getChildren().removeAll(labels);
        rects.clear();
        labels.clear();

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

        centerRectangles();
        visualizationPane.getChildren().addAll(rects);
        visualizationPane.getChildren().addAll(labels);
    }

    @FXML
    void handleCustomArray(ActionEvent event) {
        if (!customInputMode) {
            customInputMode = true;
            customArrayBtn.setText("Apply");
            customArrayField.setVisible(true);
            rndBtn.setDisable(true);
            sortBtn.setDisable(true);
        } else {
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
            String[] values = input.split(",");

            if (sortingAlgorithms != null) {
                sortingAlgorithms.stopAnimation();
            }

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

            centerRectangles();
            visualizationPane.getChildren().addAll(rects);
            visualizationPane.getChildren().addAll(labels);
            customArrayField.clear();

        } catch (NumberFormatException e) {
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
        if (sortingAlgorithms != null) {
            sortingAlgorithms.stopAnimation();
        }

        if (rects.isEmpty()) {
            return;
        }

        if (comparisonsLabel != null && swapsLabel != null) {
            comparisonsLabel.setText("0");
            swapsLabel.setText("0");
        }

        sortingAlgorithms = new SortingAlgorithms(rects, labels, timelineDuration);

        Timeline timeline = null;
        
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
        
        if (timeline != null) {
            Timeline performanceUpdateTimeline = new Timeline(
                new KeyFrame(javafx.util.Duration.millis(50), e -> updatePerformanceLabel())
            );
            performanceUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
            
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
            paneWidth = visualizationPane.getParent().getBoundsInLocal().getWidth();
        }

        double totalWidth = rects.size() * (W + gap) - gap;
        double startX = Math.max(0, (paneWidth - totalWidth) / 2);

        for (int i = 0; i < rects.size(); i++) {
            Rectangle rect = rects.get(i);
            rect.setX(startX + i * (W + gap));
            rect.setY(50);

            Text label = labels.get(i);
            label.setText(String.valueOf((int)rect.getHeight()));
            label.setX(rect.getX() + W / 2 - label.getBoundsInLocal().getWidth() / 2);
            label.setY(rect.getY() - 5);
        }
    }
    
    private void updateLegend(String algorithm) {
        if (legendBox == null) return;
        legendBox.getChildren().clear();
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
