import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;

public class Controller {

    private AnchorPane visualizationPane;

    @FXML
    private ChoiceBox<String> Algorithm;
    
    @FXML
    private Slider speedSlider;

    private String currentAlgorithm = "Bubble Sort";
    
    // Timeline duration in milliseconds
    private int timelineDuration = 200;

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
        
        // Initialize the speed slider
        initSpeedSlider();

        randomizer(new ActionEvent()); // Initialize with random rectangles
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

        // Add listener for algorithm selection changes
        Algorithm.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentAlgorithm = newVal;
            }
        });
    }

    private void initSpeedSlider() {
        // Set initial value
        speedSlider.setValue(timelineDuration);
        
        // Add listener to update animation speed in real-time
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Invert the slider value so higher value = faster animation (lower duration)
            timelineDuration = (int)(1100 - newVal.doubleValue());
            
            // Update timeline if it's running
            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
                updateTimelineSpeed();
            }
        });
    }
    
    private void updateTimelineSpeed() {
        if (timeline == null) return;
        
        // Store the current status
        Timeline.Status status = timeline.getStatus();
        
        // Stop the current timeline
        timeline.stop();
        
        // Restart with new duration based on current algorithm
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
            case "Merge Sort":
                mergeSort();
                break;
            case "Quick Sort":
                quickSort();
                break;
            default:
                bubbleSort();
        }
        
        // Only play if it was playing before
        if (status == Timeline.Status.RUNNING) {
            timeline.play();
        }
    }

    final int N = 15;  // Default size for random generation
    final int W = 15;
    final int gap = 15;
    ArrayList<Rectangle> rects = new ArrayList<>();
    ArrayList<Text> labels = new ArrayList<>();

    private byte state = 0; // 0=init, 1=iterating, 2=swapping, 3=done
    private int i = 0;
    private int j = 0;
    private int k = 0;

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

            if (timeline != null) {
                timeline.stop();
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
            case "Merge Sort":
                mergeSort();
                break;
            case "Quick Sort":
                quickSort();
                break;
            default:
                bubbleSort(); // Default to bubble sort
        }
    }

    private void bubbleSort() {
        timeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), e -> {
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
                    // Swap heights using the new swap method
                    swap(j, j + 1);

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
        timeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), e -> {
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
                    // Swap heights using the new swap method
                    swap(i, j);

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
        timeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), e -> {
            int arraySize = rects.size();

            switch (state) {
                case 0: // Initialize
                    i = 1; // Start with second element
                    j = i;
                    state = 1;
                    
                    // Reset all rectangle colors
                    for (int k = 0; k < arraySize; k++) {
                        if (k < 1) {
                            rects.get(k).setFill(Color.GREEN); // First element is already sorted
                        } else {
                            rects.get(k).setFill(Color.RED); // Unsorted portion
                        }
                    }
                    break;

                case 1: // Process element
                    if (i >= arraySize) {
                        state = 3; // Done
                        break;
                    }

                    // Reset colors to show sorted vs unsorted portions
                    for (int k = 0; k < arraySize; k++) {
                        if (k < i) {
                            rects.get(k).setFill(Color.GREEN); // Already sorted portion
                        } else if (k > i) {
                            rects.get(k).setFill(Color.RED); // Unsorted portion
                        }
                    }

                    // Current key element we're trying to insert
                    rects.get(i).setFill(Color.BLUE);
                    
                    // Set j to current element index to start insertion process
                    j = i;
                    state = 2; // Move to comparison/swap phase
                    break;

                case 2: // Compare and swap if needed
                    // First, reset any other comparison colors
                    for (int k = 0; k < i; k++) {
                        if (k != j && k != j-1) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                    }
                    
                    // Highlight the current element being inserted
                    rects.get(j).setFill(Color.BLUE);
                    
                    // Highlight the element we're comparing with
                    if (j > 0) {
                        rects.get(j-1).setFill(Color.ORCHID);
                    }
                    
                    // Check if element needs to be moved
                    if (j > 0 && rects.get(j - 1).getHeight() > rects.get(j).getHeight()) {
                        // Swap heights using the new swap method
                        swap(j, j - 1);
                        
                        // Move left and continue comparing
                        j--;
                        
                        // Stay in state 2 to continue insertion process
                    } else {
                        // Found correct position
                        // Mark all elements in sorted portion as green
                        for (int k = 0; k <= i; k++) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                        
                        // Move to next element in unsorted portion
                        i++;
                        state = 1;
                        
                        // Check if we're done
                        if (i >= arraySize) {
                            state = 3;
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

    /**
     * Swaps the heights of two rectangles and updates their labels.
     * @param index1 The index of the first rectangle.
     * @param index2 The index of the second rectangle.
     */
    private void swap(int index1, int index2) {
        if (index1 < 0 || index1 >= rects.size() || index2 < 0 || index2 >= rects.size()) {
            return; // Invalid indices
        }
        
        Rectangle rect1 = rects.get(index1);
        Rectangle rect2 = rects.get(index2);
        
        double tempHeight = rect1.getHeight();
        rect1.setHeight(rect2.getHeight());
        rect2.setHeight(tempHeight);
        
        updateLabelPositions(index1, index2);
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
     * Updates the position of labels for specific rectangles after height swap
     * @param indices the indices of rectangles that had their heights swapped
     */
    private void updateLabelPositions(int... indices) {
        for (int idx : indices) {
            if (idx < 0 || idx >= rects.size()) continue; // Skip invalid indices
            
            Rectangle rect = rects.get(idx);
            Text label = labels.get(idx);
            
            // Update label text and position
            label.setText(String.valueOf((int)rect.getHeight()));
            // Recalculate X position to center the label over its rectangle
            label.setX(rect.getX() + W / 2 - label.getBoundsInLocal().getWidth() / 2);
            label.setY(rect.getY() - 5); // Position above the bar
        }
    }

    // Variables for merge sort visualization
    private int[] mergeSortArray;
    private int[] tempArray;
    private int currentSize;    // Current size of subarrays being merged
    private int leftStart;      // Current left starting position
    private int mergeState;     // 0=init, 1=size loop, 2=left loop, 3=merge, 4=done
    private int rightStart;     // Current right starting position 
    private int mergeIndex;     // Current merge index
    private int leftIndex;      // Current left index
    private int rightIndex;     // Current right index
    private int maxSize;        // Maximum merge size
    
    private void mergeSort() {
        int arraySize = rects.size();
        
        // No need to sort if array has 1 or fewer elements
        if (arraySize <= 1) {
            if (arraySize == 1) {
                rects.get(0).setFill(Color.GREEN);
            }
            return;
        }
        
        // Initialize arrays and variables
        mergeSortArray = new int[arraySize];
        tempArray = new int[arraySize];
        
        // Copy rectangle heights to array
        for (int i = 0; i < arraySize; i++) {
            mergeSortArray[i] = (int)rects.get(i).getHeight();
            rects.get(i).setFill(Color.RED);
        }
        
        // Initialize the merge sort state variables
        mergeState = 0;
        currentSize = 1;
        leftStart = 0;
        maxSize = arraySize;
        
        timeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), e -> {
            switch (mergeState) {
                case 0: // Initialize a new size for merging
                    if (currentSize >= maxSize) {
                        // We're done with the entire sort
                        mergeState = 4;
                        break;
                    }
                    
                    // Highlight the current merge size we're working with
                    System.out.println("Merging subarrays of size " + currentSize);
                    
                    // Start with first subarray
                    leftStart = 0;
                    mergeState = 1;
                    break;
                    
                case 1: // Start merging for a specific left position
                    if (leftStart >= maxSize - 1) {
                        // Finished all merges for current size, double the size
                        currentSize = currentSize * 2;
                        mergeState = 0;
                        break;
                    }
                    
                    // Calculate boundaries for this merge operation
                    rightStart = Math.min(leftStart + currentSize, maxSize);
                    int end = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    
                    // Initialize indices for merging
                    leftIndex = leftStart;
                    rightIndex = rightStart;
                    mergeIndex = leftStart;
                    
                    // Highlight the subarrays being merged
                    for (int i = 0; i < maxSize; i++) {
                        if (i >= leftStart && i < rightStart) {
                            rects.get(i).setFill(Color.BLUE); // Left subarray
                        } else if (i >= rightStart && i <= end) {
                            rects.get(i).setFill(Color.ORCHID); // Right subarray
                        } else {
                            // Keep existing colors for other areas
                            if (rects.get(i).getFill() != Color.GREEN) {
                                rects.get(i).setFill(Color.RED);
                            }
                        }
                    }
                    
                    // Copy data to temporary array for merging
                    for (int i = leftStart; i <= end; i++) {
                        tempArray[i] = mergeSortArray[i];
                    }
                    
                    mergeState = 2;
                    break;
                    
                case 2: // Perform one step of the merge
                    int rightEnd = Math.min(leftStart + 2 * currentSize - 1, maxSize - 1);
                    
                    // If we still have elements to merge
                    if (mergeIndex <= rightEnd) {
                        // Choose smallest element from either left or right subarray
                        if (leftIndex < rightStart && 
                           (rightIndex > rightEnd || tempArray[leftIndex] <= tempArray[rightIndex])) {
                            // Take from left subarray
                            mergeSortArray[mergeIndex] = tempArray[leftIndex];
                            
                            // Visual highlighting
                            rects.get(leftIndex).setFill(Color.YELLOW);
                            rects.get(mergeIndex).setHeight(mergeSortArray[mergeIndex]);
                            updateLabelPositions(mergeIndex);
                            
                            leftIndex++;
                        } else {
                            // Take from right subarray
                            mergeSortArray[mergeIndex] = tempArray[rightIndex];
                            
                            // Visual highlighting
                            if (rightIndex <= rightEnd) {
                                rects.get(rightIndex).setFill(Color.YELLOW);
                            }
                            rects.get(mergeIndex).setHeight(mergeSortArray[mergeIndex]);
                            updateLabelPositions(mergeIndex);
                            
                            rightIndex++;
                        }
                        
                        // Mark sorted element
                        rects.get(mergeIndex).setFill(Color.GREEN);
                        
                        mergeIndex++;
                    } else {
                        // This merge is complete, move to next subarray pair
                        leftStart = leftStart + 2 * currentSize;
                        mergeState = 1;
                    }
                    break;
                    
                case 4: // Verify and finalize
                    // Verify the array is sorted
                    boolean sorted = true;
                    for (int i = 1; i < maxSize; i++) {
                        if (mergeSortArray[i - 1] > mergeSortArray[i]) {
                            sorted = false;
                            System.out.println("Array not sorted at index " + i + ": " + 
                                              mergeSortArray[i - 1] + " > " + mergeSortArray[i]);
                            break;
                        }
                    }
                    
                    if (sorted) {
                        // Final completion - mark all sorted
                        for (int i = 0; i < maxSize; i++) {
                            rects.get(i).setFill(Color.GREEN);
                            
                            // Ensure all heights match the sorted array (final check)
                            rects.get(i).setHeight(mergeSortArray[i]);
                            updateLabelPositions(i);
                        }
                        
                        System.out.println("Merge sort complete!");
                        timeline.stop();
                    } else {
                        // Something went wrong - try to fix by doing a final full merge
                        System.out.println("Merge sort incomplete - running final pass");
                        
                        // Reset to a smaller subarray size to ensure we get there
                        currentSize = maxSize / 4;
                        if (currentSize < 1) currentSize = 1;
                        mergeState = 0;
                    }
                    break;
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // Variables for quick sort visualization
    private int[] quickSortArray;
    private int quickSortState = 0;
    private ArrayList<int[]> quickSortStack = new ArrayList<>();
    private int pivotIndex = 0;
    private int partitionLeft = 0, partitionRight = 0;
    private int partitionI = 0, partitionJ = 0;
    private boolean partitioningInProgress = false;
    
    private void quickSort() {
        int arraySize = rects.size();
        
        // Initialize an array to track the current state of quick sort
        quickSortArray = new int[arraySize];
        
        // Reset state variables
        quickSortStack.clear();
        quickSortState = 0;
        partitioningInProgress = false;
        
        // Copy heights to quickSortArray and set initial color to red (unsorted)
        for (int k = 0; k < arraySize; k++) {
            rects.get(k).setFill(Color.RED);
            quickSortArray[k] = (int) rects.get(k).getHeight();
        }
        
        // Add initial range (full array)
        quickSortStack.add(new int[]{0, arraySize - 1});
        
        timeline = new Timeline(new KeyFrame(Duration.millis(timelineDuration), e -> {
            switch (quickSortState) {
                case 0: // Get next subarray to partition
                    if (quickSortStack.isEmpty()) {
                        quickSortState = 3; // Done
                        break;
                    }
                    
                    // Get next range to process
                    int[] range = quickSortStack.remove(0);
                    partitionLeft = range[0];
                    partitionRight = range[1];
                    
                    // If the range is invalid or contains one element, it's already sorted
                    if (partitionLeft >= partitionRight) {
                        if (partitionLeft == partitionRight) {
                            rects.get(partitionLeft).setFill(Color.GREEN); // Single element is sorted
                        }
                        break;
                    }
                    
                    // Reset colors to show the current range being processed
                    for (int k = 0; k < arraySize; k++) {
                        if (k >= partitionLeft && k <= partitionRight) {
                            rects.get(k).setFill(Color.BLUE); // Current partition
                        } else {
                            if (rects.get(k).getFill() != Color.GREEN) {
                                rects.get(k).setFill(Color.RED); // Unsorted or waiting to be processed
                            }
                        }
                    }
                    
                    // Choose the rightmost element as pivot
                    pivotIndex = partitionRight;
                    rects.get(pivotIndex).setFill(Color.ORCHID); // Highlight pivot
                    
                    // Initialize partition pointers
                    partitionI = partitionLeft - 1;
                    partitionJ = partitionLeft;
                    partitioningInProgress = true;
                    
                    quickSortState = 1; // Move to partitioning
                    break;
                    
                case 1: // Partitioning in progress
                    // If done with partitioning
                    if (!partitioningInProgress || partitionJ >= partitionRight) {
                        // Swap pivot with position i+1
                        partitionI++;
                        
                        // Visualize the swap
                        rects.get(partitionI).setFill(Color.YELLOW);
                        rects.get(pivotIndex).setFill(Color.YELLOW);
                        
                        // Swap elements
                        int temp = quickSortArray[partitionI];
                        quickSortArray[partitionI] = quickSortArray[pivotIndex];
                        quickSortArray[pivotIndex] = temp;
                        
                        // Update visualization
                        rects.get(partitionI).setHeight(quickSortArray[partitionI]);
                        rects.get(pivotIndex).setHeight(quickSortArray[pivotIndex]);
                        updateLabelPositions(partitionI, pivotIndex);
                        
                        // Mark pivot in final position
                        rects.get(partitionI).setFill(Color.GREEN);
                        
                        // Add subarrays to the stack for further processing
                        if (partitionLeft < partitionI - 1) {
                            quickSortStack.add(0, new int[]{partitionLeft, partitionI - 1}); // Left side
                        } else if (partitionLeft == partitionI - 1) {
                            // Single element on left is sorted
                            rects.get(partitionLeft).setFill(Color.GREEN);
                        }
                        
                        if (partitionI + 1 < partitionRight) {
                            quickSortStack.add(0, new int[]{partitionI + 1, partitionRight}); // Right side
                        } else if (partitionI + 1 == partitionRight) {
                            // Single element on right is sorted
                            rects.get(partitionRight).setFill(Color.GREEN);
                        }
                        
                        partitioningInProgress = false;
                        quickSortState = 0; // Back to getting next subarray
                        break;
                    }
                    
                    // Highlight current comparison
                    for (int k = partitionLeft; k <= partitionRight; k++) {
                        if (k == pivotIndex) {
                            rects.get(k).setFill(Color.ORCHID); // Pivot
                        } else if (k == partitionJ) {
                            rects.get(k).setFill(Color.YELLOW); // Current element
                        } else if (k <= partitionI) {
                            rects.get(k).setFill(Color.BLUE); // Elements <= pivot
                        } else {
                            rects.get(k).setFill(Color.RED); // Elements > pivot
                        }
                    }
                    
                    // Compare current element with pivot
                    if (quickSortArray[partitionJ] < quickSortArray[pivotIndex]) {
                        // Increment i and swap elements
                        partitionI++;
                        
                        // Visualize the swap
                        rects.get(partitionI).setFill(Color.YELLOW);
                        rects.get(partitionJ).setFill(Color.YELLOW);
                        
                        // Swap elements if they're different
                        if (partitionI != partitionJ) {
                            int temp = quickSortArray[partitionI];
                            quickSortArray[partitionI] = quickSortArray[partitionJ];
                            quickSortArray[partitionJ] = temp;
                            
                            // Update visualization
                            rects.get(partitionI).setHeight(quickSortArray[partitionI]);
                            rects.get(partitionJ).setHeight(quickSortArray[partitionJ]);
                            updateLabelPositions(partitionI, partitionJ);
                        }
                    }
                    
                    // Move to next element
                    partitionJ++;
                    
                    break;
                    
                case 3: // Done
                    // Check if array is sorted
                    boolean sorted = true;
                    for (int k = 1; k < arraySize; k++) {
                        if (quickSortArray[k - 1] > quickSortArray[k]) {
                            sorted = false;
                            break;
                        }
                    }
                    
                    if (sorted) {
                        // Set all to green when sorted
                        for (int k = 0; k < arraySize; k++) {
                            rects.get(k).setFill(Color.GREEN);
                        }
                        timeline.stop();
                    }
                    break;
            }
        }));
        
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
}